/**
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
package io.julb.springbootstarter.security.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.julb.library.dto.security.AuthenticatedUserDTO;
import io.julb.library.utility.exceptions.BadRequestException;
import io.julb.library.utility.http.HttpHeaderUtility;
import io.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.JOSEJWTExceptionConverterUtility;
import io.julb.library.utility.josejwt.operations.TokenVerifierOperation;
import io.julb.springbootstarter.security.services.dto.CustomUserDetails;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A service used to pre-authenticate users according to the given header.
 * <P>
 * @author Julb.
 */
public class JWTAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, UserDetails> {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationDetailsSource.class);

    /**
     * The internal token verifier.
     */
    @Autowired
    private TokenVerifierOperation internalTokenVerifier;

    /**
     * The object mapper.
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Configure the object mapper.
     */
    @PostConstruct
    private void configureObjectMapper() {
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails buildDetails(HttpServletRequest context) {
        try {
            String bearerToken = context.getHeader(HttpHeaders.AUTHORIZATION);

            LOGGER.debug("Entering authentication with bearer token.");

            // Extract value from bearer.
            String token = HttpHeaderUtility.fromBearerToken(bearerToken);
            if (StringUtils.isNotBlank(token)) {
                String payload = this.internalTokenVerifier.execute(token);

                // Build authentication details.
                AuthenticatedUserDTO authenticatedUser = this.objectMapper.readValue(payload, AuthenticatedUserDTO.class);
                return new CustomUserDetails(authenticatedUser);
            } else {
                return null;
            }
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new BadRequestException(e);
        } catch (JOSEJWTException e) {
            LOGGER.error(e.getMessage(), e);
            throw JOSEJWTExceptionConverterUtility.convertJOSEJWTException(e);
        }
    }

}
