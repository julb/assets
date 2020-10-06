package me.julb.springbootstarter.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.web.filter.GenericFilterBean;

import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * An interceptor to log the inbound request.
 * <P>
 * @author Julb.
 */
public class TrademarkFilter extends GenericFilterBean implements Ordered {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TrademarkFilter.class);

    /**
     * The trademark override if any.
     */
    @Value("${trademark.override:}")
    private String trademarkOverride;

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
            trademark = this.trademarkOverride;
        }

        if (StringUtils.isNotBlank(trademark)) {
            // Set trademark.
            LOGGER.debug(">>> TM - {}.", trademark);
            TrademarkContextHolder.setTrademark(trademark);

            // Add trademark to MDC.
            MDC.put("tm", trademark);
        }

        // Go on.
        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
