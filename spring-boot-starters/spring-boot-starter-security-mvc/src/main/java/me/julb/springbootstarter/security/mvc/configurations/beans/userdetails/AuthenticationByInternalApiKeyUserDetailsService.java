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
package me.julb.springbootstarter.security.mvc.configurations.beans.userdetails;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import me.julb.springbootstarter.core.configurations.properties.SecurityInternalApiKeyProperties;

/**
 * A service used to pre-authenticate users according to the given header.
 * <br>
 * @author Julb.
 */
@Slf4j
public class AuthenticationByInternalApiKeyUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    /**
     * The security internal api key properties.
     */
    @Autowired
    private SecurityInternalApiKeyProperties securityInternalApiKeyProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken)
        throws UsernameNotFoundException {
        LOGGER.info("Authentication by internal api key");
        String apiKey = (String) preAuthenticatedAuthenticationToken.getPrincipal();
        if (apiKey != null) {
            if (StringUtils.equalsIgnoreCase(apiKey, securityInternalApiKeyProperties.getHeaderValue())) {
                LOGGER.debug("Authentication by internal api key successful");

                //@formatter:off
                return User.builder()
                    .username("system")
                    .password("N/A")
                    .roles("ADMINISTRATOR", "ACTUATOR", "FULLY_AUTHENTICATED")
                    .build();
                //@formatter:on
            } else {
                throw new UsernameNotFoundException("user");
            }
        }

        // Return null.
        return null;
    }

}
