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

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import me.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomUsernameTotpAuthenticationToken;
import me.julb.springbootstarter.security.reactive.services.impl.SecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The TOTP authentication filter.
 * <br>
 * @author Julb.
 */
@Slf4j
public class AuthenticationByTotpAuthenticationWebFilter extends AuthenticationWebFilter implements Ordered {

    /**
     * The security service.
     */
    @Autowired
    private SecurityService securityService;

    /**
     * The URL attribute.
     */
    private static final String URL = "/login/totp";

    /**
     * The DEVICE_ID_PARAMETER attribute.
     */
    private static final String DEVICE_ID_PARAMETER = "deviceId";

    /**
     * The TOTP_PARAMETER attribute.
     */
    private static final String TOTP_PARAMETER = "totp";

    /**
     * The password encoder service.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * Default constructor.
     * @param reactiveAuthenticationManager the reactive authentication manager.
     */
    public AuthenticationByTotpAuthenticationWebFilter(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        super(reactiveAuthenticationManager);
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, URL));
        this.setServerAuthenticationConverter(exchange -> {
            return securityService.getConnectedUserIdentity().map(connectedUser -> {
                // Extract session from token
                LOGGER.info("Trying to authenticate with user/totp");
                String userId = connectedUser.getUserId();
                String sessionId = connectedUser.getSessionId();
                String deviceId = exchange.getRequest().getQueryParams().getFirst(DEVICE_ID_PARAMETER);
                String rawTotp = exchange.getRequest().getQueryParams().getFirst(TOTP_PARAMETER);
                String totp = passwordEncoderService.hash(rawTotp);

                return new CustomUsernameTotpAuthenticationToken(userId, sessionId, deviceId, totp);
            });
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
