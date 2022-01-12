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

import me.julb.applications.platformhealth.entities.ComponentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceComponentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.entities.mappers.PlannedMaintenanceEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.services.PlannedMaintenanceComponentService;
import me.julb.applications.platformhealth.services.PlannedMaintenanceHistoryService;
import me.julb.applications.platformhealth.services.PlannedMaintenanceService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenancePatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceUpdateDTO;
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
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.IdInSpecification;
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
public class PlannedMaintenanceServiceImpl implements PlannedMaintenanceService {

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The planned maintenance history service.
     */
    @Autowired
    private PlannedMaintenanceHistoryService plannedMaintenanceHistoryService;

    /**
     * The planned maintenance component service.
     */
    @Autowired
    private PlannedMaintenanceComponentService plannedMaintenanceComponentService;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The planned maintenance component repository.
     */
    @Autowired
    private PlannedMaintenanceComponentRepository plannedMaintenanceComponentRepository;

    /**
     * The mapper.
     */
    @Autowired
    private PlannedMaintenanceEntityMapper mapper;

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
    public Flux<PlannedMaintenanceDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<PlannedMaintenanceEntity> spec = new SearchSpecification<PlannedMaintenanceEntity>(searchable).and(new TmSpecification<>(tm));
            return plannedMaintenanceRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<PlannedMaintenanceDTO> findAll(@NotNull @Identifier String componentId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMapMany(component -> {
                            return plannedMaintenanceComponentRepository.findByTmAndComponentId(tm, componentId)
                                .map(PlannedMaintenanceComponentEntity::getPlannedMaintenance)
                                .map(PlannedMaintenanceEntity::getId)
                                .collectList()
                                .flatMapMany(plannedMaintenanceIds -> {
                                    ISpecification<PlannedMaintenanceEntity> spec = new SearchSpecification<PlannedMaintenanceEntity>(searchable).and(new TmSpecification<>(tm)).and(new IdInSpecification<>(plannedMaintenanceIds));
                                    return plannedMaintenanceRepository.findAll(spec, pageable).map(mapper::map);
                                });
                        });

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PlannedMaintenanceDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return plannedMaintenanceRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<PlannedMaintenanceDTO> create(@NotNull @Valid PlannedMaintenanceCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Update the entity
            PlannedMaintenanceEntity entityToCreate = mapper.map(creationDTO);
            return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                return plannedMaintenanceRepository.save(entityToCreateWithFields).flatMap(result -> {
                    PlannedMaintenanceHistoryCreationDTO dto = new PlannedMaintenanceHistoryCreationDTO();
                    dto.setLocalizedMessage(creationDTO.getLocalizedMessage());
                    dto.setSendNotification(false);
                    dto.setStatus(result.getStatus());
                    return plannedMaintenanceHistoryService.create(result.getId(), dto)
                        .thenReturn(mapper.map(result));

                });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<PlannedMaintenanceDTO> update(@NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return plannedMaintenanceRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(updateDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return plannedMaintenanceRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<PlannedMaintenanceDTO> patch(@NotNull @Identifier String id, @NotNull @Valid PlannedMaintenancePatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return plannedMaintenanceRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(patchDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return plannedMaintenanceRepository.save(entityToUpdateWithFields).map(mapper::map);
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
            return plannedMaintenanceRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return plannedMaintenanceHistoryService.delete(existing.getId())
                        .then(plannedMaintenanceComponentService.delete(existing.getId()))
                        .then(plannedMaintenanceRepository.delete(existing))
                        .then(this.onDelete(existing))
                        .then();
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an planned maintenance.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceEntity> onPersist(String tm, PlannedMaintenanceEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());
            entity.setStatus(PlannedMaintenanceStatus.DRAFT);

            // Add author.
            entity.setUser(userRefMapper.map(connectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a planned maintenance.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceEntity> onUpdate(PlannedMaintenanceEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a planned maintenance.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceEntity> onDelete(PlannedMaintenanceEntity entity) {
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
}
