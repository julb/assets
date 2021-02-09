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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByPasswordRepository;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByTotpRepository;
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
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.applications.authorizationserver.services.exceptions.InvalidPasswordException;
import me.julb.applications.authorizationserver.services.exceptions.InvalidPasswordResetTokenException;
import me.julb.applications.authorizationserver.services.exceptions.PasswordResetTokenExpiredException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The user authentication by password service implementation.
 * <P>
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
    private IMappingService mappingService;

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

        return mappingService.map(result, UserAuthenticationByPasswordDTO.class);
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
        credentials.setUserAuthentication(mappingService.map(result, UserAuthenticationByPasswordDTO.class));
        credentials.setUser(mappingService.map(result.getUser(), UserDTO.class));

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

        UserAuthenticationByPasswordEntity entityToCreate = mappingService.map(creationDTO, UserAuthenticationByPasswordEntity.class);
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
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByPasswordDTO.class);
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
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByPasswordDTO.class);
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

        // Generate a password reset token
        String passwordResetToken = generateRandomResetToken();

        // Enable the reset.
        existing.setSecuredPasswordResetToken(passwordEncoderService.encode(passwordResetToken));
        existing.setPasswordResetTokenExpiryDateTime(DateUtility.dateTimePlus(Integers.TWO, ChronoUnit.HOURS));
        this.onUpdate(existing);

        // FIXME Send notification => PLACE HOLDER TO SEND TOKEN

        UserAuthenticationByPasswordEntity result = userAuthenticationByPasswordRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByPasswordDTO.class);
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
     * Generates a random reset token.
     * @return a reset token.
     */
    protected String generateRandomResetToken() {
        // Generate an URI.
        //@formatter:off
            return new RandomStringGenerator.Builder()
                .withinRange(new char[] {Chars.ZERO, Chars.NINE}, new char[] {Chars.A_LOWERCASE, Chars.Z_LOWERCASE}, new char[] {Chars.A_UPPERCASE, Chars.Z_UPPERCASE})
                .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LETTERS)
                .build()
                .generate(Integers.ONE_HUNDRED_TWENTY_EIGHT);
            //@formatter:on
    }

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
        return mappingService.map(result, UserAuthenticationByPasswordDTO.class);
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
