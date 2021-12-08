package me.julb.springbootstarter.security.mvc.configurations.beans.matchers;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.http.HttpHeaderUtility;

/**
 * A bearer token request matcher for an header.
 * <br>
 * @author Julb.
 */
public class RequestHeaderBearerTokenRequestMatcher implements RequestMatcher {

    /**
     * The bearer token prefix.
     */
    private static String BEARER_TOKEN_PREFIX = HttpHeaderUtility.BEARER + Chars.SPACE;

    /**
     * The headerName attribute.
     */
    private String headerName;

    /**
     * Default constructor.
     * @param headerName the header name.
     */
    public RequestHeaderBearerTokenRequestMatcher(String headerName) {
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(headerName)).filter(header -> header.startsWith(BEARER_TOKEN_PREFIX)).isPresent();
    }
}