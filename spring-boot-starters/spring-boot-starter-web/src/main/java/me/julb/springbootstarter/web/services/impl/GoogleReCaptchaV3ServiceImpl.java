/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package me.julb.springbootstarter.web.services.impl;

import java.net.URI;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.web.configurations.beans.GoogleReCaptchaProperties;
import me.julb.springbootstarter.web.services.CaptchaService;
import me.julb.springbootstarter.web.services.dto.GoogleReCaptchaV3ChallengeResponseDTO;
import me.julb.springbootstarter.web.utility.HttpServletRequestUtility;

/**
 * The Captcha service implementation for Google ReCaptcha V3.
 * <P>
 * @author Julb.
 */
@Service
@Slf4j
@Validated
@ConditionalOnBean(GoogleReCaptchaProperties.class)
public class GoogleReCaptchaV3ServiceImpl implements CaptchaService {

    /**
     * The Google Recaptcha URL template.
     */
    protected static final String GOOGLE_RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    /**
     * The pattern used to validate the Google Recaptcha token.
     */
    private static final Pattern GOOGLE_RECAPTCHA_TOKEN_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    /**
     * The pattern used to validate the Google Recaptcha action.
     */
    private static final Pattern GOOGLE_RECAPTCHA_ACTION_PATTERN = Pattern.compile("^[A-Z0-9_-]+$");

    /**
     * The Google ReCaptcha properties.
     */
    @Autowired
    private GoogleReCaptchaProperties googleReCaptchaProperties;

    /**
     * The rest template to invoke Google service.
     */
    @Autowired
    protected RestTemplate googleReCaptchaRestTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean validate(@NotNull @NonNull HttpServletRequest httpServletRequest) {
        LOGGER.debug("Verifying the provided captcha token.");

        // Get IP address.
        String ipAddress = HttpServletRequestUtility.getUserIpv4Address(httpServletRequest);

        // Get token.
        String captchaToken = httpServletRequest.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN);
        if (StringUtils.isBlank(captchaToken)) {
            LOGGER.debug("Token to validate not found within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN);
            return false;
        }
        if (!GOOGLE_RECAPTCHA_TOKEN_PATTERN.matcher(captchaToken).matches()) {
            LOGGER.debug("Token to validate has an invalid format within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN);
            return false;
        }

        // Get action.
        String captchaAction = httpServletRequest.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION);
        if (StringUtils.isBlank(captchaAction)) {
            LOGGER.debug("Action to validate not found within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION);
            return false;
        }
        if (!GOOGLE_RECAPTCHA_ACTION_PATTERN.matcher(captchaAction).matches()) {
            LOGGER.debug("Action to validate has an invalid format within the request header: {}.", CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION);
            return false;
        }

        // Check if action is configured.
        if (!googleReCaptchaProperties.getActionThresholds().containsKey(captchaAction)) {
            LOGGER.debug("Action to validate is not configured with a threshold: {}.", captchaAction);
            return false;
        }
        Float actionThreshold = googleReCaptchaProperties.getActionThresholds().get(captchaAction);

        // Invoke Google service.
        GoogleReCaptchaV3ChallengeResponseDTO googleResponse = verifyToken(captchaToken, ipAddress);
        if (!googleResponse.isSuccess()) {
            LOGGER.debug("Google ReCaptcha Service failed to verify the Captcha. Errors: {}", ArrayUtils.toString(googleResponse.getErrorCodes()));
            return false;
        }

        if (!StringUtils.equalsIgnoreCase(googleResponse.getAction(), captchaAction)) {
            LOGGER.debug("Google ReCaptcha Service verified successfully the Captcha but actions don't match. Google: {} vs Actual: {}.", googleResponse.getAction(), captchaAction);
            return false;
        }

        if (googleResponse.getScore() < actionThreshold) {
            LOGGER.debug("Google ReCaptcha Service verified successfully but scoring is not good. Google: {}, Actual threshold: {}.", googleResponse.getScore(), actionThreshold);
            return false;
        }

        return true;
    }

    /**
     * Verifies the captcha using Google service.
     * @param captchaToken the captcha token.
     * @param ipAddress the ip address.
     * @return the response.
     */
    private GoogleReCaptchaV3ChallengeResponseDTO verifyToken(String captchaToken, String ipAddress) {
        URI verifyUri = URI.create(String.format(GOOGLE_RECAPTCHA_URL_TEMPLATE, googleReCaptchaProperties.getSecretKey(), captchaToken, ipAddress));
        try {
            return googleReCaptchaRestTemplate.getForObject(verifyUri, GoogleReCaptchaV3ChallengeResponseDTO.class);
        } catch (RestClientException e) {
            LOGGER.error("Failed to join the Google ReCaptcha service.", e);
            throw new InternalServerErrorException(e);
        }
    }

}
