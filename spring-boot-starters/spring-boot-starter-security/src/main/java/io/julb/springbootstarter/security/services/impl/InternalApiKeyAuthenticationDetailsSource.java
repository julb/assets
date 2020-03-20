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
package io.julb.springbootstarter.security.services.impl;

import io.julb.springbootstarter.core.configurations.properties.SecurityInternalApiKeyProperties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A service used to pre-authenticate users according to the given header.
 * <P>
 * @author Julb.
 */
public class InternalApiKeyAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, UserDetails> {

    /**
     * The security internal api key properties.
     */
    @Autowired
    private SecurityInternalApiKeyProperties securityInternalApiKeyProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails buildDetails(HttpServletRequest context) {
        String apiKey = context.getHeader(securityInternalApiKeyProperties.getHeaderName());
        if (apiKey != null) {
            if (StringUtils.equalsIgnoreCase(apiKey, securityInternalApiKeyProperties.getHeaderValue())) {
                //@formatter:off
                return User.builder()
                    .username("system")
                    .password("N/A")
                    .roles("ADMINISTRATOR", "ACTUATOR")
                    .build();
                //@formatter:on
            } else {
                return null;
            }
        }

        // Return null.
        return null;
    }

}
