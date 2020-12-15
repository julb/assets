package me.julb.springbootstarter.web.filters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import me.julb.library.utility.constants.CustomHttpHeaders;

/**
 * An interceptor to log the inbound request.
 * <P>
 * @author Julb.
 */
@Slf4j
public class RequestLoggingWebContentInterceptor extends WebContentInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws ServletException {
        if (isRequestLoggingEnabled(request)) {
            LOGGER.info(">> IN - {} {}.", request.getMethod(), request.getRequestURI());
        } else {
            LOGGER.debug(">> IN - {} {}.", request.getMethod(), request.getRequestURI());
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
        throws Exception {
        if (isRequestLoggingEnabled(request)) {
            LOGGER.info("<< OUT - {} {}.", request.getMethod(), request.getRequestURI());
        } else {
            LOGGER.debug("<< OUT - {} {}.", request.getMethod(), request.getRequestURI());
        }

        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * Checks if the debug mode is enabled or not.
     * @param request the request.
     * @return
     */
    private Boolean isRequestLoggingEnabled(HttpServletRequest request) {
        Boolean booleanObject = BooleanUtils.toBooleanObject(request.getHeader(CustomHttpHeaders.X_JULB_HTTP_TRACE_ENABLED));
        return BooleanUtils.isTrue(booleanObject);
    }

}
