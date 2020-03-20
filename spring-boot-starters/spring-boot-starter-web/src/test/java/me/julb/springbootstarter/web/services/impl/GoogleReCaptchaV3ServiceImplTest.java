/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.springbootstarter.test.base.AbstractBaseTest;
import me.julb.springbootstarter.web.configurations.GoogleReCaptchaConfiguration;
import me.julb.springbootstarter.web.services.CaptchaService;
import me.julb.springbootstarter.web.services.dto.GoogleReCaptchaV3ChallengeResponseDTO;

/**
 * Test class for {@link GoogleReCaptchaV3ServiceImpl} class.
 * <P>
 * @author Julb.
 */
@ContextConfiguration(classes = {GoogleReCaptchaConfiguration.class, GoogleReCaptchaV3ServiceImpl.class})
public class GoogleReCaptchaV3ServiceImplTest extends AbstractBaseTest {

    /**
     * The captcha service.
     */
    @Autowired
    private CaptchaService captchaService;

    /**
     * The captcha aspect to validate.
     */
    @MockBean
    private RestTemplate googleReCaptchaRestTemplate;

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
        Mockito.when(googleReCaptchaRestTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertTrue(captchaService.validate(request));
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRequestIsNull_thenThrowNullPointerException()
        throws Throwable {
        Assertions.assertThrows(NullPointerException.class, () -> {
            captchaService.validate(null);
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
        Assertions.assertFalse(captchaService.validate(request));
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
        Assertions.assertFalse(captchaService.validate(request));
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
        Assertions.assertFalse(captchaService.validate(request));
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
        Assertions.assertFalse(captchaService.validate(request));
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
        Assertions.assertFalse(captchaService.validate(request));
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
        Mockito.when(googleReCaptchaRestTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertFalse(captchaService.validate(request));
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
        Mockito.when(googleReCaptchaRestTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertFalse(captchaService.validate(request));
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
        Mockito.when(googleReCaptchaRestTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(response);

        Assertions.assertFalse(captchaService.validate(request));
    }
}
