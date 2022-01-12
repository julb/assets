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

package me.julb.applications.authorizationserver.services.impl;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByApiKeyUserDetailsDelegateService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByPasswordUserDetailsDelegateService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByPincodeUserDetailsDelegateService;
import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByTotpUserDetailsDelegateService;

import reactor.core.publisher.Mono;

/**
 * The delegate service implementation for user authentication details.
 * <br>
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
    public Mono<UserDetails> loadUserDetailsByApiKey(@NotNull @NotBlank @SecureApiKey String apiKey) {
        return userAuthenticationByApiKeyService.findOneCredentials(apiKey)
            .onErrorMap(ResourceNotFoundException.class, e -> new UsernameNotFoundException("User not found.", e))
            .map(this::buildUserDetails);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDetails> loadUserDetailsByPassword(@NotNull @NotBlank @Email String mail) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(mail)))
                .flatMap(userMail -> {
                    return userAuthenticationByPasswordService.findOneCredentials(userMail.getUser().getId())
                        .onErrorMap(ResourceNotFoundException.class, e -> new UsernameNotFoundException("User not found.", e))
                        .map(this::buildUserDetails);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDetails> loadUserDetailsByPincode(@NotNull @NotBlank @Email String mail) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(mail)))
                .flatMap(userMail -> {
                    return userAuthenticationByPincodeService.findOneCredentials(userMail.getUser().getId())
                        .onErrorMap(ResourceNotFoundException.class, e -> new UsernameNotFoundException("User not found.", e))
                        .map(this::buildUserDetails);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDetails> loadUserDetailsByTotp(@NotNull @NotBlank @Identifier String userId, @NotNull @NotBlank @Identifier String sessionId, @NotNull @NotBlank @Identifier String deviceId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, sessionId)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(sessionId)))
                .flatMap(userSession -> {
                    return userAuthenticationByTotpService.findOneCredentials(userSession.getUser().getId(), deviceId)
                        .onErrorMap(ResourceNotFoundException.class, e -> new UsernameNotFoundException("User not found.", e))
                        .map(this::buildUserDetails)
                        .map(buildUserDetails -> {
                            buildUserDetails.setMfaSessionId(sessionId);
                            return buildUserDetails;
                        });
                });
        });
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
