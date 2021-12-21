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

import lombok.Setter;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import brave.Span;
import brave.Tracer;
import reactor.core.publisher.Mono;

/**
 * This filter returns as a response header the Brave Trace ID.
 * <br>
 * @author Julb.
 */
@Setter
public class ReturnOpenTracingTraceAsResponseHeaderWebFilter implements WebFilter {

    /**
     * The default trace ID response header name.
     */
    private static final String OPENTRACING_TRACE_DEFAULT_HEADER_NAME = "X-B3-TraceId";

    /**
     * The default span ID response header name.
     */
    private static final String OPENTRACING_SPAN_DEFAULT_HEADER_NAME = "X-B3-SpanId";

    /**
     * The default sampled ID response header name.
     */
    private static final String OPENTRACING_SAMPLED_DEFAULT_HEADER_NAME = "X-B3-Sampled";

    //@formatter:off
     /**
     * The tracer attribute.
     * -- SETTER --
     * Setter for {@link #tracer} property.
     * @param tracer the value to set.
     */
     //@formatter:on
    private Tracer tracer;

    //@formatter:off
     /**
     * The openTracingTraceHeaderName attribute.
     * -- SETTER --
     * Setter for {@link #openTracingTraceHeaderName} property.
     * @param openTracingTraceHeaderName the value to set.
     */
     //@formatter:on
    private String openTracingTraceHeaderName;

    //@formatter:off
     /**
     * The openTracingSpanHeaderName attribute.
     * -- SETTER --
     * Setter for {@link #openTracingSpanHeaderName} property.
     * @param openTracingSpanHeaderName the value to set.
     */
     //@formatter:on
    private String openTracingSpanHeaderName;

    //@formatter:off
     /**
     * The openTracingSampledHeaderName attribute.
     * -- SETTER --
     * Setter for {@link #openTracingSampledHeaderName} property.
     * @param openTracingSampledHeaderName the value to set.
     */
     //@formatter:on
    private String openTracingSampledHeaderName;

    /**
     * Constructor.
     * @param tracer the tracer.
     */
    public ReturnOpenTracingTraceAsResponseHeaderWebFilter(Tracer tracer) {
        this.tracer = tracer;
        this.openTracingTraceHeaderName = OPENTRACING_TRACE_DEFAULT_HEADER_NAME;
        this.openTracingSpanHeaderName = OPENTRACING_SPAN_DEFAULT_HEADER_NAME;
        this.openTracingSampledHeaderName = OPENTRACING_SAMPLED_DEFAULT_HEADER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        
        // Get current span.
        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan != null) {
            exchange.getResponse().getHeaders().add(openTracingTraceHeaderName, currentSpan.context().traceIdString());
            exchange.getResponse().getHeaders().add(openTracingSpanHeaderName, currentSpan.context().spanIdString());
            exchange.getResponse().getHeaders().add(openTracingSampledHeaderName, String.valueOf(BooleanUtils.toInteger(currentSpan.context().sampled())));
        }

        return chain.filter(exchange);
    }

}