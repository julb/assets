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

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.springbootstarter.googlerecaptcha.configurations.GoogleReCaptchaConfiguration;
import me.julb.springbootstarter.googlerecaptcha.consumers.GoogleReCaptchaFeignClient;
import me.julb.springbootstarter.googlerecaptcha.consumers.GoogleReCaptchaV3ChallengeResponseDTO;
import me.julb.springbootstarter.googlerecaptcha.services.impl.GoogleReCaptchaV3ServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Test class for {@link GoogleReCaptchaV3ServiceImpl} class.
 * <br>
 * @author Julb.
 */
@ContextConfiguration(classes = {GoogleReCaptchaConfiguration.class, GoogleReCaptchaV3ServiceImpl.class})
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
    private GoogleReCaptchaFeignClient googleReCaptchaFeignClient;

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsValid_thenReturnTrue()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("HELLO");

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setAction("HELLO");
        response.setHostname("localhost");
        response.setScore(1.0f);
        response.setSuccess(true);
        Mockito.when(googleReCaptchaFeignClient.verify(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertTrue(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRequestIsNull_thenThrowNullPointerException()
        throws Throwable {
        Assertions.assertThrows(NullPointerException.class, () -> {
            googleReCaptchaService.validate(null);
        });
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenNoCaptchaToken_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn(null);
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("HELLO");
        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaTokenHasInvalidFormat_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("$$$$$");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("HELLO");
        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenNoCaptchaAction_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn(null);
        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaActionHasInvalidFormat_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("$$$$");
        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaActionIsUnknown_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("UNKNOWN");
        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsValidButActionDoesntMatch_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("HELLO");

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setAction("GOODBYE");
        response.setHostname("localhost");
        response.setScore(1.0f);
        response.setSuccess(true);
        Mockito.when(googleReCaptchaFeignClient.verify(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsValidButScoringIsTooBad_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("HELLO");

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setAction("HELLO");
        response.setHostname("localhost");
        response.setScore(0.4f);
        response.setSuccess(true);
        Mockito.when(googleReCaptchaFeignClient.verify(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCaptchaIsInvalid_thenReturnFalse()
        throws Throwable {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteAddr()).thenReturn("0.0.0.0");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_TOKEN)).thenReturn("TOKEN");
        Mockito.when(request.getHeader(CustomHttpHeaders.X_GOOGLE_RECAPTCHA_ACTION)).thenReturn("HELLO");

        GoogleReCaptchaV3ChallengeResponseDTO response = new GoogleReCaptchaV3ChallengeResponseDTO();
        response.setSuccess(false);
        Mockito.when(googleReCaptchaFeignClient.verify(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertFalse(googleReCaptchaService.validate(request));
    }
}
