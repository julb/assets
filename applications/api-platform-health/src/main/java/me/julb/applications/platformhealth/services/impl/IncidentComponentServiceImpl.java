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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.entities.ComponentEntity;
import me.julb.applications.platformhealth.entities.IncidentComponentEntity;
import me.julb.applications.platformhealth.entities.IncidentEntity;
import me.julb.applications.platformhealth.entities.mappers.IncidentComponentEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentRepository;
import me.julb.applications.platformhealth.repositories.specifications.IncidentComponentByIncidentSpecification;
import me.julb.applications.platformhealth.services.IncidentComponentService;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentUpdateDTO;
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
 * The incident service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class IncidentComponentServiceImpl implements IncidentComponentService {

    /**
     * The incident repository.
     */
    @Autowired
    private IncidentRepository incidentRepository;

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
     * The mapper.
     */
    @Autowired
    private IncidentComponentEntityMapper mapper;

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
    public Flux<IncidentComponentDTO> findAll(@NotNull @Identifier String incidentId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMapMany(incident -> {
                    ISpecification<IncidentComponentEntity> spec = new SearchSpecification<IncidentComponentEntity>(searchable)
                        .and(new TmSpecification<>(tm)).and(new IncidentComponentByIncidentSpecification(incident));
                    return incidentComponentRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<IncidentComponentDTO> findOne(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMap(incident -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId))))
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
    public Mono<IncidentComponentDTO> create(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMap(incident -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return incidentComponentRepository.existsByTmAndIncidentIdAndComponentId(tm, incidentId, componentId)
                                .flatMap(alreadyExists -> {
                                    if (alreadyExists.booleanValue()) {
                                        return Mono.error(new ResourceAlreadyExistsException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId)));
                                    }

                                    // Update the entity
                                    IncidentComponentEntity entityToCreate = mapper.map(creationDTO);
                                    entityToCreate.setIncident(incident);
                                    entityToCreate.setComponent(component);
                                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                        return incidentComponentRepository.save(entityToCreateWithFields).map(mapper::map);
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
    public Mono<IncidentComponentDTO> update(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMap(incident -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId))))
                                .flatMap(existing -> {
                                    // Update the entity
                                    mapper.map(updateDTO, existing);
                        
                                    // Proceed to the update
                                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                        return incidentComponentRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<IncidentComponentDTO> patch(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMap(incident -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId))))
                                .flatMap(existing -> {
                                    // Update the entity
                                    mapper.map(patchDTO, existing);
                        
                                    // Proceed to the update
                                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                        return incidentComponentRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<Void> delete(@NotNull @Identifier String incidentId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMap(incident -> {
                    return incidentComponentRepository.findByTmAndIncidentId(tm, incidentId)
                        .flatMap(existing -> {
                            return incidentComponentRepository.delete(existing)
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
    public Mono<Void> delete(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return incidentRepository.findByTmAndId(tm, incidentId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentEntity.class, incidentId)))
                .flatMap(incident -> {
                    return componentRepository.findByTmAndId(tm, componentId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentEntity.class, componentId)))
                        .flatMap(component -> {
                            return incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId))))
                                .flatMap(existing -> {
                                    return incidentComponentRepository.delete(existing)
                                        .then(this.onDelete(existing))
                                        .then();
                                });
                        });
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an incident component.
     * @param entity the entity.
     */
    private Mono<IncidentComponentEntity> onPersist(String tm, IncidentComponentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a incident history.
     * @param entity the entity.
     */
    private Mono<IncidentComponentEntity> onUpdate(IncidentComponentEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a incident component.
     * @param entity the entity.
     */
    private Mono<IncidentComponentEntity> onDelete(IncidentComponentEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<IncidentComponentEntity> postResourceEvent(IncidentComponentEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.INCIDENT_COMPONENT)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
