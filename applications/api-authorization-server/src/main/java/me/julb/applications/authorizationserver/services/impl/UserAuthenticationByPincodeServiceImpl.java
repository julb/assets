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
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByPincodeRepository;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByTotpRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeTriggerPincodeResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.applications.authorizationserver.services.exceptions.InvalidPincodeException;
import me.julb.applications.authorizationserver.services.exceptions.InvalidPincodeResetTokenException;
import me.julb.applications.authorizationserver.services.exceptions.PincodeResetTokenExpiredException;
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
import me.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The user authentication by pincode service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAuthenticationByPincodeServiceImpl implements UserAuthenticationByPincodeService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user authentication repository.
     */
    @Autowired
    private UserAuthenticationByPincodeRepository userAuthenticationByPincodeRepository;

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
    private IAsyncMessagePosterService asyncMessagePosterService;

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
    public UserAuthenticationByPincodeDTO findOne(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPincodeEntity result = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
        }

        return mappingService.map(result, UserAuthenticationByPincodeDTO.class);
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
        UserAuthenticationByPincodeEntity result = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
        }

        // Get credentials
        UserAuthenticationCredentialsDTO credentials = new UserAuthenticationCredentialsDTO();
        credentials.setUniqueCredentials(result.getSecuredPincode());
        if (StringUtils.isNotBlank(result.getPincodeExpiryDateTime())) {
            credentials.setCredentialsNonExpired(DateUtility.dateTimeNow().compareTo(result.getPincodeExpiryDateTime()) <= 0);
        } else {
            credentials.setCredentialsNonExpired(true);
        }
        credentials.setUserAuthentication(mappingService.map(result, UserAuthenticationByPincodeDTO.class));
        credentials.setUser(mappingService.map(result.getUser(), UserDTO.class));

        return credentials;
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByPincodeDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodeCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userAuthenticationByPincodeRepository.existsByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE)) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByPincodeEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.PINCODE.toString()));
        }

        UserAuthenticationByPincodeEntity entityToCreate = mappingService.map(creationDTO, UserAuthenticationByPincodeEntity.class);
        entityToCreate.setUser(user);
        entityToCreate.setMfaEnabled(Boolean.FALSE);
        this.onPersist(entityToCreate);

        // Update pincode.
        return updatePincode(entityToCreate, creationDTO.getPincode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByPincodeDTO update(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodeUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPincodeEntity existing = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
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

        UserAuthenticationByPincodeEntity result = userAuthenticationByPincodeRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByPincodeDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByPincodeDTO patch(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodePatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPincodeEntity existing = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
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

        UserAuthenticationByPincodeEntity result = userAuthenticationByPincodeRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByPincodeDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByPincodeDTO triggerPincodeReset(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodeTriggerPincodeResetDTO triggerPincodeResetDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPincodeEntity existing = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
        }

        // Generate a pincode reset token
        String pincodeResetToken = generateRandomResetToken();

        // Enable the reset.
        existing.setSecuredPincodeResetToken(passwordEncoderService.encode(pincodeResetToken));
        existing.setPincodeResetTokenExpiryDateTime(DateUtility.dateTimePlus(Integers.TWO, ChronoUnit.HOURS));
        this.onUpdate(existing);

        // FIXME Send notification => PLACE HOLDER TO SEND TOKEN

        UserAuthenticationByPincodeEntity result = userAuthenticationByPincodeRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByPincodeDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByPincodeDTO updatePincode(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodePincodeChangeDTO changePincodeDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPincodeEntity existing = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
        }

        // Check if old pincode match
        if (!passwordEncoderService.matches(changePincodeDTO.getOldPincode(), existing.getSecuredPincode())) {
            throw new InvalidPincodeException();
        }

        // Update pincode.
        return updatePincode(existing, changePincodeDTO.getNewPincode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByPincodeDTO updatePincode(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodePincodeResetDTO changePincodeDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByPincodeEntity existing = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
        }

        // Check if token match
        if (StringUtils.isBlank(existing.getSecuredPincodeResetToken()) || !passwordEncoderService.matches(changePincodeDTO.getResetToken(), existing.getSecuredPincodeResetToken())) {
            throw new InvalidPincodeResetTokenException();
        }

        // Check if token is not expired
        if (DateUtility.dateTimeNow().compareTo(existing.getPincodeResetTokenExpiryDateTime()) > 0) {
            throw new PincodeResetTokenExpiredException();
        }

        return updatePincode(existing, changePincodeDTO.getNewPincode());
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
        UserAuthenticationByPincodeEntity existing = userAuthenticationByPincodeRepository.findByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.PINCODE);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByPincodeEntity.class, userId);
        }

        // Delete entity.
        userAuthenticationByPincodeRepository.delete(existing);

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
     * Updates the pincode of the user.
     * @param userAuthentication the authentication.
     * @param newPincode the new pincode.
     * @return the DTO.
     */
    private UserAuthenticationByPincodeDTO updatePincode(UserAuthenticationByPincodeEntity userAuthentication, String newPincode) {
        if (StringUtils.isNotBlank(userAuthentication.getSecuredPincode())) {
            // Register last used pincode.
            if (userAuthentication.getLastUsedSecuredPincodes() == null) {
                userAuthentication.setLastUsedSecuredPincodes(new ArrayList<String>());
            }
            userAuthentication.getLastUsedSecuredPincodes().add(userAuthentication.getSecuredPincode());
            while (userAuthentication.getLastUsedSecuredPincodes().size() > Integers.FIVE) {
                userAuthentication.getLastUsedSecuredPincodes().remove(0);
            }
        }

        // Update pincode.
        userAuthentication.setFailedAttempts(0);
        userAuthentication.setPincodeResetTokenExpiryDateTime(null);
        userAuthentication.setSecuredPincode(passwordEncoderService.encode(newPincode));
        userAuthentication.setSecuredPincodeResetToken(null);

        this.onUpdate(userAuthentication);

        UserAuthenticationByPincodeEntity result = userAuthenticationByPincodeRepository.save(userAuthentication);
        return mappingService.map(result, UserAuthenticationByPincodeDTO.class);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserAuthenticationByPincodeEntity entity) {
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
    private void onUpdate(UserAuthenticationByPincodeEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserAuthenticationByPincodeEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserAuthenticationByPincodeEntity entity, ResourceEventType resourceEventType) {
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
