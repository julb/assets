package io.julb.springbootstarter.web.filters;

import io.julb.library.utility.constants.CustomHttpHeaders;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

/**
 * An interceptor to log the inbound request.
 * <P>
 * @author Julb.
 */
public class RequestLoggingWebContentInterceptor extends WebContentInterceptor {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingWebContentInterceptor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws ServletException {
        if (isRequestLoggingDisabled(request)) {
            LOGGER.debug(">> IN - {} {}.", request.getMethod(), request.getRequestURI());
        } else {
            LOGGER.info(">> IN - {} {}.", request.getMethod(), request.getRequestURI());
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
        throws Exception {
        if (isRequestLoggingDisabled(request)) {
            LOGGER.debug("<< OUT - {} {}.", request.getMethod(), request.getRequestURI());
        } else {
            LOGGER.info("<< OUT - {} {}.", request.getMethod(), request.getRequestURI());
        }

        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * Checks if the debug mode is enabled or not.
     * @param request the request.
     * @return
     */
    private Boolean isRequestLoggingDisabled(HttpServletRequest request) {
        Boolean booleanObject = BooleanUtils.toBooleanObject(request.getHeader(CustomHttpHeaders.X_JULB_HTTP_TRACE_DISABLED));
        return BooleanUtils.isTrue(booleanObject);
    }

}
