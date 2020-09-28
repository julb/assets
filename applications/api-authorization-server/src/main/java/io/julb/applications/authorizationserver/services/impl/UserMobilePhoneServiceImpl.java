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

package io.julb.applications.authorizationserver.services.impl;

import io.julb.applications.authorizationserver.entities.UserEntity;
import io.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import io.julb.applications.authorizationserver.repositories.UserMobilePhoneRepository;
import io.julb.applications.authorizationserver.repositories.UserRepository;
import io.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import io.julb.applications.authorizationserver.services.UserMobilePhoneService;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneCreationDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhonePatchDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneVerifyDTO;
import io.julb.applications.authorizationserver.services.exceptions.InvalidMobilePhoneVerifyTokenException;
import io.julb.applications.authorizationserver.services.exceptions.MobilePhoneVerifyTokenExpiredException;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.utility.constants.Chars;
import io.julb.library.utility.constants.Integers;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;
import io.julb.springbootstarter.security.services.PasswordEncoderService;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The user mobile phone service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserMobilePhoneServiceImpl implements UserMobilePhoneService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMobilePhoneServiceImpl.class);

    /**
     * The item repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserMobilePhoneRepository userMobilePhoneRepository;

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
    public Page<UserMobilePhoneDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        ISpecification<UserMobilePhoneEntity> spec = new SearchSpecification<UserMobilePhoneEntity>(searchable).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
        Page<UserMobilePhoneEntity> result = userMobilePhoneRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, UserMobilePhoneDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMobilePhoneDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMobilePhoneEntity result = userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (result == null) {
            throw new ResourceNotFoundException(UserMobilePhoneEntity.class, id);
        }

        return mappingService.map(result, UserMobilePhoneDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserMobilePhoneCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userMobilePhoneRepository.existsByTmAndUser_IdAndMobilePhone_CountryCodeIgnoreCaseAndMobilePhone_NumberIgnoreCase(tm, userId, creationDTO.getMobilePhone().getCountryCode(), creationDTO.getMobilePhone().getNumber())) {
            throw new ResourceAlreadyExistsException(UserMobilePhoneEntity.class,
                Map.<String, String> of("userId", userId, "mobilePhone.countryCode", creationDTO.getMobilePhone().getCountryCode(), "mobilePhone.number", creationDTO.getMobilePhone().getNumber()));
        }

        UserMobilePhoneEntity entityToCreate = mappingService.map(creationDTO, UserMobilePhoneEntity.class);
        entityToCreate.setUser(user);
        entityToCreate.setVerified(false);
        this.onPersist(entityToCreate);

        UserMobilePhoneEntity result = userMobilePhoneRepository.save(entityToCreate);
        return mappingService.map(result, UserMobilePhoneDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMobilePhoneEntity existing = userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMobilePhoneEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserMobilePhoneEntity result = userMobilePhoneRepository.save(existing);
        return mappingService.map(result, UserMobilePhoneDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMobilePhonePatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMobilePhoneEntity existing = userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMobilePhoneEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserMobilePhoneEntity result = userMobilePhoneRepository.save(existing);
        return mappingService.map(result, UserMobilePhoneDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMobilePhoneDTO triggerMobilePhoneVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMobilePhoneEntity existing = userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMobilePhoneEntity.class, id);
        }

        if (!existing.getVerified()) {
            // Trigger verification.
            String verifyToken = generateRandomVerifyToken();

            // Enable the reset.
            existing.setUser(user);
            existing.setSecuredMobilePhoneVerifyToken(passwordEncoderService.encode(verifyToken));
            existing.setMobilePhoneVerifyTokenExpiryDateTime(DateUtility.dateTimePlus(Integers.TWO, ChronoUnit.HOURS));
            this.onUpdate(existing);

            // FIXME Send notification => PLACE HOLDER TO SEND TOKEN
            LOGGER.info("*** SIMULATE SEND NOTIF: {}, {}", existing.getId(), verifyToken);
        }

        UserMobilePhoneEntity result = userMobilePhoneRepository.save(existing);
        return mappingService.map(result, UserMobilePhoneDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMobilePhoneDTO updateVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneVerifyDTO verifyDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMobilePhoneEntity existing = userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMobilePhoneEntity.class, id);
        }

        // If mobile phone is verified, return the result.
        if (!existing.getVerified()) {
            // Check token.
            if (StringUtils.isBlank(existing.getSecuredMobilePhoneVerifyToken()) || !passwordEncoderService.matches(verifyDTO.getVerifyToken(), existing.getSecuredMobilePhoneVerifyToken())) {
                throw new InvalidMobilePhoneVerifyTokenException();
            }

            // Check if token is not expired
            if (DateUtility.dateTimeNow().compareTo(existing.getMobilePhoneVerifyTokenExpiryDateTime()) > 0) {
                throw new MobilePhoneVerifyTokenExpiredException();
            }

            // Update verified status.
            existing.setMobilePhoneVerifyTokenExpiryDateTime(null);
            existing.setVerified(true);
            existing.setSecuredMobilePhoneVerifyToken(null);

            // If phone is primary, unlock user account.
            if (existing.getPrimary() && !existing.getUser().getAccountNonLocked()) {
                existing.getUser().setAccountNonLocked(true);
                userRepository.save(existing.getUser());
            }
        }

        UserMobilePhoneEntity result = userMobilePhoneRepository.save(existing);
        return mappingService.map(result, UserMobilePhoneDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMobilePhoneEntity existing = userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMobilePhoneEntity.class, id);
        }

        // Delete entity.
        userMobilePhoneRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Generates a random verify token.
     * @return a verify token.
     */
    protected String generateRandomVerifyToken() {
        // Generate an URI.
        //@formatter:off
            return new RandomStringGenerator.Builder()
                .withinRange(new char[] {Chars.ZERO, Chars.NINE}, new char[] {Chars.A_LOWERCASE, Chars.Z_LOWERCASE}, new char[] {Chars.A_UPPERCASE, Chars.Z_UPPERCASE})
                .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LETTERS)
                .build()
                .generate(Integers.ONE_HUNDRED_TWENTY_EIGHT);
            //@formatter:on
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserMobilePhoneEntity entity) {
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
    private void onUpdate(UserMobilePhoneEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserMobilePhoneEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserMobilePhoneEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_MOBILE_PHONE)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
