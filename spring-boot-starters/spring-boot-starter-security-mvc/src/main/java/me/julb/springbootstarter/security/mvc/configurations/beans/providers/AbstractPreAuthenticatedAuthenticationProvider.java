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

package me.julb.springbootstarter.security.mvc.configurations.beans.providers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * The pre-authenticated authentication provider. This is a copy of the Spring class
 * <br>
 * @author Julb.
 */
public abstract class AbstractPreAuthenticatedAuthenticationProvider implements AuthenticationProvider, InitializingBean, Ordered {

    /**
     * The delegate.
     */
    private PreAuthenticatedAuthenticationProvider delegate = new PreAuthenticatedAuthenticationProvider();

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        delegate.afterPropertiesSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        return delegate.authenticate(authentication);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return delegate.supports(authentication);
    }

    /**
     * Set the AuthenticatedUserDetailsService to be used to load the {@code UserDetails} for the authenticated user.
     * @param uds the pre-authenticated user details service.
     */
    public void setPreAuthenticatedUserDetailsService(AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> uds) {
        delegate.setPreAuthenticatedUserDetailsService(uds);
    }

    /**
     * If true, causes the provider to throw a BadCredentialsException if the presented authentication request is invalid (contains a null principal or credentials).
     * @param throwExceptionWhenTokenRejected the flag.
     */
    public void setThrowExceptionWhenTokenRejected(boolean throwExceptionWhenTokenRejected) {
        delegate.setThrowExceptionWhenTokenRejected(throwExceptionWhenTokenRejected);
    }

    /**
     * Sets the strategy which will be used to validate the loaded UserDetails object for the user. Defaults to an {@link AccountStatusUserDetailsChecker}.
     * @param userDetailsChecker the checker.
     */
    public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
        delegate.setUserDetailsChecker(userDetailsChecker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return delegate.getOrder();
    }

    /**
     * Sets the order.
     * @param i the order.
     */
    public void setOrder(int i) {
        delegate.setOrder(i);
    }
}
