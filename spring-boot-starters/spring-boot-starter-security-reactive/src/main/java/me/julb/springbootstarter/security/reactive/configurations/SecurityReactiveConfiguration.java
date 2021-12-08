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
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import me.julb.springbootstarter.security.reactive.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.reactive.configurations.beans.handlers.CustomAuthenticationLogoutHandler;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsLogoutHandlerDelegate;

/**
 * The security configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
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
public class SecurityReactiveConfiguration {

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
    public ServerAuthenticationEntryPoint unauthorizedAuthenticationEntrypoint() {
        return new CustomAuthenticationFailureHandler();
    }

    /**
     * The logout handler.
     * @return the logout handler.
     */
    @Bean
    public ServerLogoutHandler logoutHandler() {
        CustomAuthenticationLogoutHandler customAuthenticationLogoutHandler = new CustomAuthenticationLogoutHandler();
        customAuthenticationLogoutHandler.setAuthenticationUserDetailsLogoutHandlerDelegate(authenticationUserDetailsLogoutHandlerDelegate);
        return customAuthenticationLogoutHandler;
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http)
        throws Exception {
        //@formatter:off
        return http
            .authorizeExchange()
                .pathMatchers("/v3/api-docs").permitAll()
                .matchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class, PrometheusScrapeEndpoint.class)).permitAll()
                .matchers(EndpointRequest.to(CachesEndpoint.class)).hasRole("ACTUATOR")
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedAuthenticationEntrypoint())
            .and()
                .httpBasic().disable()
                .csrf().disable()
            .logout()
                .logoutUrl("/logout")
                .logoutHandler(logoutHandler())
            .and()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .build();
        //@formatter:on
    }
}
