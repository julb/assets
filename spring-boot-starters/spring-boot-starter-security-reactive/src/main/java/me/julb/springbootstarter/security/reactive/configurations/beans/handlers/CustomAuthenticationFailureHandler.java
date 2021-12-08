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
package me.julb.springbootstarter.security.reactive.configurations.beans.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;

import me.julb.library.dto.http.error.HttpErrorResponseDTO;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.library.utility.http.HttpErrorResponseBuilder;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A custom handler for failure authentication.
 * <br>
 * @author Julb.
 */
@Slf4j
public class CustomAuthenticationFailureHandler implements ServerAuthenticationFailureHandler, ServerAuthenticationEntryPoint {

    /**
     * The authentication user details delegate service.
     */
    private Optional<IAuthenticationUserDetailsHandlerDelegate> authenticationUserDetailsDelegateService = Optional.empty();

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
        return sendResponse(exchange, exception);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return authenticationUserDetailsDelegateService
            .map(service -> service.onAuthenticationFailure(webFilterExchange, exception))
            .orElse(Mono.empty())
            .then(sendResponse(webFilterExchange.getExchange(), exception));
    }

    /**
     * Setter for property authenticationUserDetailsDelegateService.
     * @param authenticationUserDetailsDelegateService New value of property authenticationUserDetailsDelegateService.
     */
    public void setAuthenticationUserDetailsDelegateService(IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService) {
        this.authenticationUserDetailsDelegateService = Optional.ofNullable(authenticationUserDetailsDelegateService);
    }

    /**
     * Writes a unauthorized/forbidden response.
     * @param request the request.
     * @param response the response.
     * @param exception the cause.
     * @throws IOException if an error occurs.
     */
    private Mono<Void> sendResponse(ServerWebExchange exchange, AuthenticationException exception) {
        try {
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
            byte[] body = new ObjectMapper().writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);

            exchange.getResponse().setStatusCode(httpStatus);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
