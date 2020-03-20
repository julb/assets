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

import io.julb.springbootstarter.security.services.impl.PreAuthenticatedUserDetailsService;

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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * The security configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableWebSecurity
@Import({SecurityInternalApiKeyConfiguration.class, SecurityInternalJwtConfiguration.class})
@AutoConfigureAfter({SecurityInternalApiKeyConfiguration.class, SecurityInternalJwtConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * The application context.
     */
    @Autowired
    private GenericApplicationContext applicationContext;

    /**
     * The unauthorized entrypoint.
     * @return the unauthorized entrypoint.
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedAuthenticationEntrypoint() {
        return new UnauthorizedAuthenticationEntrypoint();
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
     * The service used to authenticate the user according to the received JWT.
     * @return the instance of the service.
     */
    @Bean
    public PreAuthenticatedUserDetailsService preAuthenticatedUserDetailsService() {
        return new PreAuthenticatedUserDetailsService();
    }

    /**
     * Defines a pre-authenticated authentication provider.
     * @return the preAuthenticatedAuthenticationProvider.
     */
    @Bean
    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(preAuthenticatedUserDetailsService());
        return preAuthenticatedAuthenticationProvider;
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http)
        throws Exception {
        // Add filters.
        Map<String, RequestHeaderAuthenticationFilter> beansOfType = applicationContext.getBeansOfType(RequestHeaderAuthenticationFilter.class);
        for (RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter : beansOfType.values()) {
            http.addFilterBefore(requestHeaderAuthenticationFilter, AnonymousAuthenticationFilter.class);
        }

        // Configure security.
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
                .csrf().disable();
        //@formatter:on
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider());
    }
}
