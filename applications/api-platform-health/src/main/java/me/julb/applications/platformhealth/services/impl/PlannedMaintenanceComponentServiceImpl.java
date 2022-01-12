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

import java.util.Map;

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
import me.julb.applications.platformhealth.entities.mappers.PlannedMaintenanceComponentEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.repositories.specifications.PlannedMaintenanceComponentByPlannedMaintenanceSpecification;
import me.julb.applications.platformhealth.services.PlannedMaintenanceComponentService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenancecomponent.PlannedMaintenanceComponentDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
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
public class PlannedMaintenanceComponentServiceImpl implements PlannedMaintenanceComponentService {

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The planned maintenance component service.
     */
    @Autowired
    private PlannedMaintenanceComponentRepository plannedMaintenanceComponentRepository;

    /**
     * The mapper.
     */
    @Autowired
    private PlannedMaintenanceComponentEntityMapper mapper;

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
    public Flux<PlannedMaintenanceComponentDTO> findAll(@NotNull @Identifier String plannedMaintenanceId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMapMany(plannedMaintenance -> {
                    ISpecification<PlannedMaintenanceComponentEntity> spec = new SearchSpecification<PlannedMaintenanceComponentEntity>(searchable)
                        .and(new TmSpecification<>(tm)).and(new PlannedMaintenanceComponentByPlannedMaintenanceSpecification(plannedMaintenance));
                    return plannedMaintenanceComponentRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PlannedMaintenanceComponentDTO> findOne(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceIdAndComponentId(tm, plannedMaintenanceId, componentId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceComponentEntity.class, Map.<String, String> of("plannedMaintenance", plannedMaintenanceId, "component", componentId))))
                                .map(mapper::map);
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
    public Mono<PlannedMaintenanceComponentDTO> create(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return plannedMaintenanceComponentRepository.existsByTmAndPlannedMaintenanceIdAndComponentId(tm, plannedMaintenanceId, componentId)
                                .flatMap(alreadyExists -> {
                                    if (alreadyExists.booleanValue()) {
                                        return Mono.error(new ResourceAlreadyExistsException(PlannedMaintenanceComponentEntity.class, Map.<String, String> of("plannedMaintenance", plannedMaintenanceId, "component", componentId)));
                                    }

                                    // Update the entity
                                    PlannedMaintenanceComponentEntity entityToCreate = new PlannedMaintenanceComponentEntity();
                                    entityToCreate.setPlannedMaintenance(plannedMaintenance);
                                    entityToCreate.setComponent(component);
                                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                        return plannedMaintenanceComponentRepository.save(entityToCreateWithFields).map(mapper::map);
                                    });
                                });
                        });
                });
        });
    }

    /**
     * /** {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String plannedMaintenanceId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceId(tm, plannedMaintenanceId)
                        .flatMap(existing -> {
                            return plannedMaintenanceComponentRepository.delete(existing)
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
    public Mono<Void> delete(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId)))
                .flatMap(plannedMaintenance -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceIdAndComponentId(tm, plannedMaintenanceId, componentId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(PlannedMaintenanceComponentEntity.class, Map.<String, String> of("plannedMaintenance", plannedMaintenanceId, "component", componentId))))
                                .flatMap(existing -> {
                                    return plannedMaintenanceComponentRepository.delete(existing)
                                        .then(this.onDelete(existing))
                                        .then();
                                });
                        });
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an planned maintenance component.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceComponentEntity> onPersist(String tm, PlannedMaintenanceComponentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when deleting a planned maintenance component.
     * @param entity the entity.
     */
    private Mono<PlannedMaintenanceComponentEntity> onDelete(PlannedMaintenanceComponentEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<PlannedMaintenanceComponentEntity> postResourceEvent(PlannedMaintenanceComponentEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.PLANNED_MAINTENANCE_COMPONENT)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
