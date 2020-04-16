package io.julb.springbootstarter.web.filters;

import io.julb.library.utility.constants.CustomHttpHeaders;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

/**
 * An interceptor to log the inbound request.
 * <P>
 * @author Julb.
 */
public class TrademarkFilter extends GenericFilterBean {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TrademarkFilter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException,
        ServletException {
        // Trademark from header.
        String trademark = ((HttpServletRequest) request).getHeader(CustomHttpHeaders.X_JULB_TM);

        // Trademark from system.
        if (StringUtils.isBlank(trademark)) {
            trademark = System.getProperty("trademark.override");
        }

        if (StringUtils.isNotBlank(trademark)) {
            // Set trademark.
            LOGGER.debug(">>> TM - {}.", trademark);
            TrademarkContextHolder.setTrademark(trademark);
        }

        // Go on.
        chain.doFilter(request, response);
    }
}
