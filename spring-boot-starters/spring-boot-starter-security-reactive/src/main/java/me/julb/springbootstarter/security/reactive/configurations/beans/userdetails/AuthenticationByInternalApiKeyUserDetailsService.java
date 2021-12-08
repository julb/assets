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
package me.julb.springbootstarter.security.reactive.configurations.beans.userdetails;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import me.julb.springbootstarter.core.configurations.properties.SecurityInternalApiKeyProperties;

import reactor.core.publisher.Mono;

/**
 * A service used to pre-authenticate users according to the given header.
 * <br>
 * @author Julb.
 */
@Slf4j
public class AuthenticationByInternalApiKeyUserDetailsService implements ReactiveUserDetailsService {

    /**
     * The security internal api key properties.
     */
    @Autowired
    private SecurityInternalApiKeyProperties securityInternalApiKeyProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDetails> findByUsername(String apiKey) {
        LOGGER.info("Authentication by internal api key");
        if (apiKey != null) {
            if (StringUtils.equalsIgnoreCase(apiKey, securityInternalApiKeyProperties.getHeaderValue())) {
                //@formatter:off
                return Mono.just(User.builder()
                    .username("system")
                    .password("N/A")
                    .roles("ADMINISTRATOR", "ACTUATOR")
                    .build());
                //@formatter:on
            } else {
                return Mono.error(new UsernameNotFoundException("user"));
            }
        }

        // Return null.
        return Mono.empty();
    }
}
