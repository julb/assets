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

package me.julb.applications.authorizationserver.services.impl;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserSessionRepository;
import me.julb.applications.authorizationserver.services.UserAuthenticationByApiKeyService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPasswordService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByTotpService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.applications.authorizationserver.services.dto.security.UserAuthenticationUserDetailsDTO;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.SecureApiKey;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByApiKeyUserDetailsDelegateService;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByPasswordUserDetailsDelegateService;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByPincodeUserDetailsDelegateService;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByTotpUserDetailsDelegateService;

/**
 * The delegate service implementation for user authentication details.
 * <P>
 * @author Julb.
 */
@Service
public class UserAuthenticationDetailsDelegateServiceImpl
    implements IAuthenticationByApiKeyUserDetailsDelegateService, IAuthenticationByPasswordUserDetailsDelegateService, IAuthenticationByPincodeUserDetailsDelegateService, IAuthenticationByTotpUserDetailsDelegateService {

    /**
     * The user mail repository.
     */
    @Autowired
    private UserMailRepository userMailRepository;

    /**
     * The user session repository.
     */
    @Autowired
    private UserSessionRepository userSessionRepository;

    /**
     * The user authentication by API key service.
     */
    @Autowired
    private UserAuthenticationByApiKeyService userAuthenticationByApiKeyService;

    /**
     * The user authentication by password service.
     */
    @Autowired
    private UserAuthenticationByPasswordService userAuthenticationByPasswordService;

    /**
     * The user authentication by pincode service.
     */
    @Autowired
    private UserAuthenticationByPincodeService userAuthenticationByPincodeService;

    /**
     * The user authentication by TOTP service.
     */
    @Autowired
    private UserAuthenticationByTotpService userAuthenticationByTotpService;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserDetailsByApiKey(@NotNull @NotBlank @SecureApiKey String apiKey) {
        try {
            UserAuthenticationCredentialsDTO credentials = userAuthenticationByApiKeyService.findOneCredentials(apiKey);
            return buildUserDetails(credentials);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User not found.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserDetailsByPassword(@NotNull @NotBlank @Email String mail) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get the user with the mail.
        UserMailEntity userMail = userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail);
        if (userMail == null) {
            throw new UsernameNotFoundException(mail);
        }

        // Finds the credentials.
        try {
            UserAuthenticationCredentialsDTO credentials = userAuthenticationByPasswordService.findOneCredentials(userMail.getUser().getId());
            return buildUserDetails(credentials);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User not found.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserDetailsByPincode(@NotNull @NotBlank @Email String mail) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get the user with the mail.
        UserMailEntity userMail = userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail);
        if (userMail == null) {
            throw new UsernameNotFoundException(mail);
        }

        // Finds the credentials.
        try {
            UserAuthenticationCredentialsDTO credentials = userAuthenticationByPincodeService.findOneCredentials(userMail.getUser().getId());
            return buildUserDetails(credentials);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User not found.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserDetailsByTotp(@NotNull @NotBlank @Identifier String userId, @NotNull @NotBlank @Identifier String sessionId, @NotNull @NotBlank @Identifier String deviceId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get the user with the mail.
        UserSessionEntity userSession = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, sessionId);
        if (userSession == null) {
            throw new UsernameNotFoundException(sessionId);
        }

        // Finds the credentials.
        try {
            UserAuthenticationCredentialsDTO credentials = userAuthenticationByTotpService.findOneCredentials(userSession.getUser().getId(), deviceId);
            UserAuthenticationUserDetailsDTO buildUserDetails = buildUserDetails(credentials);
            buildUserDetails.setMfaSessionId(sessionId);
            return buildUserDetails;
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User not found.", e);
        }
    }

    /**
     * Builds a user details based on the given credentials.
     * @param credentials the credentials.
     * @return the user details.
     */
    protected UserAuthenticationUserDetailsDTO buildUserDetails(UserAuthenticationCredentialsDTO credentials) {
        return new UserAuthenticationUserDetailsDTO(credentials);
    }
}
