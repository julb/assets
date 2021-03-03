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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.applications.authorizationserver.repositories.UserMobilePhoneRepository;
import me.julb.applications.authorizationserver.repositories.UserPreferencesRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.services.UserMobilePhoneService;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhonePatchDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneVerifyDTO;
import me.julb.applications.authorizationserver.services.exceptions.InvalidMobilePhoneVerifyTokenException;
import me.julb.applications.authorizationserver.services.exceptions.MobilePhoneVerifyTokenExpiredException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.random.RandomUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.NotificationDispatchAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

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
     * The user preferences repository.
     */
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

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
        try {
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

            // Check validity of the number.

            UserMobilePhoneEntity entityToCreate = mappingService.map(creationDTO, UserMobilePhoneEntity.class);
            entityToCreate.setUser(user);
            entityToCreate.setVerified(false);

            // Handle phone number.
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            PhoneNumber phoneNumber = phoneUtil.parse(creationDTO.getMobilePhone().getNumber(), creationDTO.getMobilePhone().getCountryCode());
            entityToCreate.getMobilePhone().setInternationalNumber(phoneUtil.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL));
            entityToCreate.getMobilePhone().setNationalNumber(phoneUtil.format(phoneNumber, PhoneNumberFormat.NATIONAL));
            entityToCreate.getMobilePhone().setE164Number(phoneUtil.format(phoneNumber, PhoneNumberFormat.E164));

            this.onPersist(entityToCreate);

            UserMobilePhoneEntity result = userMobilePhoneRepository.save(entityToCreate);
            return mappingService.map(result, UserMobilePhoneDTO.class);
        } catch (NumberParseException e) {
            throw new InternalServerErrorException(e);
        }
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

        // Check that the item exists
        UserPreferencesEntity preferences = userPreferencesRepository.findByTmAndUser_Id(tm, userId);
        if (preferences == null) {
            throw new ResourceNotFoundException(UserPreferencesEntity.class, "user", userId);
        }

        if (!existing.getVerified()) {
            // Trigger verification.
            String verifyToken = RandomUtility.generateRandomTokenForMobilePhonePurpose();

            // Enable the reset.
            existing.setUser(user);
            existing.setSecuredMobilePhoneVerifyToken(passwordEncoderService.encode(verifyToken));

            // Compute expiry.
            Integer expiryValue = configSourceService.getTypedProperty("authorization-server.mobile-phone.verify.expiry.value", Integer.class);
            ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty("authorization-server.mobile-phone.verify.expiry.chrono-unit", ChronoUnit.class);
            existing.setMobilePhoneVerifyTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));

            this.onUpdate(existing);

            //@formatter:off
            asyncMessagePosterService.postNotificationMessage(
                new NotificationDispatchAsyncMessageBuilder()
                    .kind(NotificationKind.TRIGGER_MOBILE_PHONE_VERIFY)
                    .parameter("userId", existing.getUser().getId())
                    .parameter("userMobilePhoneId", existing.getId())
                    .parameter("userMobilePhoneE164Number", existing.getMobilePhone().getE164Number())
                    .parameter("verifyToken", verifyToken)
                    .parameter("expiryDateTime", existing.getMobilePhoneVerifyTokenExpiryDateTime())
                    .sms()
                        .to(existing.getMobilePhone().getE164Number(), preferences.getLanguage())
                    .and()
                .build()
            );
            //@formatter:on
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
