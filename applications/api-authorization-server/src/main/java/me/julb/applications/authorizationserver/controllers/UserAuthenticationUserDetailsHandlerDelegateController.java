/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.services.UserAuthenticationGenericService;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.dto.security.UserAuthenticationUserDetailsDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFirstCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenWithIdTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionWithRawIdTokenDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;
import me.julb.springbootstarter.web.utility.HttpServletRequestUtility;

/**
 * The delegate service implementation for user authentication details.
 * <P>
 * @author Julb.
 */
@Service
public class UserAuthenticationUserDetailsHandlerDelegateController implements IAuthenticationUserDetailsHandlerDelegate {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationUserDetailsHandlerDelegateController.class);

    /**
     * The REMEMBERME_PARAMETER attribute.
     */
    private static final String REMEMBERME_PARAMETER = "rememberMe";

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
     * The JSON object mapper.
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * /** {@inheritDoc}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            LOGGER.info("Authentication successful.");

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserAuthenticationUserDetailsDTO) {
                UserAuthenticationUserDetailsDTO dto = (UserAuthenticationUserDetailsDTO) principal;
                String userId = dto.getCredentials().getUser().getId();
                String id = dto.getCredentials().getUserAuthentication().getId();

                // Update successful.
                userAuthenticationGenericService.updateSuccessfulUse(userId, id);

                // Generate session.
                UserAuthenticationType type = dto.getCredentials().getUserAuthentication().getType();
                if (UserAuthenticationType.PASSWORD.equals(type) || UserAuthenticationType.PINCODE.equals(type) || UserAuthenticationType.API_KEY.equals(type)) {
                    // Remember me ?
                    Boolean rememberMe = obtainRememberMe(request);

                    // Prepare user session creation.
                    UserSessionCreationDTO userSessionCreationDTO = new UserSessionCreationDTO();
                    if (dto.getMfaEnabled()) {
                        userSessionCreationDTO.setMfaVerified(Boolean.FALSE);
                    }

                    if (rememberMe) {
                        userSessionCreationDTO.setDurationInSeconds(TimeUnit.DAYS.toSeconds(Integers.THIRTY));
                    } else {
                        userSessionCreationDTO.setDurationInSeconds(TimeUnit.HOURS.toSeconds(Integers.TWO));
                    }

                    // Generate session
                    UserSessionWithRawIdTokenDTO session = userSessionService.create(userId, userSessionCreationDTO);

                    // Generate access token
                    UserSessionAccessTokenFirstCreationDTO creationDTO = new UserSessionAccessTokenFirstCreationDTO();
                    creationDTO.setBrowser(HttpServletRequestUtility.getBrowser(request));
                    creationDTO.setIpv4Address(HttpServletRequestUtility.getUserIpv4Address(request));
                    creationDTO.setLastUseDateTime(DateUtility.dateTimeNow());
                    creationDTO.setOperatingSystem(HttpServletRequestUtility.getOperatingSystem(request));
                    UserSessionAccessTokenDTO accessTokenDTO = userSessionService.createAccessTokenFirst(userId, session.getId(), creationDTO);

                    // Return result.
                    UserSessionAccessTokenWithIdTokenDTO sessionToken = new UserSessionAccessTokenWithIdTokenDTO();
                    sessionToken.setIdToken(session.getRawIdToken());
                    sessionToken.setAccessToken(accessTokenDTO.getAccessToken());
                    sessionToken.setExpiresIn(accessTokenDTO.getExpiresIn());
                    sessionToken.setType(accessTokenDTO.getType());

                    // Write JSON response to body.
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getOutputStream(), sessionToken);
                    response.flushBuffer();
                } else if (UserAuthenticationType.TOTP.equals(type)) {
                    // Get MFA session ID.
                    UserSessionDTO userSession = userSessionService.markMfaAsVerified(userId, dto.getMfaSessionId());

                    // Regenerate an access token.
                    UserSessionAccessTokenFirstCreationDTO creationDTO = new UserSessionAccessTokenFirstCreationDTO();
                    creationDTO.setBrowser(HttpServletRequestUtility.getBrowser(request));
                    creationDTO.setIpv4Address(HttpServletRequestUtility.getUserIpv4Address(request));
                    creationDTO.setLastUseDateTime(DateUtility.dateTimeNow());
                    creationDTO.setOperatingSystem(HttpServletRequestUtility.getOperatingSystem(request));
                    UserSessionAccessTokenDTO accessTokenDTO = userSessionService.createAccessTokenFirst(userId, userSession.getId(), creationDTO);

                    // Write JSON response to body.
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getOutputStream(), accessTokenDTO);
                    response.flushBuffer();
                }
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
    }

    /**
     * Fetches the rememberMe attribute.
     * @param request so that request attributes can be retrieved
     * @return the rememberMe attribute.
     */
    protected Boolean obtainRememberMe(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(REMEMBERME_PARAMETER));
    }
}
