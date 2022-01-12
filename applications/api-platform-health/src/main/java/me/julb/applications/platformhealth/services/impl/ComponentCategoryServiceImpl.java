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
import me.julb.applications.platformhealth.entities.mappers.ComponentCategoryEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentCategoryRepository;
import me.julb.applications.platformhealth.services.ComponentCategoryService;
import me.julb.applications.platformhealth.services.ComponentService;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryUpdateDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
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
 * The component category service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ComponentCategoryServiceImpl implements ComponentCategoryService {

    /**
     * The component category repository.
     */
    @Autowired
    private ComponentCategoryRepository componentCategoryRepository;

    /**
     * The component service.
     */
    @Autowired
    private ComponentService componentService;

    /**
     * The mapper.
     */
    @Autowired
    private ComponentCategoryEntityMapper mapper;

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
    public Flux<ComponentCategoryDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<ComponentCategoryEntity> spec = new SearchSpecification<ComponentCategoryEntity>(searchable).and(new TmSpecification<>(tm));
            return componentCategoryRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ComponentCategoryDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ComponentCategoryDTO> create(@NotNull @Valid ComponentCategoryCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Update the entity
            ComponentCategoryEntity entityToCreate = mapper.map(creationDTO);
            return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                return componentCategoryRepository.save(entityToCreateWithFields).map(mapper::map);
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ComponentCategoryDTO> update(@NotNull @Identifier String id, @NotNull @Valid ComponentCategoryUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(updateDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return componentCategoryRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ComponentCategoryDTO> patch(@NotNull @Identifier String id, @NotNull @Valid ComponentCategoryPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return componentCategoryRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(patchDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return componentCategoryRepository.save(entityToUpdateWithFields).map(mapper::map);
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
            return componentCategoryRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ComponentCategoryEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return componentService.delete(existing.getId())
                        .then(componentCategoryRepository.delete(existing))
                        .then(this.onDelete(existing))
                        .then();
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an component category.
     * @param tm the trademark.
     * @param entity the entity.
     */
    private Mono<ComponentCategoryEntity> onPersist(String tm, ComponentCategoryEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setPosition(0);

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a component category.
     * @param entity the entity.
     */
    private Mono<ComponentCategoryEntity> onUpdate(ComponentCategoryEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a component category.
     * @param entity the entity.
     */
    private Mono<ComponentCategoryEntity> onDelete(ComponentCategoryEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<ComponentCategoryEntity> postResourceEvent(ComponentCategoryEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.COMPONENT_CATEGORY)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
