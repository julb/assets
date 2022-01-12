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

import me.julb.applications.platformhealth.entities.ComponentCategoryEntity;
import me.julb.applications.platformhealth.entities.ComponentEntity;
import me.julb.applications.platformhealth.entities.IncidentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.entities.mappers.ComponentEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentCategoryRepository;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceComponentRepository;
import me.julb.applications.platformhealth.repositories.specifications.ComponentByComponentCategorySpecification;
import me.julb.applications.platformhealth.services.ComponentService;
import me.julb.applications.platformhealth.services.dto.component.ComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.component.ComponentDTO;
import me.julb.applications.platformhealth.services.dto.component.ComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.component.ComponentUpdateDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.exceptions.ResourceStillReferencedException;
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
 * The component service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ComponentServiceImpl implements ComponentService {

    /**
     * The component category repository.
     */
    @Autowired
    private ComponentCategoryRepository componentCategoryRepository;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The incident component service.
     */
    @Autowired
    private IncidentComponentRepository incidentComponentRepository;

    /**
     * The planned maintenance component service.
     */
    @Autowired
    private PlannedMaintenanceComponentRepository plannedMaintenanceComponentRepository;

    /**
     * The mapper.
     */
    @Autowired
    private ComponentEntityMapper mapper;

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
    public Flux<ComponentDTO> findAll(@NotNull @Identifier String componentCategoryId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMapMany(componentCategory -> {
                    ISpecification<ComponentEntity> spec = new SearchSpecification<ComponentEntity>(searchable).and(new TmSpecification<>(tm)).and(new ComponentByComponentCategorySpecification(componentCategory));
                    return componentRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ComponentDTO> findOne(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMap(componentCategory -> {
                    return componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id)
                        .switchIfEmpty(Mono.error( new ResourceNotFoundException(ComponentEntity.class, id)))
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
    public Mono<ComponentDTO> create(@NotNull @Identifier String componentCategoryId, @NotNull @Valid ComponentCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMap(componentCategory -> {
                    // Update the entity
                    ComponentEntity entityToCreate = mapper.map(creationDTO);
                    entityToCreate.setComponentCategory(componentCategory);
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return componentRepository.save(entityToCreateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ComponentDTO> update(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id, @NotNull @Valid ComponentUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMap(componentCategory -> {
                    return componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id)
                        .switchIfEmpty(Mono.error( new ResourceNotFoundException(ComponentEntity.class, id)))
                        .flatMap(existing -> {
                            // Update the entity
                            mapper.map(updateDTO, existing);
                        
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return componentRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<ComponentDTO> patch(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id, @NotNull @Valid ComponentPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMap(componentCategory -> {
                    return componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id)
                        .switchIfEmpty(Mono.error( new ResourceNotFoundException(ComponentEntity.class, id)))
                        .flatMap(existing -> {
                            // Update the entity
                            mapper.map(patchDTO, existing);
                        
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return componentRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<Void> delete(@NotNull @Identifier String componentCategoryId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMap(componentCategory -> {
                    return componentRepository.findByTmAndComponentCategoryId(tm, componentCategoryId)
                        .flatMap(existing -> {
                            return Mono.zip(
                                incidentComponentRepository.existsByComponentId(existing.getId()),
                                plannedMaintenanceComponentRepository.existsByComponentId(existing.getId())
                            ).flatMap(tuple -> {
                                if (tuple.getT1().booleanValue()) {
                                    return Mono.error(new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), IncidentEntity.class));
                                }

                                if (tuple.getT2().booleanValue()) {
                                    return Mono.error(new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), PlannedMaintenanceEntity.class));
                                }

                                // Proceed to the update
                                return componentRepository.delete(existing)
                                    .then(this.onDelete(existing))
                                    .then();
                            });
                        }).then();
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, componentCategoryId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId)))
                .flatMap(componentCategory -> {
                    return componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, id)))
                        .flatMap(existing -> {
                            return Mono.zip(
                                incidentComponentRepository.existsByComponentId(existing.getId()),
                                plannedMaintenanceComponentRepository.existsByComponentId(existing.getId())
                            ).flatMap(tuple -> {
                                if (tuple.getT1().booleanValue()) {
                                    return Mono.error(new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), IncidentEntity.class));
                                }

                                if (tuple.getT2().booleanValue()) {
                                    return Mono.error(new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), PlannedMaintenanceEntity.class));
                                }

                                // Proceed to the update
                                return componentRepository.delete(existing)
                                    .then(this.onDelete(existing))
                                    .then();
                            });
                        });
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an component.
     * @param tm the trademark.
     * @param entity the entity.
     */
    private Mono<ComponentEntity> onPersist(String tm, ComponentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setPosition(0);

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a component.
     * @param entity the entity.
     */
    private Mono<ComponentEntity> onUpdate(ComponentEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a component.
     * @param entity the entity.
     */
    private Mono<ComponentEntity> onDelete(ComponentEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<ComponentEntity> postResourceEvent(ComponentEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.COMPONENT)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
