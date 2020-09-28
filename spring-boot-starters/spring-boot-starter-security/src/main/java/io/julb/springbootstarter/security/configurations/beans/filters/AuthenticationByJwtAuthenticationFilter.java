/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package io.julb.springbootstarter.security.configurations.beans.filters;

import io.julb.library.utility.http.HttpHeaderUtility;
import io.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomJwtPreAuthenticatedAuthenticationToken;
import io.julb.springbootstarter.security.configurations.beans.matchers.RequestHeaderBearerTokenRequestMatcher;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * The pre-authenticated header authentication filter for internal JWT.
 * <P>
 * @author Julb.
 */
public class AuthenticationByJwtAuthenticationFilter extends AbstractRequestHeaderAuthenticationFilter {

    /**
     * Constructor.
     */
    public AuthenticationByJwtAuthenticationFilter() {
        super(HttpHeaders.AUTHORIZATION);
        this.setRequestMatcher(new RequestHeaderBearerTokenRequestMatcher(HttpHeaders.AUTHORIZATION));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException,
        ServletException {
        super.doFilter(request, response, chain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String preAuthenticatedPrincipal = (String) super.getPreAuthenticatedPrincipal(request);
        if (preAuthenticatedPrincipal == null) {
            return null;
        }
        // Extract value from bearer.
        return HttpHeaderUtility.fromBearerToken(preAuthenticatedPrincipal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return HttpHeaderUtility.BEARER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PreAuthenticatedAuthenticationToken createPreAuthenticatedAuthenticationToken(Object principal, Object credentials) {
        return new CustomJwtPreAuthenticatedAuthenticationToken(principal, credentials);
    }
}
