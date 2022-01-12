/**
 * MIT License
 *
 * Copyright (c) 2017-2021 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.julb.springbootstarter.googlerecaptcha.services.impl;

import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.library.utility.validator.constraints.GoogleReCaptchaAction;
import me.julb.library.utility.validator.constraints.GoogleReCaptchaToken;
import me.julb.library.utility.validator.constraints.IPAddress;
import me.julb.springbootstarter.googlerecaptcha.annotations.ConditionalOnGoogleReCaptchaEnabled;
import me.julb.springbootstarter.googlerecaptcha.configurations.beans.GoogleReCaptchaProperties;
import me.julb.springbootstarter.googlerecaptcha.repositories.GoogleReCaptchaV3Repository;
import me.julb.springbootstarter.googlerecaptcha.services.GoogleReCaptchaService;

import reactor.core.publisher.Mono;

/**
 * The Captcha service implementation for Google ReCaptcha V3.
 * <br>
 * @author Julb.
 */
@Slf4j
@Validated
@Service
@ConditionalOnGoogleReCaptchaEnabled
public class GoogleReCaptchaV3ServiceImpl implements GoogleReCaptchaService {

    /**
     * The Google ReCaptcha properties.
     */
    @Autowired
    private GoogleReCaptchaProperties googleReCaptchaProperties;

    /**
     * The HTTP client to verify captcha.
     */
    @Autowired
    protected GoogleReCaptchaV3Repository googleReCaptchaV3Repository;

    /**
     * {@inheritDoc}
     */
   /* @Override
    public Mono<Boolean> validate(@NotNull ServerWebExchange exchange) {
        LOGGER.debug("Verifying the provided captcha token.");

        // Get IP address.
        String ipAddress = getUserIpAddress(exchange.getRequest());

        // Get token.
        String captchaToken = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN);
        if (StringUtils.isBlank(captchaToken)) {
            LOGGER.debug("Token to validate not found within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN);
            return Mono.just(false);
        }
        if (!GOOGLE_RECAPTCHA_TOKEN_PATTERN.matcher(captchaToken).matches()) {
            LOGGER.debug("Token to validate has an invalid format within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN);
            return Mono.just(false);
        }

        // Get action.
        String captchaAction = exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION);
        if (StringUtils.isBlank(captchaAction)) {
            LOGGER.debug("Action to validate not found within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION);
            return Mono.just(false);
        }
        if (!GOOGLE_RECAPTCHA_ACTION_PATTERN.matcher(captchaAction).matches()) {
            LOGGER.debug("Action to validate has an invalid format within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION);
            return Mono.just(false);
        }

        // Invoke Google service.
        return validate(captchaToken, captchaAction, ipAddress);
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> validate(@NotNull @GoogleReCaptchaToken String captchaToken, @NotNull  @GoogleReCaptchaAction String captchaAction, @IPAddress String ipAddress) {
        LOGGER.debug("Verifying the provided captcha token.");

        // Check if action is configured.
        if (!googleReCaptchaProperties.getActionThresholds().containsKey(captchaAction)) {
            LOGGER.debug("Action to validate is not configured with a threshold: {}.", captchaAction);
            return Mono.just(false);
        }

        // Get the action threshold.
        Float actionThreshold = googleReCaptchaProperties.getActionThresholds().get(captchaAction);

        // Invoke Google service.
        return googleReCaptchaV3Repository.verify(googleReCaptchaProperties.getSecretKey(), captchaToken, ipAddress).map(googleResponse -> {
            if (!googleResponse.isSuccess()) {
                LOGGER.debug("Google ReCaptcha Service failed to verify the Captcha. Errors: {}", ArrayUtils.toString(googleResponse.getErrorCodes()));
                return false;
            }
            LOGGER.debug("Google stated that the Captcha verification is successful.");
    
            if (googleResponse.getAction() != null && !StringUtils.equalsIgnoreCase(googleResponse.getAction(), captchaAction)) {
                LOGGER.debug("Google ReCaptcha Service verified successfully the Captcha but actions don't match. Google: {} vs Actual: {}.", googleResponse.getAction(), captchaAction);
                return false;
            }
            LOGGER.debug("Action passed as parameter and action returned by Google are matching.");
    
            if (googleResponse.getScore() < actionThreshold) {
                LOGGER.debug("Google ReCaptcha Service verified successfully but scoring is not good. Google: {}, Actual threshold: {}.", googleResponse.getScore(), actionThreshold);
                return false;
            }
    
            LOGGER.debug("Captcha verified successfully.");
    
            return true;
        });
    }
}
