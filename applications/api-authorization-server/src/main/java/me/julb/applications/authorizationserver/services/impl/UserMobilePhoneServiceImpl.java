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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import me.julb.applications.authorizationserver.entities.mobilephone.mappers.UserMobilePhoneEntityMapper;
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
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.messaging.reactive.builders.NotificationDispatchAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The user mobile phone service implementation.
 * <br>
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
    private UserMobilePhoneEntityMapper mapper;

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
    public Flux<UserMobilePhoneDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMapMany(user -> {
                    ISpecification<UserMobilePhoneEntity> spec = new SearchSpecification<UserMobilePhoneEntity>(searchable).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
                    return userMobilePhoneRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserMobilePhoneDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, id)))
                        .map(mapper::map);
                });
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserMobilePhoneDTO> create(@NotNull @Identifier String userId, @NotNull @Valid UserMobilePhoneCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMobilePhoneRepository.existsByTmAndUser_IdAndMobilePhone_CountryCodeIgnoreCaseAndMobilePhone_NumberIgnoreCase(tm, userId, creationDTO.getMobilePhone().getCountryCode(), creationDTO.getMobilePhone().getNumber())
                        .flatMap(alreadyExists -> {
                            if (alreadyExists.booleanValue()) {
                                return Mono.error(new ResourceAlreadyExistsException(UserMobilePhoneEntity.class,
                                    Map.<String, String> of("userId", userId, "mobilePhone.countryCode", creationDTO.getMobilePhone().getCountryCode(), "mobilePhone.number", creationDTO.getMobilePhone().getNumber())));
                            }

                            try {
                                // Check validity of the number.
                                UserMobilePhoneEntity entityToCreate = mapper.map(creationDTO);
                                entityToCreate.setUser(user);
                                entityToCreate.setVerified(false);

                                // Handle phone number.
                                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                                PhoneNumber phoneNumber = phoneUtil.parse(creationDTO.getMobilePhone().getNumber(), creationDTO.getMobilePhone().getCountryCode());
                                entityToCreate.getMobilePhone().setInternationalNumber(phoneUtil.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL));
                                entityToCreate.getMobilePhone().setNationalNumber(phoneUtil.format(phoneNumber, PhoneNumberFormat.NATIONAL));
                                entityToCreate.getMobilePhone().setE164Number(phoneUtil.format(phoneNumber, PhoneNumberFormat.E164));

                                return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                    return userMobilePhoneRepository.save(entityToCreateWithFields).map(mapper::map);
                                });
                            } catch (NumberParseException e) {
                                return Mono.error(new InternalServerErrorException(e));
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
    public Mono<UserMobilePhoneDTO> update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(updateDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userMobilePhoneRepository.save(entityToUpdateWithFields).map(mapper::map);
                            });
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserMobilePhoneDTO> patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMobilePhonePatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(patchDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userMobilePhoneRepository.save(entityToUpdateWithFields).map(mapper::map);
                            });
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserMobilePhoneDTO> triggerMobilePhoneVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {

                    return userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, id)))
                        .flatMap(existing -> {
                            
                            return userPreferencesRepository.findByTmAndUser_Id(tm, userId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserPreferencesEntity.class, "user", userId)))
                                .flatMap(preferences -> {
                                    // If emobilePhone is verified, return the result.
                                    if (!existing.getVerified().booleanValue()) {
                                        // Trigger verification.
                                        String verifyToken = RandomUtility.generateRandomTokenForMobilePhonePurpose();

                                        // Enable the reset.
                                        existing.setUser(user);
                                        existing.setSecuredMobilePhoneVerifyToken(passwordEncoderService.encode(verifyToken));

                                        // Compute expiry.
                                        Integer expiryValue = configSourceService.getTypedProperty(tm, "authorization-server.mobile-phone.verify.expiry.value", Integer.class);
                                        ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty(tm, "authorization-server.mobile-phone.verify.expiry.chrono-unit", ChronoUnit.class);
                                        existing.setMobilePhoneVerifyTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));

                                        //@formatter:off
                                        return asyncMessagePosterService.postNotificationMessage(
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
                                        )
                                        .then(this.onUpdate(existing))
                                        .flatMap(entityToUpdateWithFields -> {
                                            return userMobilePhoneRepository.save(entityToUpdateWithFields).map(mapper::map);
                                        });
                                        //@formatter:on
                                    } else {
                                        return Mono.just(mapper.map(existing));
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
    public Mono<UserMobilePhoneDTO> updateVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneVerifyDTO verifyDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, id)))
                        .flatMap(existing -> {
                            // If emobilePhone is verified, return the result.
                            if (!existing.getVerified().booleanValue()) {
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

                                // If mobilePhone is primary, unlock user account.
                                if (existing.getPrimary().booleanValue() && !existing.getUser().getAccountNonLocked().booleanValue()) {
                                    existing.getUser().setAccountNonLocked(true);
                                    return userRepository.save(existing.getUser()).flatMap(updatedUser -> {
                                        return userMobilePhoneRepository.save(existing).map(mapper::map);
                                    });
                                } else {
                                    return userMobilePhoneRepository.save(existing).map(mapper::map);
                                }
                            } else {
                                return Mono.just(mapper.map(existing));
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
    public Mono<Void> delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMobilePhoneRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMobilePhoneEntity.class, id)))
                        .flatMap(existing -> {
                            // Delete entity.
                            return userMobilePhoneRepository.delete(existing).then(
                                this.onDelete(existing)
                            ).then();
                        });
                });
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private Mono<UserMobilePhoneEntity> onPersist(String tm, UserMobilePhoneEntity entity) {
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
    private Mono<UserMobilePhoneEntity> onUpdate(UserMobilePhoneEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<UserMobilePhoneEntity> onDelete(UserMobilePhoneEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserMobilePhoneEntity> postResourceEvent(UserMobilePhoneEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_MOBILE_PHONE)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
