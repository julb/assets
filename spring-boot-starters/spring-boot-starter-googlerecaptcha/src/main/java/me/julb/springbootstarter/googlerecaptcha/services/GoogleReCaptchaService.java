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

package me.julb.springbootstarter.googlerecaptcha.services;

import javax.validation.constraints.NotNull;

import me.julb.library.utility.validator.constraints.GoogleReCaptchaAction;
import me.julb.library.utility.validator.constraints.GoogleReCaptchaToken;
import me.julb.library.utility.validator.constraints.IPAddress;

import reactor.core.publisher.Mono;

/**
 * The google re-captcha service.
 * <br>
 * @author Julb.
 */
public interface GoogleReCaptchaService {

    /**
     * Validates the given captcha.
     * @param captchaToken the received captcha token.
     * @param captchaAction the received captcha action.
     * @param ipAddress the client ip address.
     * @return <code>true</code> if the captcha is valid, <code>false</code> otherwise.
     */
    Mono<Boolean> validate(@NotNull @GoogleReCaptchaToken String captchaToken, @NotNull @GoogleReCaptchaAction String captchaAction, @IPAddress String ipAddress);

}
