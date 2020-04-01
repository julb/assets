package io.julb.springbootstarter.web.filters;

import io.julb.library.utility.constants.CustomHttpHeaders;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

/**
 * An interceptor to log the inbound request.
 * <P>
 * @author Julb.
 */
public class TrademarkWebContentInterceptor extends WebContentInterceptor {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TrademarkWebContentInterceptor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws ServletException {
        String trademark = request.getHeader(CustomHttpHeaders.X_JULB_TM);

        if (StringUtils.isNotBlank(trademark)) {
            // Set trademark.
            LOGGER.debug(">>> TM - {}.", trademark);
            TrademarkContextHolder.setTrademark(trademark);
        }

        // Return value.
        return super.preHandle(request, response, handler);
    }
}
