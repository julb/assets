/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package io.julb.springbootstarter.security.configurations;

import io.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import io.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationLogoutHandler;
import io.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsLogoutHandlerDelegate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * The security configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableWebSecurity
//@formatter:off
@Import({
    SecurityAuthenticationByApiKeyConfiguration.class, 
    SecurityAuthenticationByInternalApiKeyConfiguration.class, 
    SecurityAuthenticationByJwtConfiguration.class, 
    SecurityAuthenticationByPasswordConfiguration.class, 
    SecurityAuthenticationByPincodeConfiguration.class, 
    SecurityAuthenticationByTotpConfiguration.class
})
@AutoConfigureAfter({
    SecurityAuthenticationByApiKeyConfiguration.class, 
    SecurityAuthenticationByInternalApiKeyConfiguration.class, 
    SecurityAuthenticationByJwtConfiguration.class, 
    SecurityAuthenticationByPasswordConfiguration.class, 
    SecurityAuthenticationByPincodeConfiguration.class, 
    SecurityAuthenticationByTotpConfiguration.class
})
//@formatter:on
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * The application context.
     */
    @Autowired
    private GenericApplicationContext applicationContext;

    /**
     * The authentication user details logout handler.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsLogoutHandlerDelegate authenticationUserDetailsLogoutHandlerDelegate;

    /**
     * The unauthorized entrypoint.
     * @return the unauthorized entrypoint.
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedAuthenticationEntrypoint() {
        return new CustomAuthenticationFailureHandler();
    }

    /**
     * Exposes the authentication manager bean.
     * @return the authentication manager.
     * @throws Exception if an error occurs.
     */
    @Bean
    public AuthenticationManager applicationAuthenticationManager()
        throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * The logout handler.
     * @return the logout handler.
     */
    @Bean
    public CustomAuthenticationLogoutHandler logoutHandler() {
        CustomAuthenticationLogoutHandler customAuthenticationLogoutHandler = new CustomAuthenticationLogoutHandler();
        customAuthenticationLogoutHandler.setAuthenticationUserDetailsLogoutHandlerDelegate(authenticationUserDetailsLogoutHandlerDelegate);
        return customAuthenticationLogoutHandler;
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http)
        throws Exception {
        //@formatter:off
        http
            .authorizeRequests()
                .antMatchers("/v3/api-docs").permitAll()
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class, PrometheusScrapeEndpoint.class)).permitAll()
                .requestMatchers(EndpointRequest.to(CachesEndpoint.class)).hasRole("ACTUATOR")
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedAuthenticationEntrypoint())
            .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .httpBasic().disable()
                .csrf().disable()
            .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler());
        //@formatter:on
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        Map<String, AuthenticationProvider> beansOfType = applicationContext.getBeansOfType(AuthenticationProvider.class);
        for (AuthenticationProvider authenticationProvider : beansOfType.values()) {
            auth.authenticationProvider(authenticationProvider);
        }
    }

}
