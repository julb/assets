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

package me.julb.springbootstarter.googlerecaptcha.repositories;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import me.julb.library.utility.validator.constraints.GoogleReCaptchaToken;
import me.julb.library.utility.validator.constraints.IPAddress;
import me.julb.springbootstarter.googlerecaptcha.repositories.impl.GoogleReCaptchaV3ChallengeResponseDTO;

import reactor.core.publisher.Mono;

/**
 * The Google ReCaptcha v3 repository.
 * <br>
 *
 * @author Julb.
 */
public interface GoogleReCaptchaV3Repository {
    /**
     * Triggers a challenge to verify captcha.
     * @param secret the Google recaptcha secret.
     * @param captchaToken the token to verify.
     * @param remoteIp the remote IP address.
     * @return the challenge response.
     */
    Mono<GoogleReCaptchaV3ChallengeResponseDTO> verify(@NotNull @NotBlank String secret, @NotNull @NotBlank @GoogleReCaptchaToken String captchaToken, @IPAddress String remoteIp);
}
