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

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

import reactor.core.publisher.Mono;

/**
 * A custom handler for successful authentication.
 * <br>
 * @author Julb.
 */
public class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    /**
     * The authentication user details delegate service.
     */
    private Optional<IAuthenticationUserDetailsHandlerDelegate> authenticationUserDetailsDelegateService = Optional.empty();

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return authenticationUserDetailsDelegateService
            .map(service -> service.onAuthenticationSuccess(webFilterExchange, authentication))
            .orElse(Mono.empty());
    }

    /**
     * Setter for property authenticationUserDetailsDelegateService.
     * @param authenticationUserDetailsDelegateService New value of property authenticationUserDetailsDelegateService.
     */
    public void setAuthenticationUserDetailsDelegateService(IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService) {
        this.authenticationUserDetailsDelegateService = Optional.ofNullable(authenticationUserDetailsDelegateService);
    }
}
