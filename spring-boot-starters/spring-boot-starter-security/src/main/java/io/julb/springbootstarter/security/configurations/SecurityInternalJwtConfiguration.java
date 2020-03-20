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

import io.julb.library.utility.josejwt.jwk.impl.ManualJWKSetProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualSymmetricJWKProvider;
import io.julb.library.utility.josejwt.operations.TokenVerifierOperation;
import io.julb.springbootstarter.security.configurations.properties.SecurityInternalJwtProperties;
import io.julb.springbootstarter.security.filters.AuthorizationBearerTokenRequestHeaderAuthenticationFilter;
import io.julb.springbootstarter.security.services.impl.JWTAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * The security JWT configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@ConditionalOnProperty(prefix = "security.internal-jwt", name = "enabled", matchIfMissing = false)
@EnableConfigurationProperties(SecurityInternalJwtProperties.class)
public class SecurityInternalJwtConfiguration {

    /**
     * The internal token verifier.
     * @param internalTokenForgery the elements to generate an internal token verifier.
     * @return the internal token verifier.
     */
    @Bean
    public TokenVerifierOperation internalTokenVerifier(SecurityInternalJwtProperties internalTokenForgery) {
        //@formatter:off
        ManualSymmetricJWKProvider signatureKey = new ManualSymmetricJWKProvider.Builder()
            .algorithm(internalTokenForgery.getSignatureAlgorithm())
            .secretKey(internalTokenForgery.getSignatureKey())
            .keyId(internalTokenForgery.getSignatureKeyId())
            .useForSignature()
            .build();
        ManualJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(signatureKey).build();
        //@formatter:on

        return new TokenVerifierOperation(jwkSetProvider, internalTokenForgery.getAudience(), internalTokenForgery.getIssuer());
    }

    /**
     * Filters the request to get the JWT.
     * @param authenticationManager the authentication manager.
     * @return the request header authentication filter.
     * @throws Exception if an error occurs.
     */
    @Bean
    public RequestHeaderAuthenticationFilter jwtRequestHeaderAuthenticationFilter(AuthenticationManager authenticationManager)
        throws Exception {
        AuthorizationBearerTokenRequestHeaderAuthenticationFilter filter = new AuthorizationBearerTokenRequestHeaderAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(jwtAuthenticationFailureHandler());
        filter.setAuthenticationDetailsSource(jwtAuthenticationDetailsSource());
        return filter;
    }

    /**
     * Builds an authentication details source for JWT authentication.
     * @return the authentication details source.
     */
    @Bean
    public AuthenticationDetailsSource<HttpServletRequest, UserDetails> jwtAuthenticationDetailsSource() {
        return new JWTAuthenticationDetailsSource();
    }

    /**
     * The authentication failure handler.
     * @return the authentication failure handler.
     */
    @Bean
    public AuthenticationFailureCustomHandler jwtAuthenticationFailureHandler() {
        return new AuthenticationFailureCustomHandler();
    }
}