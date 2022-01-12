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

package me.julb.springbootstarter.googlerecaptcha.repositories.impl;

import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import me.julb.springbootstarter.consumer.reactive.decoders.WebClientExceptionConverter;
import me.julb.springbootstarter.googlerecaptcha.annotations.ConditionalOnGoogleReCaptchaEnabled;
import me.julb.springbootstarter.googlerecaptcha.repositories.GoogleReCaptchaV3Repository;

import reactor.core.publisher.Mono;

/**
 * The Google ReCaptcha V3 repository.
 * <br>
 *
 * @author Julb.
 */
@Component
@Validated
@ConditionalOnGoogleReCaptchaEnabled
public class GoogleReCaptchaV3RepositoryImpl implements GoogleReCaptchaV3Repository {

    /**
     * The rest template to invoke Google ReCaptcha V3 service.
     */
    @Autowired
    protected WebClient googleReCaptchaV3WebClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<GoogleReCaptchaV3ChallengeResponseDTO> verify(@NotNull @NotBlank String secret, @NotNull @NotBlank String captchaToken, String remoteIp) {
        //@formatter:off
        return googleReCaptchaV3WebClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/siteverify")
                .queryParam("secret", secret)
                .queryParam("response", captchaToken)
                .queryParamIfPresent("remoteip", Optional.ofNullable(remoteIp))
                .build()
            )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(GoogleReCaptchaV3ChallengeResponseDTO.class)
            .doOnError(WebClientExceptionConverter::convert);
        //@formatter:on
    }
}
