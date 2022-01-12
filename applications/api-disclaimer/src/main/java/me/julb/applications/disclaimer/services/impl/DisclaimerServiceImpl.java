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

package me.julb.applications.disclaimer.services.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.disclaimer.entities.DisclaimerEntity;
import me.julb.applications.disclaimer.entities.mappers.DisclaimerEntityMapper;
import me.julb.applications.disclaimer.repositories.DisclaimerRepository;
import me.julb.applications.disclaimer.services.DisclaimerService;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerCreationDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerPatchDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerUpdateDTO;
import me.julb.applications.disclaimer.services.exceptions.DisclaimerIsFrozenException;
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
 * The disclaimer service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DisclaimerServiceImpl implements DisclaimerService {

    /**
     * The disclaimer repository.
     */
    @Autowired
    private DisclaimerRepository disclaimerRepository;

    /**
     * The mapper.
     */
    @Autowired
    private DisclaimerEntityMapper mapper;

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
    public Flux<DisclaimerDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<DisclaimerEntity> spec = new SearchSpecification<DisclaimerEntity>(searchable).and(new TmSpecification<>(tm));
            return disclaimerRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<DisclaimerDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the disclaimer exists
            return disclaimerRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<DisclaimerDTO> create(@NotNull @Valid DisclaimerCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check if disclaimer already exists
            return disclaimerRepository.existsByTmAndCodeIgnoreCaseAndVersion(tm, creationDTO.getCode(), creationDTO.getVersion())
                .flatMap(alreadyExists -> {
                    if (alreadyExists.booleanValue()) {
                        return Mono.error(new ResourceAlreadyExistsException(DisclaimerEntity.class, Map.<String, String> of("code", creationDTO.getCode(), "version", creationDTO.getVersion().toString())));
                    }

                    // Update the entity
                    DisclaimerEntity entityToCreate = mapper.map(creationDTO);
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return disclaimerRepository.save(entityToCreateWithFields).map(mapper::map);
                    });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<DisclaimerDTO> update(@NotNull @Identifier String id, @NotNull @Valid DisclaimerUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return disclaimerRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, id)))
                .flatMap(existing -> {
                    if (existing.getFrozen().booleanValue()) {
                        return Mono.error(new DisclaimerIsFrozenException(id));
                    }

                    // Update the entity
                    mapper.map(updateDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return disclaimerRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<DisclaimerDTO> publish(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return disclaimerRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, id)))
                .flatMap(existing -> {
                    // Finds the other version to deactivate
                    Flux<DisclaimerEntity> disclaimersToDeactivate = disclaimerRepository.findByTmAndIdNotAndCodeIgnoreCaseAndActiveIsTrue(tm, id, existing.getCode())
                        .flatMap(disclaimerToDeactivate -> {
                            disclaimerToDeactivate.setActive(Boolean.FALSE);
                            disclaimerToDeactivate.setActivatedAt(null);
                            return this.onUpdate(disclaimerToDeactivate);
                        });
                    return disclaimerRepository.saveAll(disclaimersToDeactivate)
                        .then()
                        .flatMap(v -> {
                        // Update the entity
                        existing.setActive(true);
                        existing.setFrozen(Boolean.TRUE);
                        existing.setActivatedAt(DateUtility.dateTimeNow());

                        // Proceed to the update
                        return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                            return disclaimerRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<DisclaimerDTO> unpublish(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return disclaimerRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    existing.setActive(false);
                    existing.setActivatedAt(null);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return disclaimerRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<DisclaimerDTO> patch(@NotNull @Identifier String id, @NotNull @Valid DisclaimerPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return disclaimerRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, id)))
                .flatMap(existing -> {
                    if (existing.getFrozen().booleanValue()) {
                        return Mono.error(new DisclaimerIsFrozenException(id));
                    }

                    // Update the entity
                    mapper.map(patchDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return disclaimerRepository.save(entityToUpdateWithFields).map(mapper::map);
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
            return disclaimerRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, id)))
                .flatMap(existing -> {
                    // Check if not frozen.
                    if (existing.getFrozen().booleanValue()) {
                        return Mono.error(new DisclaimerIsFrozenException(id));
                    }

                    // Delete entity.
                    return disclaimerRepository.delete(existing).then(
                        this.onDelete(existing)
                    ).then();
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a disclaimer.
     * @param tm the trademark.
     * @param entity the entity.
     */
    private Mono<DisclaimerEntity> onPersist(String tm, DisclaimerEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(tm);
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setActive(Boolean.FALSE);
        entity.setActivatedAt(null);
        entity.setFrozen(Boolean.FALSE);

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a disclaimer.
     * @param entity the entity.
     */
    private Mono<DisclaimerEntity> onUpdate(DisclaimerEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a disclaimer.
     * @param entity the entity.
     */
    private Mono<DisclaimerEntity> onDelete(DisclaimerEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<DisclaimerEntity> postResourceEvent(DisclaimerEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.DISCLAIMER)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
