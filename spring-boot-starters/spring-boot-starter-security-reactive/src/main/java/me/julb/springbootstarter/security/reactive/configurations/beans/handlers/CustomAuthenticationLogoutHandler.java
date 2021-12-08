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

package me.julb.springbootstarter.security.reactive.configurations.beans.handlers;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;

import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsLogoutHandlerDelegate;

import reactor.core.publisher.Mono;

/**
 * The logout handler.
 * <br>
 * @author Julb.
 */
@Slf4j
public class CustomAuthenticationLogoutHandler implements ServerLogoutHandler {

    /**
     * The authentication user details logout handler delegate.
     */
    private Optional<IAuthenticationUserDetailsLogoutHandlerDelegate> authenticationUserDetailsLogoutHandlerDelegate = Optional.empty();

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        LOGGER.info("User has triggerred a logout.");
        return authenticationUserDetailsLogoutHandlerDelegate
            .map(delegate -> delegate.onAuthenticationLogout(exchange, authentication))
            .orElse(Mono.empty());
    }

    /**
     * Setter for property authenticationUserDetailsLogoutHandlerDelegate.
     * @param authenticationUserDetailsLogoutHandlerDelegate New value of property authenticationUserDetailsLogoutHandlerDelegate.
     */
    public void setAuthenticationUserDetailsLogoutHandlerDelegate(IAuthenticationUserDetailsLogoutHandlerDelegate authenticationUserDetailsLogoutHandlerDelegate) {
        this.authenticationUserDetailsLogoutHandlerDelegate = Optional.ofNullable(authenticationUserDetailsLogoutHandlerDelegate);
    }
}
