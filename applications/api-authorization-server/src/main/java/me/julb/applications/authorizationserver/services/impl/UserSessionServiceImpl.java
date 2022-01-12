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
import me.julb.applications.authorizationserver.entities.mappers.UserEntityMapper;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.entities.session.mappers.UserSessionEntityMapper;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.UserSessionRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.UserSessionToAccessTokenMapper;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFirstCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFromIdTokenCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenWithIdTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionCredentialsDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionPatchDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionWithRawIdTokenDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.exceptions.UnauthorizedException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.random.RandomUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.SecureIdToken;
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
 * The user session service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserSessionServiceImpl implements UserSessionService {

    /**
     * The item repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserSessionRepository userSessionRepository;

    /**
     * The mapper.
     */
    @Autowired
    private UserSessionEntityMapper mapper;

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

    /**
     * The user session to access token mapper.
     */
    @Autowired
    private UserSessionToAccessTokenMapper userSessionToAccessTokenMapper;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<UserSessionDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMapMany(user -> {
                    ISpecification<UserSessionEntity> spec = new SearchSpecification<UserSessionEntity>(searchable).and(new TmSpecification<>(tm))
                        .and(new ObjectBelongsToUserIdSpecification<>(userId));
                    return userSessionRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserSessionDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserSessionEntity.class, id)))
                        .map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserSessionCredentialsDTO> findOneCredentials(@NotNull @NotBlank @SecureIdToken String idToken) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Extract user ID.
            String userId = getUserIdFromRawIdToken(idToken);
    
            // Extract user session id.
            String userSessionId = getUserSessionIdFromRawIdToken(idToken);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, userSessionId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserSessionEntity.class, userSessionId)))
                        .map(result -> {
                            // Get credentials
                            UserSessionCredentialsDTO credentials = new UserSessionCredentialsDTO();
                            credentials.setCredentials(result.getSecuredIdToken());
                            credentials.setCredentialsNonExpired(true);
                            credentials.setUserSession(mapper.map(result));
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
    public Mono<UserSessionWithRawIdTokenDTO> create(@NotNull @Identifier String userId, @NotNull @Valid UserSessionCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    UserSessionEntity entityToCreate = mapper.map(creationDTO);
                    entityToCreate.setUser(user);
                    entityToCreate.setExpiryDateTime(DateUtility.dateTimePlus(creationDTO.getDurationInSeconds(), ChronoUnit.SECONDS));
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        String rawIdToken = buildIdToken(userId, entityToCreateWithFields.getId());
                        entityToCreateWithFields.setSecuredIdToken(passwordEncoderService.encode(rawIdToken));
                        return userSessionRepository.save(entityToCreateWithFields).map(result -> {
                            UserSessionWithRawIdTokenDTO map = mapper.mapWithRawIdToken(result);
                            map.setRawIdToken(rawIdToken);
                            return map;
                        });
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserSessionAccessTokenWithIdTokenDTO> createAccessTokenFromIdToken(@NotNull @Valid UserSessionAccessTokenFromIdTokenCreationDTO accessTokenCreation) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Extract user ID.
            String userId = getUserIdFromRawIdToken(accessTokenCreation.getRawIdToken());
    
            // Extract user session id.
            String userSessionId = getUserSessionIdFromRawIdToken(accessTokenCreation.getRawIdToken());

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, userSessionId)
                        .switchIfEmpty(Mono.error(new UnauthorizedException()))
                        .flatMap(existing -> {
                            // Check if session matches.
                            if (!passwordEncoderService.matches(accessTokenCreation.getRawIdToken(), existing.getSecuredIdToken())) {
                                return Mono.error(new UnauthorizedException());
                            }

                            if (DateUtility.dateTimeBeforeNow(existing.getExpiryDateTime()).booleanValue()) {
                                return Mono.error(new UnauthorizedException());
                            }

                            mapper.map(accessTokenCreation, existing);

                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userSessionRepository.save(entityToUpdateWithFields)
                                    .flatMap(userSessionToAccessTokenMapper::map)
                                    .map(accessToken -> {
                                        UserSessionAccessTokenWithIdTokenDTO sessionToken = new UserSessionAccessTokenWithIdTokenDTO();
                                        sessionToken.setAccessToken(accessToken.getAccessToken());
                                        sessionToken.setExpiresAt(accessToken.getExpiresAt());
                                        sessionToken.setExpiresIn(accessToken.getExpiresIn());
                                        sessionToken.setType(accessToken.getType());
                                        sessionToken.setIdToken(accessTokenCreation.getRawIdToken());
                                        sessionToken.setIdTokenExpiresAt(existing.getExpiryDateTime());
                                        sessionToken.setIdTokenExpiresIn(DateUtility.secondsUntil(existing.getExpiryDateTime()));
                                        return sessionToken;
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
    public Mono<UserSessionAccessTokenDTO> createAccessTokenFirst(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionAccessTokenFirstCreationDTO accessTokenCreation) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new UnauthorizedException()))
                        .flatMap(existing -> {
                            if (DateUtility.dateTimeBeforeNow(existing.getExpiryDateTime()).booleanValue()) {
                                return Mono.error(new UnauthorizedException());
                            }
                            mapper.map(accessTokenCreation, existing);
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userSessionRepository.save(entityToUpdateWithFields).flatMap(userSessionToAccessTokenMapper::map);
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
    public Mono<UserSessionDTO> update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserSessionEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(updateDTO, existing);
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userSessionRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<UserSessionDTO> patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserSessionEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(patchDTO, existing);
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userSessionRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<UserSessionDTO> markMfaAsVerified(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserSessionEntity.class, id)))
                        .flatMap(existing -> {
                            existing.setMfaVerified(Boolean.TRUE);
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userSessionRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<Void> delete(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userSessionRepository.findByTmAndUser_Id(tm, userId)
                        .flatMap(existing -> {
                            // Delete entity.
                            return userSessionRepository.delete(existing).then(
                                this.onDelete(existing)
                            ).then();
                        })
                        .then();
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
                    return userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserSessionEntity.class, id)))
                        .flatMap(existing -> {
                            // Delete entity.
                            return userSessionRepository.delete(existing).then(
                                this.onDelete(existing)
                            ).then();
                        });
                });
        });
    }

    // ------------------------------------------ Utility methods.

    /**
     * Generates a random ID token.
     * @return the random ID token.
     */
    protected String generateRandomIdToken() {
        return RandomUtility.generateAlphaNumericToken(Integers.SIXTY_FOUR).toLowerCase();
    }

    /**
     * Builds the API key.
     * @param userId the user ID.
     * @param userSessionId the api key id.
     * @return the full API key.
     */
    protected String buildIdToken(String userId, String userSessionId) {
        String userSessionIdUserId = StringUtils.join(userSessionId, userId);
        String randomIdToken = generateRandomIdToken();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Integers.SIXTY_FOUR; i++) {
            sb.append(userSessionIdUserId.charAt(i));
            sb.append(randomIdToken.charAt(i));
        }

        return sb.toString();
    }

    /**
     * Gets the user session ID from the raw ID token.
     * @param rawIdToken the raw ID token.
     * @return the user session ID.
     */
    protected String getUserSessionIdFromRawIdToken(String rawIdToken) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Integers.SIXTY_FOUR; i += 2) {
            sb.append(rawIdToken.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Gets the user ID from the raw ID token.
     * @param rawIdToken the raw ID token.
     * @return the user ID.
     */
    protected String getUserIdFromRawIdToken(String rawIdToken) {
        StringBuilder sb = new StringBuilder();
        for (int i = Integers.SIXTY_FOUR; i < Integers.ONE_HUNDRED_TWENTY_EIGHT; i += 2) {
            sb.append(rawIdToken.charAt(i));
        }
        return sb.toString();
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private Mono<UserSessionEntity> onPersist(String tm, UserSessionEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        entity.setLastUseDateTime(DateUtility.dateTimeNow());

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private Mono<UserSessionEntity> onUpdate(UserSessionEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<UserSessionEntity> onDelete(UserSessionEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserSessionEntity> postResourceEvent(UserSessionEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_SESSION)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
