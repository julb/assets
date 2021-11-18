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

import java.io.IOException;
import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.library.utility.josejwt.TokenReceiver;
import me.julb.library.utility.josejwt.jwk.IJWKSetProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualAsymmetricJWKProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualJWKSetProvider;
import me.julb.library.utility.josejwt.jwk.impl.RemoteUrlJWKSetProvider;
import me.julb.library.utility.josejwt.keyloader.PEMKeyLoader;
import me.julb.springbootstarter.security.configurations.beans.filters.AuthenticationByJwtAuthenticationFilter;
import me.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationFailureHandler;
import me.julb.springbootstarter.security.configurations.beans.handlers.CustomAuthenticationSuccessHandler;
import me.julb.springbootstarter.security.configurations.beans.providers.AuthenticationByJwtAuthenticationProvider;
import me.julb.springbootstarter.security.configurations.beans.userdetails.AuthenticationByJwtUserDetailsService;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsPermissionDelegateService;
import me.julb.springbootstarter.security.configurations.properties.SecurityJwtProperties;

/**
 * The security JWT configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.jwt", name = "enabled", matchIfMissing = false)
@EnableConfigurationProperties(SecurityJwtProperties.class)
public class SecurityAuthenticationByJwtConfiguration {

    /**
     * The authentication by JWT delegate service.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsPermissionDelegateService authenticationByJwtUserDetailsDelegateService;

    /**
     * The authentication handler delegate service.
     */
    @Autowired(required = false)
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * The security JWT properties.
     */
    @Autowired
    private SecurityJwtProperties securityJwtProperties;

    /**
     * The internal token verifier.
     * @return the internal token verifier.
     */
    @Bean
    public TokenReceiver jwtTokenReceiver() {
        try {
            IJWKSetProvider jwkSetProvider = null;

            if (securityJwtProperties.getSignatureKey() != null) {
                PublicKey publicKey;
                publicKey = PEMKeyLoader.loadPublicKey(securityJwtProperties.getSignatureKey().getKeyPath().getInputStream());

                //@formatter:off
                ManualAsymmetricJWKProvider signatureKey = new ManualAsymmetricJWKProvider.Builder()
                    .algorithm(securityJwtProperties.getSignatureKey().getAlgorithm())
                    .publicKey(publicKey)
                    .keyId(securityJwtProperties.getSignatureKey().getKeyId())
                    .useForSignature()
                    .build();
                //@formatter:on

                jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(signatureKey).build();
            } else if (securityJwtProperties.getSignatureJwksUrl() != null) {
                //@formatter:off
                jwkSetProvider = new RemoteUrlJWKSetProvider.Builder()
                    .url(securityJwtProperties.getSignatureJwksUrl())
                    .build();
                //@formatter:on
            }

            return new TokenReceiver().setSignatureJWKSetProvider(jwkSetProvider);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Filters the request to get the JWT.
     * @param authenticationManager the Spring authentication manager.
     * @return the request header authentication filter.
     */
    @Bean
    public RequestHeaderAuthenticationFilter authenticationByJwtFilter(AuthenticationManager authenticationManager) {
        AuthenticationByJwtAuthenticationFilter filter = new AuthenticationByJwtAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(authenticationByJwtSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationByJwtFailureHandler());
        return filter;
    }

    /**
     * The service used to authenticate the user according to the received JWT.
     * @return the instance of the service.
     */
    @Bean
    public AuthenticationByJwtUserDetailsService authenticationByJwtUserDetailsService() {
        AuthenticationByJwtUserDetailsService authenticationByJwtUserDetailsService = new AuthenticationByJwtUserDetailsService();
        authenticationByJwtUserDetailsService.setAuthenticationByJwtUserDetailsDelegateService(authenticationByJwtUserDetailsDelegateService);
        authenticationByJwtUserDetailsService.setTokenReceiver(jwtTokenReceiver());
        return authenticationByJwtUserDetailsService;
    }

    /**
     * Defines a pre-authenticated authentication provider.
     * @return the preAuthenticatedAuthenticationProvider.
     */
    @Bean
    public AuthenticationByJwtAuthenticationProvider authenticationByJwtAuthenticationProvider() {
        AuthenticationByJwtAuthenticationProvider preAuthenticatedAuthenticationProvider = new AuthenticationByJwtAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(authenticationByJwtUserDetailsService());
        return preAuthenticatedAuthenticationProvider;
    }

    /**
     * The authentication success handler.
     * @return the authentication success handler.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationByJwtSuccessHandler() {
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler();
        customAuthenticationSuccessHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationSuccessHandler;
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public AuthenticationFailureHandler authenticationByJwtFailureHandler() {
        CustomAuthenticationFailureHandler customAuthenticationFailureHandler = new CustomAuthenticationFailureHandler();
        customAuthenticationFailureHandler.setAuthenticationUserDetailsDelegateService(authenticationUserDetailsDelegateService);
        return customAuthenticationFailureHandler;
    }
}