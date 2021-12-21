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

package me.julb.applications.announcement.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.announcement.entities.AnnouncementEntity;
import me.julb.applications.announcement.entities.mappers.AnnouncementEntityMapper;
import me.julb.applications.announcement.repositories.AnnouncementRepository;
import me.julb.applications.announcement.services.AnnouncementService;
import me.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import me.julb.applications.announcement.services.dto.AnnouncementDTO;
import me.julb.applications.announcement.services.dto.AnnouncementPatchDTO;
import me.julb.applications.announcement.services.dto.AnnouncementUpdateDTO;
import me.julb.applications.announcement.services.exceptions.AnnouncementAlreadyExistsInIntervalException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The announcement service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class AnnouncementServiceImpl implements AnnouncementService {

    /**
     * The announcement repository.
     */
    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * The mapper.
     */
    @Autowired
    private AnnouncementEntityMapper mapper;

    /**
     * The user ref mapper.
     */
    @Autowired
    private UserRefEntityMapper userRefMapper;

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
    public Flux<AnnouncementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<AnnouncementEntity> spec = new SearchSpecification<AnnouncementEntity>(searchable).and(new TmSpecification<>(tm));
            return announcementRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AnnouncementDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return announcementRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AnnouncementEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AnnouncementDTO> create(@NotNull @Valid AnnouncementCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check if not overlapping another one.
            return announcementRepository.existsByTmAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(tm, creationDTO.getVisibilityDateTime().getFrom(), creationDTO.getVisibilityDateTime().getTo())
                .flatMap(otherAnnouncementOverlapExists -> {
                    if (otherAnnouncementOverlapExists.booleanValue()) {
                        return Mono.error(new AnnouncementAlreadyExistsInIntervalException(creationDTO.getVisibilityDateTime().getFrom(), creationDTO.getVisibilityDateTime().getTo()));
                    }

                    // Update the entity
                    AnnouncementEntity entityToCreate = mapper.map(creationDTO);
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return announcementRepository.save(entityToCreateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AnnouncementDTO> update(@NotNull @Identifier String id, @NotNull @Valid AnnouncementUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return announcementRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AnnouncementEntity.class, id)))
                .flatMap(existing -> {
                    // Check if not overlapping another one.
                    return announcementRepository.existsByTmAndIdNotAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(tm, id, updateDTO.getVisibilityDateTime().getFrom(), updateDTO.getVisibilityDateTime().getTo())
                        .flatMap(otherAnnouncementOverlapExists -> {
                            if (otherAnnouncementOverlapExists.booleanValue()) {
                                return Mono.error(new AnnouncementAlreadyExistsInIntervalException(updateDTO.getVisibilityDateTime().getFrom(), updateDTO.getVisibilityDateTime().getTo()));
                            }

                            // Update the entity
                            mapper.map(updateDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return announcementRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<AnnouncementDTO> patch(@NotNull @Identifier String id, @NotNull @Valid AnnouncementPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return announcementRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AnnouncementEntity.class, id)))
                .flatMap(existing -> {
                    // Check if not overlapping another one.
                    final String from;
                    if (patchDTO.getVisibilityDateTime() != null && patchDTO.getVisibilityDateTime().getFrom() != null) {
                        from = patchDTO.getVisibilityDateTime().getFrom();
                    } else {
                        from = existing.getVisibilityDateTime().getFrom();
                    }
                    final String to;
                    if (patchDTO.getVisibilityDateTime() != null && patchDTO.getVisibilityDateTime().getTo() != null) {
                        to = patchDTO.getVisibilityDateTime().getTo();
                    } else {
                        to = existing.getVisibilityDateTime().getTo();
                    }

                    // Check if not overlapping another one.
                    return announcementRepository.existsByTmAndIdNotAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(tm, id, from, to)
                        .flatMap(otherAnnouncementOverlapExists -> {
                            if (otherAnnouncementOverlapExists.booleanValue()) {
                                return Mono.error(new AnnouncementAlreadyExistsInIntervalException(from, to));
                            }

                            // Update the entity
                            mapper.map(patchDTO, existing);
                
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return announcementRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<Void> delete(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return announcementRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AnnouncementEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return announcementRepository.delete(existing).then(
                        this.onDelete(existing)
                    ).then();
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an announcement.
     * @param entity the entity.
     */
    private Mono<AnnouncementEntity> onPersist(String tm, AnnouncementEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connnectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());
    
            // Add author.
            entity.setUser(userRefMapper.map(connnectedUser));
    
            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a announcement.
     * @param entity the entity.
     */
    private Mono<AnnouncementEntity> onUpdate(AnnouncementEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a announcement.
     * @param entity the entity.
     */
    private Mono<AnnouncementEntity> onDelete(AnnouncementEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<AnnouncementEntity> postResourceEvent(AnnouncementEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.ANNOUNCEMENT)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
