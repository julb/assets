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
package io.julb.springbootstarter.security.configurations.beans.userdetails;

import io.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByApiKeyUserDetailsDelegateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * A service used to pre-authenticate users with API Key according to the given header.
 * <P>
 * @author Julb.
 */
public class AuthenticationByApiKeyUserDetailsService implements UserDetailsService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationByApiKeyUserDetailsService.class);

    /**
     * The authentication by API KEY delegate service.
     */
    private IAuthenticationByApiKeyUserDetailsDelegateService authenticationByApiKeyUserDetailsDelegateService;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String arg0)
        throws UsernameNotFoundException {
        LOGGER.info("Loading user authentication details for user <{}>.", arg0);
        return authenticationByApiKeyUserDetailsDelegateService.loadUserDetailsByApiKey(arg0);
    }

    /**
     * Setter for property authenticationByApiKeyUserDetailsDelegateService.
     * @param authenticationByApiKeyUserDetailsDelegateService New value of property authenticationByApiKeyUserDetailsDelegateService.
     */
    public void setAuthenticationByApiKeyUserDetailsDelegateService(IAuthenticationByApiKeyUserDetailsDelegateService authenticationByApiKeyUserDetailsDelegateService) {
        this.authenticationByApiKeyUserDetailsDelegateService = authenticationByApiKeyUserDetailsDelegateService;
    }
}
