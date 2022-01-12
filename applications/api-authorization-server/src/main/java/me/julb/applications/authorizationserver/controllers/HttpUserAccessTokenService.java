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

package me.julb.applications.authorizationserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenWithIdTokenDTO;
import me.julb.library.utility.exceptions.InternalServerErrorException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The access token utility.
 * <br>
 * @author Julb.
 */
@Service
public class HttpUserAccessTokenService {

    /**
     * The JSON object mapper.
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Write access token with id token to the HTTP response.
     * @param accessToken the access token.
     * @param response the response.
     */
    public Mono<Void> writeResponseWithIdToken(UserSessionAccessTokenWithIdTokenDTO accessToken, ServerHttpResponse response) {
        try {
            // Write JSON response to body.
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(accessToken));
            return response.writeWith(Flux.just(buffer));
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Write access token to the HTTP response.
     * @param accessToken the access token.
     * @param response the response.
     */
    public Mono<Void> writeResponseWithoutIdToken(UserSessionAccessTokenDTO accessToken, ServerHttpResponse response) {
        try {
            // Write JSON response to body.
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(accessToken));
            return response.writeWith(Flux.just(buffer));
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
