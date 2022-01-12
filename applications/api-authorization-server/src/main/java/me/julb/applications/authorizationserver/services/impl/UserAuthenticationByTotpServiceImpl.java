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

import java.util.ArrayList;
import java.util.List;
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
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.entities.authentication.mappers.UserAuthenticationEntityMapper;
import me.julb.applications.authorizationserver.entities.mappers.UserEntityMapper;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByPasswordRepository;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByPincodeRepository;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByTotpRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.repositories.specifications.UserAuthenticationOfGivenTypeSpecification;
import me.julb.applications.authorizationserver.services.UserAuthenticationByTotpService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpWithRawSecretDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.exceptions.ResourceStillReferencedException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.otp.TotpUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
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
 * The user authentication service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAuthenticationByTotpServiceImpl implements UserAuthenticationByTotpService {

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
     * The user authentication repository.
     */
    @Autowired
    private UserAuthenticationByPincodeRepository userAuthenticationByPincodeRepository;

    /**
     * The user authentication by api key repository.
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
     * The password encoder.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<UserAuthenticationByTotpDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMapMany(user -> {
                    ISpecification<UserAuthenticationByTotpEntity> spec =
                        new SearchSpecification<UserAuthenticationByTotpEntity>(searchable).and(new UserAuthenticationOfGivenTypeSpecification<>(UserAuthenticationType.TOTP)).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
                    return userAuthenticationByTotpRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationByTotpDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id)))
                        .map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationCredentialsDTO> findOneCredentials(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id)))
                        .map(result -> {
                            // Generate valid OTP
                            Iterable<String> validTotps = TotpUtility.generateValidTotps(result.getSecret(), Integers.ONE);
                            List<String> encodedValidTotps = new ArrayList<>();
                            for (String validTotp : validTotps) {
                                encodedValidTotps.add(passwordEncoderService.encode(validTotp));
                            }

                            // Get credentials
                            UserAuthenticationCredentialsDTO credentials = new UserAuthenticationCredentialsDTO();
                            credentials.setCredentials(encodedValidTotps.toArray(new String[0]));
                            credentials.setCredentialsNonExpired(true);
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
    public Mono<UserAuthenticationByTotpWithRawSecretDTO> create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByTotpCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByTotpRepository.existsByTmAndUser_IdAndTypeAndNameIgnoreCase(tm, userId, UserAuthenticationType.TOTP, creationDTO.getName())
                        .flatMap(alreadyExists -> {
                            if (alreadyExists.booleanValue()) {
                                return Mono.error(new ResourceAlreadyExistsException(UserAuthenticationByTotpEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.TOTP.toString(), "name", creationDTO.getName())));
                            }

                            String secret = TotpUtility.generateRandomTotpSecret();

                            UserAuthenticationByTotpEntity entityToCreate = mapper.map(creationDTO);
                            entityToCreate.setUser(user);
                            entityToCreate.setSecret(secret);

                            return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                return userAuthenticationByTotpRepository.save(entityToCreateWithFields).map(result -> {
                                    UserAuthenticationByTotpWithRawSecretDTO userAuthenticationByTotpWithRawSecret = mapper.mapWithRawSecret(result);
                                    userAuthenticationByTotpWithRawSecret.setRawSecret(secret);
                                    userAuthenticationByTotpWithRawSecret.setQrCodeUri(TotpUtility.generateQrcodeUri(user.getMail(), tm, secret));
                                    return userAuthenticationByTotpWithRawSecret;
                                });
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
    public Mono<UserAuthenticationByTotpDTO> update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id)))
                        .flatMap(existing -> {
                            return userAuthenticationByTotpRepository.existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(tm, userId, UserAuthenticationType.TOTP, id, updateDTO.getName())
                                .flatMap(alreadyExists -> {
                                    if (alreadyExists.booleanValue()) {
                                        return Mono.error(new ResourceAlreadyExistsException(UserAuthenticationByTotpEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.TOTP.toString(), "name", updateDTO.getName())));
                                    }

                                    mapper.map(updateDTO, existing);
                
                                    // Proceed to the update
                                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                        return userAuthenticationByTotpRepository.save(entityToUpdateWithFields).map(mapper::map);
                                    });
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
    public Mono<UserAuthenticationByTotpDTO> patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id)))
                        .flatMap(existing -> {
                            if (StringUtils.isNotBlank(patchDTO.getName())) {
                                return userAuthenticationByTotpRepository.existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(tm, userId, UserAuthenticationType.TOTP, id, patchDTO.getName())
                                    .flatMap(alreadyExists -> {
                                        if (alreadyExists.booleanValue()) {
                                            return Mono.error(new ResourceAlreadyExistsException(UserAuthenticationByTotpEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.TOTP.toString(), "name", patchDTO.getName())));
                                        }

                                        mapper.map(patchDTO, existing);
                    
                                        // Proceed to the update
                                        return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                            return userAuthenticationByTotpRepository.save(entityToUpdateWithFields).map(mapper::map);
                                        });
                                    });
                            } else {
                                mapper.map(patchDTO, existing);
                    
                                // Proceed to the update
                                return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return userAuthenticationByTotpRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<Void> delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id)))
                        .flatMap(existing -> {
                            return userAuthenticationByTotpRepository.countByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.TOTP).flatMap(count -> {
                                if (count == Integers.ONE) {
                                    return Mono.zip(
                                        userAuthenticationByPasswordRepository.existsByTmAndUser_IdAndTypeAndMfaEnabledIsTrue(tm, userId, UserAuthenticationType.PASSWORD),
                                        userAuthenticationByPincodeRepository.existsByTmAndUser_IdAndTypeAndMfaEnabledIsTrue(tm, userId, UserAuthenticationType.PINCODE)
                                    ).flatMap(tuple -> {
                                        if (tuple.getT1().booleanValue()) {
                                            return Mono.error(new ResourceStillReferencedException(UserAuthenticationByTotpEntity.class, id, UserAuthenticationByPasswordEntity.class));
                                        }

                                        if (tuple.getT2().booleanValue()) {
                                            return Mono.error(new ResourceStillReferencedException(UserAuthenticationByTotpEntity.class, id, UserAuthenticationByPincodeEntity.class));
                                        }

                                        // Delete entity.
                                        return userAuthenticationByTotpRepository.delete(existing).then(
                                            this.onDelete(existing)
                                        ).then();
                                    });
                                } else {
                                    // Delete entity.
                                    return userAuthenticationByTotpRepository.delete(existing).then(
                                        this.onDelete(existing)
                                    ).then();
                                }
                            });
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
    private Mono<UserAuthenticationByTotpEntity> onPersist(String tm, UserAuthenticationByTotpEntity entity) {
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
    private Mono<UserAuthenticationByTotpEntity> onUpdate(UserAuthenticationByTotpEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<UserAuthenticationByTotpEntity> onDelete(UserAuthenticationByTotpEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserAuthenticationByTotpEntity> postResourceEvent(UserAuthenticationByTotpEntity entity, ResourceEventType resourceEventType) {
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
