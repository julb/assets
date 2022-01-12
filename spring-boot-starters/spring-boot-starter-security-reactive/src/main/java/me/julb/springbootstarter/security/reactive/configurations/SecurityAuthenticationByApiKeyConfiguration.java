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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.authentication.ReactivePreAuthenticatedAuthenticationManager;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import me.julb.springbootstarter.security.configurations.properties.SecurityApiKeyProperties;
import me.julb.springbootstarter.security.reactive.configurations.beans.filters.AuthenticationByApiKeyAuthenticationWebFilter;
import me.julb.springbootstarter.security.reactive.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.reactive.configurations.beans.handlers.CustomAuthenticationSuccessHandler;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.AuthenticationByApiKeyUserDetailsService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByApiKeyUserDetailsDelegateService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

/**
 * The security API key configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.api-key", name = "enabled", matchIfMissing = false)
@ConditionalOnBean(IAuthenticationByApiKeyUserDetailsDelegateService.class)
@EnableConfigurationProperties(SecurityApiKeyProperties.class)
public class SecurityAuthenticationByApiKeyConfiguration {

    /**
     * The security api key properties.
     */
    @Autowired
    private SecurityApiKeyProperties securityApiKeyProperties;

    /**
     * The authentication by api key delegate service.
     */
    @Autowired
    private IAuthenticationByApiKeyUserDetailsDelegateService authenticationByApiKeyUserDetailsDelegateService;

    /**
     * The authentication handler delegate service.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * Filters the request to get the API key.
     * @return the request header authentication filter.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationByApiKeyAuthenticationWebFilter authenticationByApiKeyFilter()
        throws Exception {
        AuthenticationByApiKeyAuthenticationWebFilter filter = new AuthenticationByApiKeyAuthenticationWebFilter(authenticationByApiKeyAuthenticationManager(), securityApiKeyProperties.getHeaderName());
        filter.setAuthenticationSuccessHandler(authenticationByApiKeySuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationByApiKeyFailureHandler());
        return filter;
    }

    /**
     * The service used to authenticate the user according to the received JWT.
     * @return the instance of the service.
     */
    @Bean
    public AuthenticationByApiKeyUserDetailsService authenticationByApiKeyUserDetailsService() {
        AuthenticationByApiKeyUserDetailsService authenticationByApiKeyUserDetailsService = new AuthenticationByApiKeyUserDetailsService();
        authenticationByApiKeyUserDetailsService.setAuthenticationByApiKeyUserDetailsDelegateService(authenticationByApiKeyUserDetailsDelegateService);
        return authenticationByApiKeyUserDetailsService;
    }

    /**
     * Defines a pre-authenticated authentication provider.
     * @return the preAuthenticatedAuthenticationProvider.
     */
    @Bean
    public ReactivePreAuthenticatedAuthenticationManager authenticationByApiKeyAuthenticationManager() {
        return new ReactivePreAuthenticatedAuthenticationManager(authenticationByApiKeyUserDetailsService());
    }

    /**
     * The authentication success handler.
     * @return the authentication success handler.
     */
    @Bean
    public ServerAuthenticationSuccessHandler authenticationByApiKeySuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationSuccessHandler;
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public ServerAuthenticationFailureHandler authenticationByApiKeyFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
        customAuthenticationFailureHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationFailureHandler;
    }
}