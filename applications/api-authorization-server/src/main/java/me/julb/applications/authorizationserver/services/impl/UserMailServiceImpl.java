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

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.mail.mappers.UserMailEntityMapper;
import me.julb.applications.authorizationserver.entities.mappers.UserEntityMapper;
import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserPreferencesRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailPatchDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailVerifyDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.applications.authorizationserver.services.exceptions.InvalidMailVerifyTokenException;
import me.julb.applications.authorizationserver.services.exceptions.MailVerifyTokenExpiredException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
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
 * The user mail service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserMailServiceImpl implements UserMailService {

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
     * The user preferences repository.
     */
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    /**
     * The mapper.
     */
    @Autowired
    private UserMailEntityMapper mapper;

    /**
     * The mapper.
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
    public Flux<UserMailDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMapMany(user -> {
                    ISpecification<UserMailEntity> spec = new SearchSpecification<UserMailEntity>(searchable).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
                    return userMailRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserMailDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> existsByMail(@NotNull @NotBlank @Email String mail) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userMailRepository.existsByTmAndMailIgnoreCase(tm, mail);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserMailDTO> findByMail(@NotNull @NotBlank @Email String mail) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userMailRepository.findByTmAndMailIgnoreCase(tm, mail)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, "mail", mail)))
                .map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserMailDTO> findByMailVerified(@NotNull @NotBlank @Email String mail) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, "mail", mail)))
                .map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDTO> findUserByMailVerified(@NotNull @NotBlank @Email String mail) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, "mail", mail)))
                .map(UserMailEntity::getUser)
                .map(userMapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserMailDTO> create(@NotNull @Identifier String userId, @NotNull @Valid UserMailCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMailRepository.existsByTmAndMailIgnoreCase(tm, creationDTO.getMail())
                        .flatMap(alreadyExists -> {
                            if (alreadyExists.booleanValue()) {
                                return Mono.error(new ResourceAlreadyExistsException(UserMailEntity.class, "mail", creationDTO.getMail()));
                            }

                            UserMailEntity entityToCreate = mapper.map(creationDTO);
                            entityToCreate.setUser(user);
                            entityToCreate.setVerified(false);
                            return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                return userMailRepository.save(entityToCreateWithFields).map(mapper::map);
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
    public Mono<UserMailDTO> update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(updateDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userMailRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<UserMailDTO> patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(patchDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userMailRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<UserMailDTO> triggerMailVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {

                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .flatMap(existing -> {
                            
                            return userPreferencesRepository.findByTmAndUser_Id(tm, userId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserPreferencesEntity.class, "user", userId)))
                                .flatMap(preferences -> {
                                    // If email is verified, return the result.
                                    if (!existing.getVerified().booleanValue()) {
                                        // Trigger verification.
                                        String verifyToken = RandomUtility.generateRandomTokenForMailPurpose();

                                        // Enable the reset.
                                        existing.setUser(user);
                                        existing.setSecuredMailVerifyToken(passwordEncoderService.encode(verifyToken));

                                        // Compute expiry.
                                        Integer expiryValue = configSourceService.getTypedProperty(tm, "authorization-server.mail.verify.expiry.value", Integer.class);
                                        ChronoUnit expiryChronoUnit = configSourceService.getTypedProperty(tm, "authorization-server.mail.verify.expiry.chrono-unit", ChronoUnit.class);
                                        existing.setMailVerifyTokenExpiryDateTime(DateUtility.dateTimePlus(expiryValue, expiryChronoUnit));

                                        //@formatter:off
                                        return asyncMessagePosterService.postNotificationMessage(
                                            new NotificationDispatchAsyncMessageBuilder()
                                                .kind(NotificationKind.TRIGGER_MAIL_VERIFY)
                                                .parameter("userId", existing.getUser().getId())
                                                .parameter("userMailId", existing.getId())
                                                .parameter("userMail", existing.getMail())
                                                .parameter("verifyToken", verifyToken)
                                                .parameter("expiryDateTime", existing.getMailVerifyTokenExpiryDateTime())
                                                .mail()
                                                    .to(existing.getMail(), preferences.getLanguage())
                                                .and()
                                            .build()
                                        )
                                        .then(this.onUpdate(existing))
                                        .flatMap(entityToUpdateWithFields -> {
                                            return userMailRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserMailDTO> updateVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailVerifyDTO verifyDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .flatMap(existing -> {
                            // If email is verified, return the result.
                            if (!existing.getVerified().booleanValue()) {
                                // Check token.
                                if (StringUtils.isBlank(existing.getSecuredMailVerifyToken()) || !passwordEncoderService.matches(verifyDTO.getVerifyToken(), existing.getSecuredMailVerifyToken())) {
                                    return Mono.error(new InvalidMailVerifyTokenException());
                                }
                    
                                // Check if token is not expired
                                if (DateUtility.dateTimeNow().compareTo(existing.getMailVerifyTokenExpiryDateTime()) > 0) {
                                    return Mono.error(new MailVerifyTokenExpiredException());
                                }

                                // Update verified status.
                                existing.setMailVerifyTokenExpiryDateTime(null);
                                existing.setVerified(true);
                                existing.setSecuredMailVerifyToken(null);

                                // If mail is primary, unlock user account.
                                if (existing.getPrimary().booleanValue() && !existing.getUser().getAccountNonLocked().booleanValue()) {
                                    existing.getUser().setAccountNonLocked(true);
                                    return userRepository.save(existing.getUser()).flatMap(updatedUser -> {
                                        return userMailRepository.save(existing).map(mapper::map);
                                    });
                                } else {
                                    return userMailRepository.save(existing).map(mapper::map);
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
    public Mono<UserMailDTO> updateVerifyWithoutToken(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .flatMap(existing -> {
                            // If email is verified, return the result.
                            if (!existing.getVerified().booleanValue()) {
                                // Update verified status.
                                existing.setMailVerifyTokenExpiryDateTime(null);
                                existing.setVerified(true);
                                existing.setSecuredMailVerifyToken(null);

                                // If mail is primary, unlock user account.
                                if (existing.getPrimary().booleanValue() && !existing.getUser().getAccountNonLocked().booleanValue()) {
                                    existing.getUser().setAccountNonLocked(true);
                                    return userRepository.save(existing.getUser()).flatMap(updatedUser -> {
                                        return userMailRepository.save(existing).map(mapper::map);
                                    });
                                } else {
                                    return userMailRepository.save(existing).map(mapper::map);
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
                    return userMailRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserMailEntity.class, id)))
                        .flatMap(existing -> {
                            // Delete entity.
                            return userMailRepository.delete(existing).then(
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
    private Mono<UserMailEntity> onPersist(String tm, UserMailEntity entity) {
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
    private Mono<UserMailEntity> onUpdate(UserMailEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<UserMailEntity> onDelete(UserMailEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserMailEntity> postResourceEvent(UserMailEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_MAIL)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
