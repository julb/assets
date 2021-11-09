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
package me.julb.springbootstarter.security.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import me.julb.springbootstarter.security.configurations.beans.filters.AuthenticationByPasswordAuthenticationFilter;
import me.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationSuccessHandler;
import me.julb.springbootstarter.security.configurations.beans.providers.AuthenticationByPasswordAuthenticationProvider;
import me.julb.springbootstarter.security.configurations.beans.userdetails.AuthenticationByPasswordUserDetailsService;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByPasswordUserDetailsDelegateService;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

/**
 * The security authentication by password configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.username-password", name = "enabled", matchIfMissing = false)
@ConditionalOnBean(IAuthenticationByPasswordUserDetailsDelegateService.class)
public class SecurityAuthenticationByPasswordConfiguration {

    /**
     * The authentication by password delegate service.
     */
    @Autowired
    private IAuthenticationByPasswordUserDetailsDelegateService authenticationByPasswordUserDetailsDelegateService;

    /**
     * The authentication handler delegate service.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * Builds a filter to authenticate by user and password.
     * @param authenticationManager the authentication manager.
     * @return the filter.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationByPasswordAuthenticationFilter authenticationByPasswordFilter(AuthenticationManager authenticationManager)
        throws Exception {
        AuthenticationByPasswordAuthenticationFilter filter = new AuthenticationByPasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(authenticationByPasswordSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationByPasswordFailureHandler());
        return filter;
    }

    /**
     * The service used to authenticate a user with a paassword.
     * @return a service used to authenticate a user with a paassword.
     */
    @Bean
    public AuthenticationByPasswordUserDetailsService authenticationByPasswordUserDetailsService() {
        AuthenticationByPasswordUserDetailsService authenticationByPasswordUserDetailsService = new AuthenticationByPasswordUserDetailsService();
        authenticationByPasswordUserDetailsService.setAuthenticationByPasswordUserDetailsDelegateService(authenticationByPasswordUserDetailsDelegateService);
        return authenticationByPasswordUserDetailsService;
    }

    /**
     * The provider used to aauthenticate the user by password.
     * @return the provider to authenticate by password.
     */
    @Bean
    public AuthenticationByPasswordAuthenticationProvider authenticationByPasswordAuthenticationProvider() {
        AuthenticationByPasswordAuthenticationProvider daoAuthenticationProvider = new AuthenticationByPasswordAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(authenticationByPasswordUserDetailsService());
        return daoAuthenticationProvider;
    }

    /**
     * The authentication success handler.
     * @return the authentication success handler.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationByPasswordSuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationSuccessHandler;
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public AuthenticationFailureHandler authenticationByPasswordFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
        customAuthenticationFailureHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationFailureHandler;
    }
}