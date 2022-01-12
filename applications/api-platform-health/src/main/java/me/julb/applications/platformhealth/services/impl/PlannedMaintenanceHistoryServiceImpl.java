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

package me.julb.applications.platformhealth.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceHistoryEntity;
import me.julb.applications.platformhealth.entities.mappers.PlannedMaintenanceHistoryEntityMapper;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceHistoryRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.repositories.specifications.PlannedMaintenanceHistoryByPlannedMaintenanceSpecification;
import me.julb.applications.platformhealth.services.PlannedMaintenanceHistoryService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryUpdateDTO;
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
 * The planned maintenance service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PlannedMaintenanceHistoryServiceImpl implements PlannedMaintenanceHistoryService {

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The planned maintenance history service.
     */
    @Autowired
    private PlannedMaintenanceHistoryRepository plannedMaintenanceHistoryRepository;

    /**
     * The mapper.
     */
    @Autowired
    private PlannedMaintenanceHistoryEntityMapper mapper;

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
    public Flux<PlannedMaintenanceHistoryDTO> findAll(@NotNull @Identifier String plannedMaintenanceId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMapMany(plannedMaintenance -> {
                    ISpecification<PlannedMaintenanceHistoryEntity> spec = new SearchSpecification<PlannedMaintenanceHistoryEntity>(searchable)
                        .and(new TmSpecification<>(tm)).and(new PlannedMaintenanceHistoryByPlannedMaintenanceSpecification(plannedMaintenance));
                    return plannedMaintenanceHistoryRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PlannedMaintenanceHistoryDTO> findOne(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id)))
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
    public Mono<PlannedMaintenanceHistoryDTO> create(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Valid PlannedMaintenanceHistoryCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    PlannedMaintenanceHistoryEntity entityToCreate = mapper.map(creationDTO);
                    entityToCreate.setPlannedMaintenance(plannedMaintenance);
                    entityToCreate.setPreviousStatus(plannedMaintenance.getStatus());
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return plannedMaintenanceHistoryRepository.save(entityToCreateWithFields)
                            .flatMap(result -> {
                                // Add history item.
                                plannedMaintenance.setStatus(creationDTO.getStatus());
                                plannedMaintenance.setLastUpdatedAt(DateUtility.dateTimeNow());
                                return plannedMaintenanceRepository.save(plannedMaintenance)
                                    .then(postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED))
                                    .thenReturn(mapper.map(result));
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
    public Mono<PlannedMaintenanceHistoryDTO> update(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceHistoryUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(updateDTO, existing);
                        
                            // Proceed to the update
                            return postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED).then(
                                this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return plannedMaintenanceHistoryRepository.save(entityToUpdateWithFields).map(mapper::map);
                                })
                            );
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<PlannedMaintenanceHistoryDTO> patch(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceHistoryPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id)))
                        .flatMap(existing -> {
                            mapper.map(patchDTO, existing);
                        
                            // Proceed to the update
                            return postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED).then(
                                this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return plannedMaintenanceHistoryRepository.save(entityToUpdateWithFields).map(mapper::map);
                                })
                            );
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String plannedMaintenanceId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceId(tm, plannedMaintenanceId)
                        .flatMap(existing -> {
                            return plannedMaintenanceHistoryRepository.delete(existing)
                                .then(this.onDelete(existing))
                                .then();
                        }).then();
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id)))
                        .flatMap(existing -> {
                            return plannedMaintenanceHistoryRepository.delete(existing)
                                .then(this.onDelete(existing))
                                .flatMap(plannedMaintenanceHistoryDeleted -> {
                                    return plannedMaintenanceHistoryRepository.findTopByTmAndPlannedMaintenanceIdOrderByCreatedAtDesc(tm, plannedMaintenanceId)
                                        .flatMap(latestPlannedMaintenanceHistory -> {
                                            plannedMaintenance.setStatus(latestPlannedMaintenanceHistory.getStatus());
                                            plannedMaintenance.setLastUpdatedAt(DateUtility.dateTimeNow());
                                            return plannedMaintenanceRepository.save(plannedMaintenance)
                                                        .then(postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED))
                                                        .then();
                                        });
                                });
                        });
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an planned maintenance history.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceHistoryEntity> onPersist(String tm, PlannedMaintenanceHistoryEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());

            // Add author.
            entity.setUser(userRefMapper.map(connectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a planned maintenance history.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceHistoryEntity> onUpdate(PlannedMaintenanceHistoryEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a planned maintenance history.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceHistoryEntity> onDelete(PlannedMaintenanceHistoryEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<PlannedMaintenanceEntity> postResourceEvent(PlannedMaintenanceEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.PLANNED_MAINTENANCE)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<PlannedMaintenanceHistoryEntity> postResourceEvent(PlannedMaintenanceHistoryEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.PLANNED_MAINTENANCE_HISTORY)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
