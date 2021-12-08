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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
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
        return chain.filter(new RequestLoggingServerWebExchangeDecorator(exchange));
    }

    /**
     * A reactive decorator to log the inbound request.
     * <br>
     * @author Julb.
     */
    private static final class RequestLoggingServerWebExchangeDecorator extends ServerWebExchangeDecorator {

        /**
         * A flag indicating of the trace is enabled.
         */
        private boolean traceEnabled;

        /**
         * Default constructor.
         * @param delegate the delegate.
         */
        public RequestLoggingServerWebExchangeDecorator(ServerWebExchange delegate) {
            super(delegate);

            // Flag indicating if trace is enabled.
            this.traceEnabled = BooleanUtils.toBoolean(getDelegate().getRequest().getHeaders().getFirst(CustomHttpHeaders.X_JULB_HTTP_TRACE_ENABLED));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServerHttpRequest getRequest() {
            if (this.traceEnabled) {
                LOGGER.info(">> IN - {} {}.", getDelegate().getRequest().getMethod(), getDelegate().getRequest().getURI());
            } else {
                LOGGER.debug(">> IN - {} {}.", getDelegate().getRequest().getMethod(), getDelegate().getRequest().getURI());
            }
            return getDelegate().getRequest();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServerHttpResponse getResponse() {
            if (this.traceEnabled) {
                LOGGER.info("<< OUT - {} {}.", getDelegate().getRequest().getMethod(), getDelegate().getRequest().getURI());
            } else {
                LOGGER.debug("<< OUT - {} {}.", getDelegate().getRequest().getMethod(), getDelegate().getRequest().getURI());
            }
            return getDelegate().getResponse();
        }
    }
}
