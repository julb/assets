package me.julb.springbootstarter.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.springframework.web.filter.GenericFilterBean;

import brave.Span;
import brave.Tracer;

/**
 * This filter returns as a response header the Brave Trace ID.
 * <P>
 * @author Julb.
 */
@Setter
public class ReturnOpenTracingTraceAsResponseHeaderFilterBean extends GenericFilterBean {

    /**
     * The default trace ID response header name.
     */
    private static final String OPENTRACING_TRACE_DEFAULT_HEADER_NAME = "X-B3-TraceId";

    /**
     * The default trace ID response header name.
     */
    private static final String OPENTRACING_SPAN_DEFAULT_HEADER_NAME = "X-B3-SpanId";

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

    /**
     * Constructor.
     * @param tracer the tracer.
     */
    public ReturnOpenTracingTraceAsResponseHeaderFilterBean(Tracer tracer) {
        this.tracer = tracer;
        this.openTracingTraceHeaderName = OPENTRACING_TRACE_DEFAULT_HEADER_NAME;
        this.openTracingSpanHeaderName = OPENTRACING_SPAN_DEFAULT_HEADER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException,
        ServletException {
        // Get current span.
        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan != null) {
            // Add it to the HttpServletResponse.
            if (response instanceof HttpServletResponse) {
                // for readability we're returning trace id in a hex form
                ((HttpServletResponse) response).addHeader(openTracingTraceHeaderName, currentSpan.context().traceIdString());
                ((HttpServletResponse) response).addHeader(openTracingSpanHeaderName, currentSpan.context().spanIdString());
            }
        }

        // Go on.
        chain.doFilter(request, response);
    }

}