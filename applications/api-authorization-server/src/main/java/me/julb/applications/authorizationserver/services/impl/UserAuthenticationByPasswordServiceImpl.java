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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.entities.authentication.mappers.UserAuthenticationEntityMapper;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.mappers.UserEntityMapper;
import me.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByPasswordRepository;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByTotpRepository;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserMobilePhoneRepository;
import me.julb.applications.authorizationserver.repositories.UserPreferencesRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPasswordService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPasswordChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPasswordResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordTriggerPasswordResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.applications.authorizationserver.services.dto.recovery.RecoveryChannelType;
import me.julb.applications.authorizationserver.services.exceptions.InvalidPasswordException;
import me.julb.applications.authorizationserver.services.exceptions.InvalidPasswordResetTokenException;
import me.julb.applications.authorizationserver.services.exceptions.PasswordResetTokenExpiredException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.BadRequestException;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.random.RandomUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.messaging.reactive.builders.NotificationDispatchAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

import reactor.core.publisher.Mono;

/**
 * The user authentication by password service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAuthenticationByPasswordServiceImpl implements UserAuthenticationByPasswordService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user mail repository.
     */
    @Autowired
    private UserMailRepository userMailRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserMobilePhoneRepository userMobilePhoneRepository;

    /**
     * The user preferences repository.
     */
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    /**
     * The user authentication repository.
     */
    @Autowired
    private UserAuthenticationByPasswordRepository userAuthenticationByPasswordRepository;

    /**
     * The user authentication by TOTP repository.
     */
    @Autowired
    private UserAuthenticationByTotpRepository userAuthenticationByTotpRepository;

    /**
     * The mapper.
     */
    @Autowired
    private UserAuthenticationEntityMapper mapper;

    /**
     * The user mapper.
     */
    @Autowired
    private UserEntityMapper userMapper;

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

    /**
     * The config source service.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    /**
     * The password encoder.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationByPasswordDTO> findOne(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationCredentialsDTO> findOneCredentials(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .map(result -> {
                            // Get credentials
                            UserAuthenticationCredentialsDTO credentials = new UserAuthenticationCredentialsDTO();
                            credentials.setUniqueCredentials(result.getSecuredPassword());
                            if (StringUtils.isNotBlank(result.getPasswordExpiryDateTime())) {
                                credentials.setCredentialsNonExpired(DateUtility.dateTimeNow().compareTo(result.getPasswordExpiryDateTime()) <= 0);
                            } else {
                                credentials.setCredentialsNonExpired(true);
                            }
                            credentials.setUserAuthentication(mapper.map(result));
                            credentials.setUser(userMapper.map(result.getUser()));

                            return credentials;
                        });
                });
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPasswordDTO> create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .flatMap(alreadyExists -> {
                            if (alreadyExists.booleanValue()) {
                                return Mono.error(new ResourceAlreadyExistsException(UserAuthenticationByPasswordEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.PASSWORD.toString())));
                            }

                            UserAuthenticationByPasswordEntity entityToCreate = mapper.map(creationDTO);
                            entityToCreate.setUser(user);
                            entityToCreate.setMfaEnabled(Boolean.FALSE);
                            return this.onPersist(tm, entityToCreate)
                                .flatMap(entityToCreateWithFields -> updatePassword(entityToCreateWithFields, creationDTO.getPassword()));
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPasswordDTO> update(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .flatMap(existing -> {
                            if (updateDTO.getMfaEnabled().booleanValue() && !existing.getMfaEnabled().booleanValue()) {
                                return userAuthenticationByTotpRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.TOTP)
                                    .flatMap(deviceExists -> {
                                        if (!deviceExists.booleanValue()) {
                                            return Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, Map.of("userId", userId)));
                                        }
                                        // Update the entity
                                        mapper.map(updateDTO, existing);
                            
                                        // Proceed to the update
                                        return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                            return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
                                        });
                                    });
                            } else {
                                // Update the entity
                                mapper.map(updateDTO, existing);
                    
                                // Proceed to the update
                                return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
                                });
                            }
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPasswordDTO> patch(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .flatMap(existing -> {
                            if (patchDTO.getMfaEnabled() != null && patchDTO.getMfaEnabled().booleanValue() && !existing.getMfaEnabled().booleanValue()) {
                                return userAuthenticationByTotpRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.TOTP)
                                    .flatMap(deviceExists -> {
                                        if (!deviceExists.booleanValue()) {
                                            return Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, Map.of("userId", userId)));
                                        }
                                        // Update the entity
                                        mapper.map(patchDTO, existing);
                            
                                        // Proceed to the update
                                        return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                            return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
                                        });
                                    });
                            } else {
                                // Update the entity
                                mapper.map(patchDTO, existing);
                    
                                // Proceed to the update
                                return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
                                });
                            }
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationByPasswordDTO> triggerPasswordReset(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordTriggerPasswordResetDTO triggerPasswordResetDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .flatMap(existing -> {
                            return userPreferencesRepository.findByTmAndUser_Id(tm, userId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserPreferencesEntity.class, "user", userId)))
                                .flatMap(preferences -> {
                                    // Assert the recovery channel is supported.
                                    RecoveryChannelType[] supportedRecoveryChannelTypes = configSourceService.getTypedProperty(tm, "authorization-server.account-recovery.channel-types", RecoveryChannelType[].class);
                                    if (!ArrayUtils.contains(supportedRecoveryChannelTypes, triggerPasswordResetDTO.getRecoveryChannelDevice().getType())) {
                                        return Mono.error(new BadRequestException());
                                    }

                                    // Generate a password reset token
                                    if (RecoveryChannelType.MAIL.equals(triggerPasswordResetDTO.getRecoveryChannelDevice().getType())) {
                                        // Find the mail.
                                        return userMailRepository.findByTmAndUser_IdAndIdAndVerifiedIsTrue(tm, userId, triggerPasswordResetDTO.getRecoveryChannelDevice().getId())
                                            .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, triggerPasswordResetDTO.getRecoveryChannelDevice().getId())))
                                            .flatMap(userMailEntity -> {
                                                // Generate token for mail purpose.
                                                String passwordResetToken = RandomUtility.generateRandomTokenForMailPurpose();

                                                // Update entity.
                                                existing.setSecuredPasswordResetToken(passwordEncoderService.encode(passwordResetToken));

                                                // Compute expiry.
                                                Integer expiryValue = configSourceService.getTypedProperty(tm, "authorization-server.mail.password-reset.expiry.value", Integer.class);
                                                ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty(tm, "authorization-server.mail.password-reset.expiry.chrono-unit", ChronoUnit.class);
                                                existing.setPasswordResetTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));
                                                
                                                // Proceed to the update
                                                //@formatter:off
                                                return asyncMessagePosterService.postNotificationMessage(
                                                    new NotificationDispatchAsyncMessageBuilder()
                                                        .kind(NotificationKind.TRIGGER_PASSWORD_RESET_WITH_MAIL)
                                                        .parameter("userMail", userMailEntity.getMail())
                                                        .parameter("resetToken", passwordResetToken)
                                                        .parameter("expiryDateTime", existing.getPasswordResetTokenExpiryDateTime())
                                                        .sms()
                                                            .to(userMailEntity.getMail(), preferences.getLanguage())
                                                        .and()
                                                    .build()
                                                ).flatMap(message -> {
                                                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                                        return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
                                                    });
                                                });
                                                //@formatter:on
                                            });
                                    } else if (RecoveryChannelType.MOBILE_PHONE.equals(triggerPasswordResetDTO.getRecoveryChannelDevice().getType())) {
                                        // Find the mail.
                                        return userMobilePhoneRepository.findByTmAndUser_IdAndIdAndVerifiedIsTrue(tm, userId, triggerPasswordResetDTO.getRecoveryChannelDevice().getId())
                                            .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, triggerPasswordResetDTO.getRecoveryChannelDevice().getId())))
                                            .flatMap(userMobilePhoneEntity -> {
                                                // Generate token for mobilePhone purpose.
                                                String passwordResetToken = RandomUtility.generateRandomTokenForMobilePhonePurpose();

                                                // Update entity.
                                                existing.setSecuredPasswordResetToken(passwordEncoderService.encode(passwordResetToken));

                                                // Manage expiry
                                                Integer expiryValue = configSourceService.getTypedProperty(tm, "authorization-server.mobile-phone.password-reset.expiry.value", Integer.class);
                                                ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty(tm, "authorization-server.mobile-phone.password-reset.expiry.chrono-unit", ChronoUnit.class);
                                                existing.setPasswordResetTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));
                                                
                                                // Proceed to the update
                                                //@formatter:off
                                                return asyncMessagePosterService.postNotificationMessage(
                                                    new NotificationDispatchAsyncMessageBuilder()
                                                        .kind(NotificationKind.TRIGGER_PASSWORD_RESET_WITH_MOBILE_PHONE)
                                                        .parameter("userMobilePhoneE164Number", userMobilePhoneEntity.getMobilePhone().getE164Number())
                                                        .parameter("resetToken", passwordResetToken)
                                                        .parameter("expiryDateTime", existing.getPasswordResetTokenExpiryDateTime())
                                                        .sms()
                                                            .to(userMobilePhoneEntity.getMobilePhone().getE164Number(), preferences.getLanguage())
                                                        .and()
                                                    .build()
                                                ).flatMap(message -> {
                                                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                                        return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
                                                    });
                                                });
                                                //@formatter:on
                                            });
                                    } else {
                                        return Mono.error(new BadRequestException());
                                    }
                                });
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationByPasswordDTO> updatePassword(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPasswordChangeDTO changePasswordDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .flatMap(existing -> {
                            // Check if old password match
                            if (!passwordEncoderService.matches(changePasswordDTO.getOldPassword(), existing.getSecuredPassword())) {
                                return Mono.error(new InvalidPasswordException());
                            }

                            // Update password.
                            return updatePassword(existing, changePasswordDTO.getNewPassword());
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationByPasswordDTO> updatePassword(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPasswordResetDTO changePasswordDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .flatMap(existing -> {
                            // Check if token match
                            if (StringUtils.isBlank(existing.getSecuredPasswordResetToken()) || !passwordEncoderService.matches(changePasswordDTO.getResetToken(), existing.getSecuredPasswordResetToken())) {
                                return Mono.error(new InvalidPasswordResetTokenException());
                            }

                            // Check if token is not expired
                            if (DateUtility.dateTimeNow().compareTo(existing.getPasswordResetTokenExpiryDateTime()) > 0) {
                                return Mono.error(new PasswordResetTokenExpiredException());
                            }

                            // Update password.
                            return updatePassword(existing, changePasswordDTO.getNewPassword());
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId)))
                        .flatMap(existing -> {
                            // Delete entity.
                            return userAuthenticationByPasswordRepository.delete(existing).then(
                                this.onDelete(existing)
                            ).then();
                        });
                });
        });
    }

    // ------------------------------------------ Utility methods.

    /**
     * Updates the password of the user.
     * @param userAuthentication the authentication.
     * @param newPassword the new password.
     * @return the DTO.
     */
    private Mono<UserAuthenticationByPasswordDTO> updatePassword(UserAuthenticationByPasswordEntity userAuthentication, String newPassword) {
        if (StringUtils.isNotBlank(userAuthentication.getSecuredPassword())) {
            // Register last used password.
            if (userAuthentication.getLastUsedSecuredPasswords() == null) {
                userAuthentication.setLastUsedSecuredPasswords(new ArrayList<String>());
            }
            userAuthentication.getLastUsedSecuredPasswords().add(userAuthentication.getSecuredPassword());
            while (userAuthentication.getLastUsedSecuredPasswords().size() > Integers.FIVE) {
                userAuthentication.getLastUsedSecuredPasswords().remove(0);
            }
        }

        // Update password.
        userAuthentication.setFailedAttempts(0);
        userAuthentication.setPasswordResetTokenExpiryDateTime(null);
        userAuthentication.setSecuredPassword(passwordEncoderService.encode(newPassword));
        userAuthentication.setSecuredPasswordResetToken(null);
        
        // Proceed to the update
        return this.onUpdate(userAuthentication).flatMap(entityToUpdateWithFields -> {
            return userAuthenticationByPasswordRepository.save(entityToUpdateWithFields).map(mapper::map);
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private Mono<UserAuthenticationByPasswordEntity> onPersist(String tm, UserAuthenticationByPasswordEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private Mono<UserAuthenticationByPasswordEntity> onUpdate(UserAuthenticationByPasswordEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<UserAuthenticationByPasswordEntity> onDelete(UserAuthenticationByPasswordEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserAuthenticationByPasswordEntity> postResourceEvent(UserAuthenticationByPasswordEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_AUTHENTICATION)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
