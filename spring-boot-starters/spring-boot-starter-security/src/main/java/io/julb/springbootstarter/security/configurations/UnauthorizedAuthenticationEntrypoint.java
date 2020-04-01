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
package io.julb.springbootstarter.security.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.julb.library.dto.http.error.HttpErrorResponseDTO;
import io.julb.library.utility.http.HttpErrorResponseBuilder;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * The unauthorized entrypoint.
 * <P>
 * @author Julb.
 */
public class UnauthorizedAuthenticationEntrypoint implements AuthenticationEntryPoint {
    // ------------------------------------------ Constructors.

    // ------------------------------------------ Getters/Setters.

    // ------------------------------------------ Class methods.

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException,
        ServletException {
        HttpStatus httpStatus;
        if (authException instanceof BadCredentialsException || authException instanceof InsufficientAuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            httpStatus = HttpStatus.FORBIDDEN;
        }

        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
        response.setStatus(httpStatus.value());
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
        response.flushBuffer();
    }

}
