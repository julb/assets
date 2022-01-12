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
package me.julb.springbootstarter.security.reactive.controllers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import me.julb.library.dto.http.error.HttpErrorResponseDTO;
import me.julb.library.utility.http.HttpErrorResponseBuilder;
import me.julb.springbootstarter.opentracing.utility.TracingContextUtility;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import brave.Tracer;
import reactor.core.publisher.Mono;

/**
 * A controller advice defined to catch all global exceptions.
 * <br>
 * @author Julb.
 */
@RestController
@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionAdviceController {

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    /**
     * The tracer attribute.
     */
    @Autowired
    private Tracer tracer;

    /**
     * Method that handles thrown Unauthorized accesses.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({BadCredentialsException.class, InsufficientAuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleUnauthorizedException(Exception exception, ServerWebExchange exchange) {

        // For this exception, raise this HTTP Status.
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        LOGGER.warn("Exception {} caught: {}. Root cause: {}", exception.getClass(), exception.getMessage(), (exception.getCause() != null ? exception.getCause().getMessage() : null));
        LOGGER.debug(HttpStatus.UNAUTHORIZED.getReasonPhrase(), exception);
        // Create the request
        HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
            httpStatus.value(), 
            httpStatus.getReasonPhrase(), 
            exchange.getRequest().getPath().toString(),
            TracingContextUtility.asMap(tracer.currentSpan().context()));
        return Mono.just(new ResponseEntity<>(errorResponse, httpStatus));
    }

    /**
     * Method that handles thrown Forbidden exceptions.
     * @param exception the exception that occurred.
     * @param exchange the exchange.
     * @return the error response.
     */
    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public final Mono<ResponseEntity<HttpErrorResponseDTO>> handleForbiddenException(Exception exception, ServerWebExchange exchange) {

        // For this exception, raise this HTTP Status.
        return securityService.isAuthenticated().map(isAuthenticated -> {
            HttpStatus httpStatus;
            if (isAuthenticated) {
                httpStatus = HttpStatus.FORBIDDEN;
            } else {
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
            LOGGER.warn("Exception {} caught: {}. Root cause: {}", exception.getClass(), exception.getMessage(), (exception.getCause() != null ? exception.getCause().getMessage() : null));
            LOGGER.debug(httpStatus.getReasonPhrase(), exception);
    
            // Create the request
            HttpErrorResponseDTO errorResponse = HttpErrorResponseBuilder.defaultErrorResponse(
                httpStatus.value(), 
                httpStatus.getReasonPhrase(), 
                exchange.getRequest().getPath().toString(),
                TracingContextUtility.asMap(tracer.currentSpan().context()));
            return new ResponseEntity<>(errorResponse, httpStatus);
        });
    }
}
