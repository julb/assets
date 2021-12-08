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

package me.julb.springbootstarter.security.reactive.configurations.beans.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import me.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomUsernamePincodeAuthenticationToken;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

import reactor.core.publisher.Mono;

/**
 * The pincode authentication filter.
 * <br>
 * @author Julb.
 */
public class AuthenticationByPincodeAuthenticationWebFilter extends AuthenticationWebFilter implements Ordered {

    /**
     * The URL attribute.
     */
    private static final String URL = "/login/pincode";

    /**
     * The MAIL_PARAMETER attribute.
     */
    private static final String MAIL_PARAMETER = "mail";

    /**
     * The PINCODE_PARAMETER attribute.
     */
    private static final String PINCODE_PARAMETER = "pincode";

    /**
     * The password encoder service.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * Default constructor.
     * @param reactiveAuthenticationManager the reactive authentication manager.
     */
    public AuthenticationByPincodeAuthenticationWebFilter(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        super(reactiveAuthenticationManager);
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, URL));
        this.setServerAuthenticationConverter(exchange -> {
            String userName = exchange.getRequest().getQueryParams().getFirst(MAIL_PARAMETER);
            String password = exchange.getRequest().getQueryParams().getFirst(PINCODE_PARAMETER);
            String hashedPassword = passwordEncoderService.hash(password);
            return Mono.just(new CustomUsernamePincodeAuthenticationToken(userName, hashedPassword));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 20;
    }

}
