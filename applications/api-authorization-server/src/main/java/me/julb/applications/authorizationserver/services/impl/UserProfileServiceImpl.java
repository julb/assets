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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.profile.UserProfileEntity;
import me.julb.applications.authorizationserver.entities.profile.mappers.UserProfileEntityMapper;
import me.julb.applications.authorizationserver.repositories.UserProfileRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.services.UserProfileService;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfilePatchDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileUpdateDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Mono;

/**
 * The user profile service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserProfileServiceImpl implements UserProfileService {

    /**
     * The item repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * The mapper.
     */
    @Autowired
    private UserProfileEntityMapper mapper;

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

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserProfileDTO> findOne(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userProfileRepository.findByTmAndUser_Id(tm, userId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserProfileEntity.class, userId)))
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
    public Mono<UserProfileDTO> create(@NotNull @Identifier String userId, @NotNull @Valid UserProfileCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userProfileRepository.existsByTmAndUser_Id(tm, userId)
                        .flatMap(alreadyExists -> {
                            if (alreadyExists.booleanValue()) {
                                return Mono.error(new ResourceAlreadyExistsException(UserProfileEntity.class, "user", userId));
                            }

                            UserProfileEntity entityToCreate = mapper.map(creationDTO);
                            entityToCreate.setUser(user);
                            return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                return userProfileRepository.save(entityToCreateWithFields).map(mapper::map);
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
    public Mono<UserProfileDTO> update(@NotNull @Identifier String userId, @NotNull @Valid UserProfileUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userProfileRepository.findByTmAndUser_Id(tm, userId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserProfileEntity.class, userId)))
                        .flatMap(existing -> {
                            // Update the entity
                            mapper.map(updateDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userProfileRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<UserProfileDTO> patch(@NotNull @Identifier String userId, @NotNull @Valid UserProfilePatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return userRepository.findByTmAndId(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserEntity.class, userId)))
                .flatMap(user -> {
                    return userProfileRepository.findByTmAndUser_Id(tm, userId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserProfileEntity.class, userId)))
                        .flatMap(existing -> {
                            // Update the entity
                            mapper.map(patchDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return userProfileRepository.save(entityToUpdateWithFields).map(mapper::map);
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
                    return userProfileRepository.findByTmAndUser_Id(tm, userId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserProfileEntity.class, userId)))
                        .flatMap(existing -> {
                            
                            // Delete entity.
                            return userProfileRepository.delete(existing).then(
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
    private Mono<UserProfileEntity> onPersist(String tm, UserProfileEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        entity.setFirstName(StringUtils.capitalize(entity.getFirstName()));
        entity.setLastName(StringUtils.upperCase(entity.getLastName()));

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private Mono<UserProfileEntity> onUpdate(UserProfileEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        entity.setFirstName(StringUtils.capitalize(entity.getFirstName()));
        entity.setLastName(StringUtils.upperCase(entity.getLastName()));

        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<UserProfileEntity> onDelete(UserProfileEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<UserProfileEntity> postResourceEvent(UserProfileEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_PROFILE)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
