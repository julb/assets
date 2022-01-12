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

package me.julb.applications.authorizationserver.controllers;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.stereotype.Service;

import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.services.UserAuthenticationGenericService;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.dto.security.UserAuthenticationUserDetailsDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFirstCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFromIdTokenCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionCreationDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;
import me.julb.springbootstarter.web.reactive.utility.ServerHttpRequestUtility;

import reactor.core.publisher.Mono;

/**
 * The delegate service implementation for user authentication details.
 * <br>
 * @author Julb.
 */
@Service
@Slf4j
public class UserAuthenticationUserDetailsHandlerDelegateController implements IAuthenticationUserDetailsHandlerDelegate {

    /**
     * The REMEMBERME_PARAMETER attribute.
     */
    private static final String REMEMBERME_PARAMETER = "rememberMe";

    /**
     * The HTTP user access token service.
     */
    @Autowired
    private HttpUserAccessTokenService httpUserAccessTokenService;

    /**
     * The user authentication generic service.
     */
    @Autowired
    private UserAuthenticationGenericService userAuthenticationGenericService;

    /**
     * The user session service.
     */
    @Autowired
    private UserSessionService userSessionService;

    /**
     * /** {@inheritDoc}
     */
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        LOGGER.info("Authentication successful.");
        ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserAuthenticationUserDetailsDTO dto) {
            String userId = dto.getCredentials().getUser().getId();
            String id = dto.getCredentials().getUserAuthentication().getId();

            // Update successful.
            return userAuthenticationGenericService.updateSuccessfulUse(userId, id).flatMap(authenticationUpdated -> {
                // Generate session.
                UserAuthenticationType type = dto.getCredentials().getUserAuthentication().getType();
                if (UserAuthenticationType.PASSWORD.equals(type) || UserAuthenticationType.PINCODE.equals(type) || UserAuthenticationType.API_KEY.equals(type)) {
                    // Remember me ?
                    Boolean rememberMe = obtainRememberMe(request);

                    // Prepare user session creation.
                    UserSessionCreationDTO userSessionCreationDTO = new UserSessionCreationDTO();
                    if (dto.getMfaEnabled().booleanValue()) {
                        userSessionCreationDTO.setMfaVerified(Boolean.FALSE);
                    }

                    if (rememberMe.booleanValue()) {
                        userSessionCreationDTO.setDurationInSeconds(TimeUnit.DAYS.toSeconds(Integers.THIRTY));
                    } else {
                        userSessionCreationDTO.setDurationInSeconds(TimeUnit.HOURS.toSeconds(Integers.TWO));
                    }

                    // Generate session
                    return userSessionService.create(userId, userSessionCreationDTO).flatMap(session -> {
                        // Generate access token
                        UserSessionAccessTokenFromIdTokenCreationDTO creationDTO = new UserSessionAccessTokenFromIdTokenCreationDTO();
                        creationDTO.setBrowser(Objects.toString(ServerHttpRequestUtility.getBrowser(request), null));
                        creationDTO.setIpv4Address(ServerHttpRequestUtility.getUserIpAddress(request));
                        creationDTO.setLastUseDateTime(DateUtility.dateTimeNow());
                        creationDTO.setOperatingSystem(Objects.toString(ServerHttpRequestUtility.getOperatingSystem(request)));
                        creationDTO.setRawIdToken(session.getRawIdToken());
                        return userSessionService.createAccessTokenFromIdToken(creationDTO).flatMap(sessionToken -> {
                            // Write JSON response to body.
                            return httpUserAccessTokenService.writeResponseWithIdToken(sessionToken, response);
                        });
                    });
                } else if (UserAuthenticationType.TOTP.equals(type)) {
                    // Get MFA session ID.
                    return userSessionService.markMfaAsVerified(userId, dto.getMfaSessionId()).flatMap(userSession -> {
                        // Regenerate an access token.
                        UserSessionAccessTokenFirstCreationDTO creationDTO = new UserSessionAccessTokenFirstCreationDTO();
                        creationDTO.setBrowser(Objects.toString(ServerHttpRequestUtility.getBrowser(request)));
                        creationDTO.setIpv4Address(ServerHttpRequestUtility.getUserIpAddress(request));
                        creationDTO.setLastUseDateTime(DateUtility.dateTimeNow());
                        creationDTO.setOperatingSystem(Objects.toString(ServerHttpRequestUtility.getOperatingSystem(request)));
                        return userSessionService.createAccessTokenFirst(userId, userSession.getId(), creationDTO).flatMap(sessionToken -> {
                            // Write JSON response to body.
                            return httpUserAccessTokenService.writeResponseWithoutIdToken(sessionToken, response);
                        });
                    });
                } else {
                    return Mono.empty();
                }
            });            
        } else {
            return Mono.empty();
        }
    }

    /**
     * Fetches the rememberMe attribute.
     * @param request so that request attributes can be retrieved
     * @return the rememberMe attribute.
     */
    protected Boolean obtainRememberMe(ServerHttpRequest request) {
        return Boolean.parseBoolean(request.getQueryParams().getFirst(REMEMBERME_PARAMETER));
    }
}
