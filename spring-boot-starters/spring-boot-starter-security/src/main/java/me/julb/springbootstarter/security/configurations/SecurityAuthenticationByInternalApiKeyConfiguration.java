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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import me.julb.springbootstarter.core.configurations.properties.SecurityInternalApiKeyProperties;
import me.julb.springbootstarter.security.configurations.beans.filters.AuthenticationByInternalApiKeyAuthenticationFilter;
import me.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationSuccessHandler;
import me.julb.springbootstarter.security.configurations.beans.providers.AuthenticationByInternalApiKeyAuthenticationProvider;
import me.julb.springbootstarter.security.configurations.beans.userdetails.AuthenticationByInternalApiKeyUserDetailsService;

/**
 * The security JWT configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.internal-api-key", name = "enabled", matchIfMissing = false)
@EnableConfigurationProperties(SecurityInternalApiKeyProperties.class)
public class SecurityAuthenticationByInternalApiKeyConfiguration {

    /**
     * The security api key properties.
     */
    @Autowired
    private SecurityInternalApiKeyProperties securityInternalApiKeyProperties;

    /**
     * Filters the request to get the JWT.
     * @param authenticationManager the Spring authentication manager.
     * @return the request header authentication filter.
     * @throws Exception if an error occurs.
     */
    @Bean
    public RequestHeaderAuthenticationFilter authenticationByInternalApiKeyFilter(AuthenticationManager authenticationManager)
        throws Exception {
        AuthenticationByInternalApiKeyAuthenticationFilter filter = new AuthenticationByInternalApiKeyAuthenticationFilter(securityInternalApiKeyProperties.getHeaderName());
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(authenticationByInternalApiKeySuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationByInternalApiKeyFailureHandler());
        return filter;
    }

    /**
     * The service used to authenticate the user according to the received JWT.
     * @return the instance of the service.
     */
    @Bean
    public AuthenticationByInternalApiKeyUserDetailsService authenticationByInternalApiKeyUserDetailsService() {
        return new AuthenticationByInternalApiKeyUserDetailsService();
    }

    /**
     * Defines a pre-authenticated authentication provider.
     * @return the preAuthenticatedAuthenticationProvider.
     */
    @Bean
    public AuthenticationByInternalApiKeyAuthenticationProvider authenticationByInternalApiKeyAuthenticationProvider() {
        AuthenticationByInternalApiKeyAuthenticationProvider preAuthenticatedAuthenticationProvider = new AuthenticationByInternalApiKeyAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(authenticationByInternalApiKeyUserDetailsService());
        return preAuthenticatedAuthenticationProvider;
    }

    /**
     * The authentication success handler.
     * @return the authentication success handler.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationByInternalApiKeySuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public AuthenticationFailureHandler authenticationByInternalApiKeyFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}