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
package me.julb.springbootstarter.security.reactive.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import me.julb.springbootstarter.security.reactive.configurations.beans.filters.AuthenticationByTotpAuthenticationWebFilter;
import me.julb.springbootstarter.security.reactive.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.reactive.configurations.beans.handlers.CustomAuthenticationSuccessHandler;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.AuthenticationByTotpUserDetailsService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByTotpUserDetailsDelegateService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

/**
 * The security authentication by totp configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.username-totp", name = "enabled", matchIfMissing = false)
@ConditionalOnBean(IAuthenticationByTotpUserDetailsDelegateService.class)
public class SecurityAuthenticationByTotpConfiguration {

    /**
     * The authentication by password delegate service.
     */
    @Autowired
    private IAuthenticationByTotpUserDetailsDelegateService authenticationByTotpUserDetailsDelegateService;

    /**
     * The authentication handler delegate service.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * Builds a filter to authenticate by user and totp.
     * @param authenticationManager the authentication manager.
     * @return the filter.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationByTotpAuthenticationWebFilter authenticationByTotpFilter()
        throws Exception {
        AuthenticationByTotpAuthenticationWebFilter filter = new AuthenticationByTotpAuthenticationWebFilter(authenticationByTotpAuthenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationByTotpSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationByTotpFailureHandler());
        return filter;
    }

    /**
     * The service used to authenticate a user with a totp.
     * @return a service used to authenticate a user with a totp.
     */
    @Bean
    public AuthenticationByTotpUserDetailsService authenticationByTotpUserDetailsService() {
        AuthenticationByTotpUserDetailsService authenticationByTotpUserDetailsService = new AuthenticationByTotpUserDetailsService();
        authenticationByTotpUserDetailsService.setAuthenticationByTotpUserDetailsDelegateService(authenticationByTotpUserDetailsDelegateService);
        return authenticationByTotpUserDetailsService;
    }

    /**
     * The manager used to authenticate the user by totp.
     * @return the manager to authenticate by totp.
     */
    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationByTotpAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(authenticationByTotpUserDetailsService());
    }

    /**
     * The authentication success handler.
     * @return the authentication success handler.
     */
    @Bean
    public ServerAuthenticationSuccessHandler authenticationByTotpSuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationSuccessHandler;
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public ServerAuthenticationFailureHandler authenticationByTotpFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
        customAuthenticationFailureHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationFailureHandler;
    }
}