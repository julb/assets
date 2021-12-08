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
package me.julb.springbootstarter.security.mvc.configurations.beans.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import me.julb.springbootstarter.security.mvc.configurations.beans.matchers.RequestHeaderRequestMatcher;

/**
 * The pre-authenticated header authentication filter for API keys.
 * <br>
 * @author Julb.
 */
public abstract class AbstractRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter implements Ordered {

    /**
     * The requestMatcher attribute.
     */
    private RequestMatcher requestMatcher;

    /**
     * The authenticationManager attribute.
     */
    private AuthenticationManager authenticationManager;

    /**
     * Constructor.
     * @param apiKeyHeader the API key header.
     */
    public AbstractRequestHeaderAuthenticationFilter(String apiKeyHeader) {
        super();
        this.setPrincipalRequestHeader(apiKeyHeader);
        this.setExceptionIfHeaderMissing(false);
        this.requestMatcher = new RequestHeaderRequestMatcher(apiKeyHeader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException,
        ServletException {
        //
        if (this.requestMatcher.matches((HttpServletRequest) request)) {
            doAuthenticate((HttpServletRequest) request, (HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    /**
     * Setter for property requestMatcher.
     * @param requestMatcher New value of property requestMatcher.
     */
    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    /**
     * Creates a pr-authenticated authentication token.
     * @param principal the principal.
     * @param credentials the credentials.
     * @return the pre-authenticated authentication token.
     */
    protected PreAuthenticatedAuthenticationToken createPreAuthenticatedAuthenticationToken(Object principal, Object credentials) {
        return new PreAuthenticatedAuthenticationToken(principal, credentials);
    }

    /**
     * Do the actual authentication for a pre-authenticated user.
     */
    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response)
        throws IOException,
        ServletException {
        Authentication authResult;

        Object principal = getPreAuthenticatedPrincipal(request);
        Object credentials = getPreAuthenticatedCredentials(request);

        if (principal == null) {
            return;
        }

        try {
            PreAuthenticatedAuthenticationToken authRequest = createPreAuthenticatedAuthenticationToken(principal, credentials);
            authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            authResult = authenticationManager.authenticate(authRequest);
            successfulAuthentication(request, response, authResult);
        } catch (AuthenticationException failed) {
            unsuccessfulAuthentication(request, response, failed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
