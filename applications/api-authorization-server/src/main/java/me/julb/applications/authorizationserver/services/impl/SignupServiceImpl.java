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

import java.util.HashSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mappers.UserEntityMapper;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.services.SignupService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPasswordService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.UserPreferencesService;
import me.julb.applications.authorizationserver.services.UserProfileService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileDTO;
import me.julb.applications.authorizationserver.services.dto.signup.AbstractSignupCreationDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithInviteDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.security.UserRole;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Mono;

/**
 * The sign-up service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SignupServiceImpl implements SignupService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user mail service.
     */
    @Autowired
    private UserMailService userMailService;

    /**
     * The user profile service.
     */
    @Autowired
    private UserProfileService userProfileService;

    /**
     * The user preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;

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
     * The mapper.
     */
    @Autowired
    private UserEntityMapper mapper;

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    /**
     * The async message poster service.
     */
    @Autowired
    private AsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserDTO> signup(@NotNull @Valid SignupWithInviteDTO signupWithInvite) {
        return signupUser(signupWithInvite).map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserDTO> signup(@NotNull @Valid SignupWithPasswordCreationDTO signupWithPassword) {
        return signupUser(signupWithPassword).flatMap(result -> {
            // Save password.
            UserAuthenticationByPasswordCreationDTO dto = new UserAuthenticationByPasswordCreationDTO();
            dto.setPassword(signupWithPassword.getPassword());
            return userAuthenticationByPasswordService.create(result.getId(), dto).thenReturn(mapper.map(result));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserDTO> signup(@NotNull @Valid SignupWithPincodeCreationDTO signupWithPincode) {
        return signupUser(signupWithPincode).flatMap(result -> {
            // Save password.
            UserAuthenticationByPincodeCreationDTO dto = new UserAuthenticationByPincodeCreationDTO();
            dto.setPincode(signupWithPincode.getPincode());
            return userAuthenticationByPincodeService.create(result.getId(), dto).thenReturn(mapper.map(result));
        });
    }

    // ------------------------------------------ Utility methods.

    /**
     * Sign-ups the user using the sign-up DTO.
     * @param signup the signup DTO.
     * @return the created user.
     */
    protected Mono<UserEntity> signupUser(AbstractSignupCreationDTO signup) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            CustomLocaleContext localeContext = ctx.get(ContextConstants.LOCALE);

            return userMailService.existsByMail(signup.getMail()).flatMap(alreadyExists -> {
                if (alreadyExists.booleanValue()) {
                    return Mono.error(new ResourceAlreadyExistsException(UserEntity.class, "mail", signup.getMail()));
                }

                UserEntity entityToCreate = mapper.map(signup);
                return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                    return userRepository.save(entityToCreateWithFields).flatMap(result -> {
                        // Create profile settings
                        Mono<UserProfileDTO> userProfile = userProfileService.create(result.getId(), signup.getProfile());

                        // Create preferences settings
                        UserPreferencesCreationDTO userPreferencesCreationDTO = new UserPreferencesCreationDTO();
                        userPreferencesCreationDTO.setLanguage(localeContext.getLocale());
                        Mono<UserPreferencesDTO> userPreferences = userPreferencesService.create(result.getId(), userPreferencesCreationDTO);

                        // Create mail settings.
                        UserMailCreationDTO creationDTO = new UserMailCreationDTO();
                        creationDTO.setMail(signup.getMail());
                        creationDTO.setPrimary(true);
                        Mono<UserMailDTO> userMail = userMailService.create(result.getId(), creationDTO).flatMap(mailCreated -> {
                            return userMailService.triggerMailVerify(result.getId(), mailCreated.getId());
                        });
                        
                        return Mono.zip(userProfile, userPreferences, userMail).thenReturn(result);
                    });
                });
            });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an user.
     * @param entity the entity.
     */
    private Mono<UserEntity> onPersist(String tm, UserEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setAccountNonLocked(false);
        entity.setEnabled(true);
        entity.setRoles(new HashSet<UserRole>());
        entity.getRoles().add(UserRole.USER);

        // Post event
        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserEntity> postResourceEvent(UserEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
