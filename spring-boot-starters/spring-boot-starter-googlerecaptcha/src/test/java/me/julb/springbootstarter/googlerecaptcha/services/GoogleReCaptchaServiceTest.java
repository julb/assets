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

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import me.julb.springbootstarter.googlerecaptcha.configurations.GoogleReCaptchaConfiguration;
import me.julb.springbootstarter.googlerecaptcha.repositories.GoogleReCaptchaV3Repository;
import me.julb.springbootstarter.googlerecaptcha.repositories.impl.GoogleReCaptchaV3ChallengeResponseDTO;
import me.julb.springbootstarter.googlerecaptcha.services.impl.GoogleReCaptchaV3ServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

import reactor.core.publisher.Mono;

/**
 * Test class for {@link GoogleReCaptchaV3ServiceImpl} class.
 * <br>
 * @author Julb.
 */
@ContextConfiguration(classes = {GoogleReCaptchaConfiguration.class, GoogleReCaptchaV3ServiceImpl.class, ValidationAutoConfiguration.class})
public class GoogleReCaptchaServiceTest extends AbstractBaseTest {

    /**
     * The captcha service.
     */
    @Autowired
    private GoogleReCaptchaService googleReCaptchaService;

    /**
     * The captcha aspect to validate.
     */
    @MockBean
    private GoogleReCaptchaV3Repository googleReCaptchaRepository;

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsValid_thenReturnTrue()
        throws Throwable {
        String captchaToken = "TOKEN";
        String captchaAction = "HELLO";
        String ipAddress = "0.0.0.0";

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setAction("HELLO");
        response.setHostname("localhost");
        response.setScore(1.0f);
        response.setSuccess(true);
        Mockito.when(googleReCaptchaRepository.verify(Mockito.any(), Mockito.eq(captchaToken), Mockito.eq(ipAddress))).thenReturn(Mono.just(response));
        
        Assertions.assertTrue(googleReCaptchaService.validate(captchaToken, captchaAction, ipAddress).block());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaTokenIsNull_thenThrowNullPointerException()
        throws Throwable {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            googleReCaptchaService.validate(null, "HELLO", "0.0.0.0");
        });
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaActionIsNull_thenThrowNullPointerException()
        throws Throwable {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            googleReCaptchaService.validate("TOKEN", null, "0.0.0.0");
        });
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsValidButActionDoesntMatch_thenReturnFalse()
        throws Throwable {
        String captchaToken = "TOKEN";
        String captchaAction = "HELLO";
        String ipAddress = "0.0.0.0";

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setAction("GOODBYE");
        response.setHostname("localhost");
        response.setScore(1.0f);
        response.setSuccess(true);
        Mockito.when(googleReCaptchaRepository.verify(Mockito.any(), Mockito.eq(captchaToken), Mockito.eq(ipAddress))).thenReturn(Mono.just(response));

        Assertions.assertFalse(googleReCaptchaService.validate(captchaToken, captchaAction, ipAddress).block());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsValidButScoringIsTooBad_thenReturnFalse()
        throws Throwable {
        String captchaToken = "TOKEN";
        String captchaAction = "HELLO";
        String ipAddress = "0.0.0.0";

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setAction("HELLO");
        response.setHostname("localhost");
        response.setScore(0.4f);
        response.setSuccess(true);
        Mockito.when(googleReCaptchaRepository.verify(Mockito.any(), Mockito.eq(captchaToken), Mockito.eq(ipAddress))).thenReturn(Mono.just(response));

        Assertions.assertFalse(googleReCaptchaService.validate(captchaToken, captchaAction, ipAddress).block());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsInvalid_thenReturnFalse()
        throws Throwable {
        String captchaToken = "TOKEN";
        String captchaAction = "HELLO";
        String ipAddress = "0.0.0.0";

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setSuccess(false);
        Mockito.when(googleReCaptchaRepository.verify(Mockito.any(), Mockito.eq(captchaToken), Mockito.eq(ipAddress))).thenReturn(Mono.just(response));

        Assertions.assertFalse(googleReCaptchaService.validate(captchaToken, captchaAction, ipAddress).block());
    }
}
