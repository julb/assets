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
package me.julb.springbootstarter.web.reactive.filters;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import me.julb.library.utility.constants.CustomHttpHeaders;

import reactor.core.publisher.Mono;

/**
 * An interceptor to log the inbound request.
 * <br>
 * @author Julb.
 */
@Slf4j
public class RequestLoggingWebFilter implements WebFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Trademark from header.
        final boolean traceEnabled = BooleanUtils.toBoolean(exchange.getRequest().getHeaders().getFirst(CustomHttpHeaders.X_JULB_HTTP_TRACE_ENABLED));

        // Trademark from system.
        if (traceEnabled) {
            LOGGER.info(">> IN - {} {}.", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        } else {
            LOGGER.trace(">> IN - {} {}.", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        }

        exchange.getResponse().beforeCommit(() -> {
            return Mono.deferContextual(ctx -> {
                if (traceEnabled) {
                    LOGGER.info("<< OUT - {} {}.", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
                } else {
                    LOGGER.trace("<< OUT - {} {}.", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
                }
                return Mono.empty();
            });
        });

        return chain.filter(exchange);
    }
}
