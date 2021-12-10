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
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.core.context.configs.ContextConfigSourceService;
import me.julb.springbootstarter.messaging.builders.NotificationDispatchAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

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
    private ContextConfigSourceService configSourceService;

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
    public UserAuthenticationByPasswordDTO findOne(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationCredentialsDTO findOneCredentials(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

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
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByPasswordDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userAuthenticationByPasswordRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD)) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByPasswordEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.PASSWORD.toString()));
        }

        UserAuthenticationByPasswordEntity entityToCreate = mapper.map(creationDTO);
        entityToCreate.setUser(user);
        entityToCreate.setMfaEnabled(Boolean.FALSE);
        this.onPersist(entityToCreate);

        // Update password.
        return updatePassword(entityToCreate, creationDTO.getPassword());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByPasswordDTO update(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity existing = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        // Ensure device are registered for user for MFA
        if (updateDTO.getMfaEnabled() && !existing.getMfaEnabled()) {
            if (!userAuthenticationByTotpRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.TOTP)) {
                throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, Map.of("userId", userId));
            }
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByPasswordDTO patch(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity existing = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        // Ensure device are registered for user for MFA
        if (patchDTO.getMfaEnabled() != null && patchDTO.getMfaEnabled() && !existing.getMfaEnabled()) {
            if (!userAuthenticationByTotpRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.TOTP)) {
                throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, Map.of("userId", userId));
            }
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByPasswordDTO triggerPasswordReset(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordTriggerPasswordResetDTO triggerPasswordResetDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity existing = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        // Check that the item exists
        UserPreferencesEntity preferences = userPreferencesRepository.findByTmAndUser_Id(tm, userId);
        if (preferences == null) {
            throw new ResourceNotFoundException(UserPreferencesEntity.class, "user", userId);
        }

        // Assert the recovery channel is supported.
        RecoveryChannelType[] supportedRecoveryChannelTypes = configSourceService.getTypedProperty("authorization-server.account-recovery.channel-types", RecoveryChannelType[].class);
        if (!ArrayUtils.contains(supportedRecoveryChannelTypes, triggerPasswordResetDTO.getRecoveryChannelDevice().getType())) {
            throw new BadRequestException();
        }

        // Generate a password reset token
        if (RecoveryChannelType.MAIL.equals(triggerPasswordResetDTO.getRecoveryChannelDevice().getType())) {
            // Find the mail.
            UserMailEntity userMailEntity = userMailRepository.findByTmAndUser_IdAndIdAndVerifiedIsTrue(tm, userId, triggerPasswordResetDTO.getRecoveryChannelDevice().getId());
            if (userMailEntity == null) {
                throw new ResourceNotFoundException(UserMailEntity.class, triggerPasswordResetDTO.getRecoveryChannelDevice().getId());
            }

            // Generate token for mail purpose.
            String passwordResetToken = RandomUtility.generateRandomTokenForMailPurpose();

            // Update entity.
            existing.setSecuredPasswordResetToken(passwordEncoderService.encode(passwordResetToken));

            // Compute expiry.
            Integer expiryValue = configSourceService.getTypedProperty("authorization-server.mail.password-reset.expiry.value", Integer.class);
            ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty("authorization-server.mail.password-reset.expiry.chrono-unit", ChronoUnit.class);
            existing.setPasswordResetTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));

            this.onUpdate(existing);

            //@formatter:off
            asyncMessagePosterService.postNotificationMessage(
                new NotificationDispatchAsyncMessageBuilder()
                    .kind(NotificationKind.TRIGGER_PASSWORD_RESET_WITH_MAIL)
                    .parameter("userMail", userMailEntity.getMail())
                    .parameter("resetToken", passwordResetToken)
                    .parameter("expiryDateTime", existing.getPasswordResetTokenExpiryDateTime())
                    .sms()
                        .to(userMailEntity.getMail(), preferences.getLanguage())
                    .and()
                .build()
            );
            //@formatter:on
        } else if (RecoveryChannelType.MOBILE_PHONE.equals(triggerPasswordResetDTO.getRecoveryChannelDevice().getType())) {
            // Find the mail.
            UserMobilePhoneEntity userMobilePhoneEntity = userMobilePhoneRepository.findByTmAndUser_IdAndIdAndVerifiedIsTrue(tm, userId, triggerPasswordResetDTO.getRecoveryChannelDevice().getId());
            if (userMobilePhoneEntity == null) {
                throw new ResourceNotFoundException(UserMobilePhoneEntity.class, triggerPasswordResetDTO.getRecoveryChannelDevice().getId());
            }

            // Generate token for mobilePhone purpose.
            String passwordResetToken = RandomUtility.generateRandomTokenForMobilePhonePurpose();

            // Update entity.
            existing.setSecuredPasswordResetToken(passwordEncoderService.encode(passwordResetToken));

            // Manage expiry
            Integer expiryValue = configSourceService.getTypedProperty("authorization-server.mobile-phone.password-reset.expiry.value", Integer.class);
            ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty("authorization-server.mobile-phone.password-reset.expiry.chrono-unit", ChronoUnit.class);
            existing.setPasswordResetTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));

            this.onUpdate(existing);

            //@formatter:off
            asyncMessagePosterService.postNotificationMessage(
                new NotificationDispatchAsyncMessageBuilder()
                    .kind(NotificationKind.TRIGGER_PASSWORD_RESET_WITH_MOBILE_PHONE)
                    .parameter("userMobilePhoneE164Number", userMobilePhoneEntity.getMobilePhone().getE164Number())
                    .parameter("resetToken", passwordResetToken)
                    .parameter("expiryDateTime", existing.getPasswordResetTokenExpiryDateTime())
                    .sms()
                        .to(userMobilePhoneEntity.getMobilePhone().getE164Number(), preferences.getLanguage())
                    .and()
                .build()
            );
            //@formatter:on
        }

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByPasswordDTO updatePassword(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPasswordChangeDTO changePasswordDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity existing = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        // Check if old password match
        if (!passwordEncoderService.matches(changePasswordDTO.getOldPassword(), existing.getSecuredPassword())) {
            throw new InvalidPasswordException();
        }

        // Update password.
        return updatePassword(existing, changePasswordDTO.getNewPassword());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByPasswordDTO updatePassword(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPasswordResetDTO changePasswordDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity existing = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        // Check if token match
        if (StringUtils.isBlank(existing.getSecuredPasswordResetToken()) || !passwordEncoderService.matches(changePasswordDTO.getResetToken(), existing.getSecuredPasswordResetToken())) {
            throw new InvalidPasswordResetTokenException();
        }

        // Check if token is not expired
        if (DateUtility.dateTimeNow().compareTo(existing.getPasswordResetTokenExpiryDateTime()) > 0) {
            throw new PasswordResetTokenExpiredException();
        }

        return updatePassword(existing, changePasswordDTO.getNewPassword());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPasswordEntity existing = userAuthenticationByPasswordRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PASSWORD);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPasswordEntity.class, userId);
        }

        // Delete entity.
        userAuthenticationByPasswordRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Updates the password of the user.
     * @param userAuthentication the authentication.
     * @param newPassword the new password.
     * @return the DTO.
     */
    private UserAuthenticationByPasswordDTO updatePassword(UserAuthenticationByPasswordEntity userAuthentication, String newPassword) {
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

        this.onUpdate(userAuthentication);

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(userAuthentication);
        return mapper.map(result);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserAuthenticationByPasswordEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private void onUpdate(UserAuthenticationByPasswordEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserAuthenticationByPasswordEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserAuthenticationByPasswordEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_AUTHENTICATION)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
