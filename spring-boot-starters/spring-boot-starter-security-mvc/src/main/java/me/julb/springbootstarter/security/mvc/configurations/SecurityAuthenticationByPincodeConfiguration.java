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
package me.julb.springbootstarter.security.mvc.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import me.julb.springbootstarter.security.mvc.configurations.beans.filters.AuthenticationByPincodeAuthenticationFilter;
import me.julb.springbootstarter.security.mvc.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.mvc.configurations.beans.handlers.CustomAuthenticationSuccessHandler;
import me.julb.springbootstarter.security.mvc.configurations.beans.providers.AuthenticationByPincodeAuthenticationProvider;
import me.julb.springbootstarter.security.mvc.configurations.beans.userdetails.AuthenticationByPincodeUserDetailsService;
import me.julb.springbootstarter.security.mvc.configurations.beans.userdetails.delegates.IAuthenticationByPincodeUserDetailsDelegateService;
import me.julb.springbootstarter.security.mvc.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

/**
 * The security authentication by pincode configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.username-pincode", name = "enabled", matchIfMissing = false)
@ConditionalOnBean(IAuthenticationByPincodeUserDetailsDelegateService.class)
public class SecurityAuthenticationByPincodeConfiguration {

    /**
     * The authentication by password delegate service.
     */
    @Autowired
    private IAuthenticationByPincodeUserDetailsDelegateService authenticationByPincodeUserDetailsDelegateService;

    /**
     * The authentication handler delegate service.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * Builds a filter to authenticate by user and pincode.
     * @param authenticationManager the authentication manager.
     * @return the filter.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationByPincodeAuthenticationFilter authenticationByPincodeFilter(AuthenticationManager authenticationManager)
        throws Exception {
        AuthenticationByPincodeAuthenticationFilter filter = new AuthenticationByPincodeAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(authenticationByPincodeSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationByPincodeFailureHandler());
        return filter;
    }

    /**
     * The service used to authenticate a user with a pincode.
     * @return a service used to authenticate a user with a pincode.
     */
    @Bean
    public AuthenticationByPincodeUserDetailsService authenticationByPincodeUserDetailsService() {
        AuthenticationByPincodeUserDetailsService authenticationByPincodeUserDetailsService = new AuthenticationByPincodeUserDetailsService();
        authenticationByPincodeUserDetailsService.setAuthenticationByPincodeUserDetailsDelegateService(authenticationByPincodeUserDetailsDelegateService);
        return authenticationByPincodeUserDetailsService;
    }

    /**
     * The provider used to aauthenticate the user by pincode.
     * @return the provider to authenticate by pincode.
     */
    @Bean
    public AuthenticationByPincodeAuthenticationProvider authenticationByPincodeAuthenticationProvider() {
        AuthenticationByPincodeAuthenticationProvider daoAuthenticationProvider = new AuthenticationByPincodeAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(authenticationByPincodeUserDetailsService());
        return daoAuthenticationProvider;
    }

    /**
     * The authentication success handler.
     * @return the authentication success handler.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationByPincodeSuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationSuccessHandler;
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public AuthenticationFailureHandler authenticationByPincodeFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
        customAuthenticationFailureHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationFailureHandler;
    }
}