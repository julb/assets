package me.julb.springbootstarter.security.configurations.beans.matchers;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * A request matcher for an header.
 * <br>
 * @author Julb.
 */
public class RequestHeaderRequestMatcher implements RequestMatcher {

    /**
     * The headerName attribute.
     */
    private String headerName;

    /**
     * Default constructor.
     * @param headerName the header name.
     */
    public RequestHeaderRequestMatcher(String headerName) {
        super();
        this.headerName = headerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(headerName)).isPresent();
    }

    /**
     * Getter for property headerName.
     * @return Value of property headerName.
     */
    protected String getHeaderName() {
        return headerName;
    }
}