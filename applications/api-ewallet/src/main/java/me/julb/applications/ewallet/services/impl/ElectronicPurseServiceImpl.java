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

package me.julb.applications.ewallet.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.entities.ElectronicPurseEntity;
import me.julb.applications.ewallet.entities.mappers.ElectronicPurseEntityMapper;
import me.julb.applications.ewallet.repositories.ElectronicPurseRepository;
import me.julb.applications.ewallet.services.ElectronicPurseOperationService;
import me.julb.applications.ewallet.services.ElectronicPurseService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseCreationWithUserDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.applications.ewallet.services.exceptions.ElectronicPurseOperationCannotBeExecutedInsufficientBalance;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.simple.moneyamount.MoneyAmountDTO;
import me.julb.library.persistence.mongodb.entities.moneyamount.MoneyAmountEntity;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.moneyamount.MoneyAmountBuilder;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
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
 * The electronic purse service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ElectronicPurseServiceImpl implements ElectronicPurseService {

    /**
     * The electronic purse repository.
     */
    @Autowired
    private ElectronicPurseRepository electronicPurseRepository;

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private ElectronicPurseOperationService electronicPurseOperationService;

    /**
     * The mapper.
     */
    @Autowired
    private ElectronicPurseEntityMapper mapper;

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
     * The config source service.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<ElectronicPurseDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<ElectronicPurseEntity> spec = new SearchSpecification<ElectronicPurseEntity>(searchable).and(new TmSpecification<>(tm));
            return electronicPurseRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ElectronicPurseDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, id)))
                .map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ElectronicPurseDTO> findByUserId(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndUser_Id(tm, userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, "userId", userId)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseDTO> create(@NotNull @Valid ElectronicPurseCreationWithUserDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check if not overlapping another one.
            return electronicPurseRepository.existsByTmAndUser_Id(tm, creationDTO.getUser().getId())
                .flatMap(alreadyExists -> {
                    if (alreadyExists.booleanValue()) {
                        return Mono.error(new ResourceAlreadyExistsException(ElectronicPurseEntity.class, "userId", creationDTO.getUser().getId()));
                    }
        
                    // Gets the currency of the current setup
                    ISO4217Currency currency = configSourceService.getTypedProperty(tm, "ewallet.currency", ISO4217Currency.class);

                    // Update the entity
                    ElectronicPurseEntity entityToCreate = mapper.map(creationDTO);
                    entityToCreate.setAmount(new MoneyAmountEntity(Long.valueOf(Integers.ZERO), currency));
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return electronicPurseRepository.save(entityToCreateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseDTO> refreshBalance(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return electronicPurseRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, id)))
                .flatMap(existing -> {
                    return electronicPurseOperationService.findAll(existing.getId())
                        .reduce(new MoneyAmountBuilder(existing.getAmount().getCurrency()), (initial, operation) -> {
                            initial.add(operation.getSignedAmount());
                            return initial;
                        }).flatMap(builder -> {
                            MoneyAmountDTO balance = builder.build();
                            
                            // If balance < 0, throw exception.
                            if (balance.getValue() < 0) {
                                return Mono.error(new ElectronicPurseOperationCannotBeExecutedInsufficientBalance(existing.getId(), existing.getAmount().getCurrency(), balance.getValue()));
                            }

                            // Update purse balance
                            existing.getAmount().setValue(balance.getValue());

                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return electronicPurseRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<ElectronicPurseDTO> update(@NotNull @Identifier String id, @NotNull @Valid ElectronicPurseUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return electronicPurseRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(updateDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return electronicPurseRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseDTO> patch(@NotNull @Identifier String id, @NotNull @Valid ElectronicPursePatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return electronicPurseRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(patchDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return electronicPurseRepository.save(entityToUpdateWithFields).map(mapper::map);
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
            return electronicPurseRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return electronicPurseOperationService.delete(id)
                        .then(electronicPurseRepository.delete(existing))
                        .then(this.onDelete(existing))
                        .then();
                });
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param tm the trademark.
     * @param entity the entity.
     */
    private Mono<ElectronicPurseEntity> onPersist(String tm, ElectronicPurseEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connnectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private Mono<ElectronicPurseEntity> onUpdate(ElectronicPurseEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<ElectronicPurseEntity> onDelete(ElectronicPurseEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<ElectronicPurseEntity> postResourceEvent(ElectronicPurseEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.ELECTRONIC_PURSE)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
