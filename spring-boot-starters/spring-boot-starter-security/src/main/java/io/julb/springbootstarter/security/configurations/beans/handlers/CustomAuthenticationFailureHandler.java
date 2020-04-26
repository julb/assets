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
package io.julb.springbootstarter.security.configurations.beans.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.julb.library.dto.http.error.HttpErrorResponseDTO;
import io.julb.library.utility.http.HttpErrorResponseBuilder;
import io.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * A custom handler for failure authentication.
 * <P>
 * @author Julb.
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler, AuthenticationEntryPoint {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    /**
     * The authentication user details delegate service.
     */
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException,
        ServletException {
        sendResponse(request, response, exception);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException,
        ServletException {
        if (authenticationUserDetailsDelegateService != null) {
            authenticationUserDetailsDelegateService.onAuthenticationFailure(request, response, exception);
        }
        sendResponse(request, response, exception);
    }

    /**
     * Setter for property authenticationUserDetailsDelegateService.
     * @param authenticationUserDetailsDelegateService New value of property authenticationUserDetailsDelegateService.
     */
    public void setAuthenticationUserDetailsDelegateService(IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService) {
        this.authenticationUserDetailsDelegateService = authenticationUserDetailsDelegateService;
    }

    /**
     * Writes a unauthorized/forbidden response.
     * @param request the request.
     * @param response the response.
     * @param exception the cause.
     * @throws IOException if an error occurs.
     */
    private void sendResponse(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
        throws IOException {
        // Log message.
        LOGGER.info("Authentication failure.", exception);

        // Return status.
        HttpStatus httpStatus;
        if (exception instanceof BadCredentialsException || exception instanceof InsufficientAuthenticationException || exception instanceof UsernameNotFoundException || exception instanceof AccountStatusException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            httpStatus = HttpStatus.FORBIDDEN;
        }

        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
        response.flushBuffer();
    }
}
