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

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.entities.ElectronicPurseEntity;
import me.julb.applications.ewallet.entities.ElectronicPurseOperationEntity;
import me.julb.applications.ewallet.entities.mappers.ElectronicPurseOperationEntityMapper;
import me.julb.applications.ewallet.repositories.ElectronicPurseOperationRepository;
import me.julb.applications.ewallet.repositories.ElectronicPurseRepository;
import me.julb.applications.ewallet.repositories.specifications.ElectronicPurseOperationByElectronicPurseSpecification;
import me.julb.applications.ewallet.services.ElectronicPurseOperationService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationCreationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationType;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.applications.ewallet.services.exceptions.ElectronicPurseOperationCannotBeExecutedCurrencyMismatch;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.mapping.entities.moneyamount.mappers.MoneyAmountEntityMapper;
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
 * The electronic purse service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ElectronicPurseOperationServiceImpl implements ElectronicPurseOperationService {

    /**
     * The electronic purse repository.
     */
    @Autowired
    private ElectronicPurseRepository electronicPurseRepository;

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private ElectronicPurseOperationRepository electronicPurseOperationRepository;

    /**
     * The mapper.
     */
    @Autowired
    private ElectronicPurseOperationEntityMapper mapper;

    /**
     * The user ref mapper.
     */
    @Autowired
    private UserRefEntityMapper userRefMmapper;

    /**
     * The money amount mapper.
     */
    @Autowired
    private MoneyAmountEntityMapper moneyAmountMapper;

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
    public Flux<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String electronicPurseId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMapMany(electronicPurse -> {
                    ISpecification<ElectronicPurseOperationEntity> spec = new SearchSpecification<ElectronicPurseOperationEntity>(searchable)
                        .and(new TmSpecification<>(tm))
                        .and(new ElectronicPurseOperationByElectronicPurseSpecification(electronicPurse));

                    return electronicPurseOperationRepository.findAll(spec, pageable).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String electronicPurseId) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMapMany(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseId(tm, electronicPurseId)
                        .map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ElectronicPurseOperationDTO> findOne(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id)))
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
    public Mono<ElectronicPurseOperationDTO> create(@NotNull @Identifier String electronicPurseId, @NotNull @Valid ElectronicPurseOperationCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    // Check that operation currency is matching electronic purse currency.
                    if (!Objects.equal(electronicPurse.getAmount().getCurrency(), creationDTO.getAmount().getCurrency())) {
                        throw new ElectronicPurseOperationCannotBeExecutedCurrencyMismatch(electronicPurse.getId(), electronicPurse.getAmount().getCurrency(), creationDTO.getAmount().getCurrency());
                    }

                    // Update the entity
                    ElectronicPurseOperationEntity entityToCreate = mapper.map(creationDTO);
                    entityToCreate.setElectronicPurse(electronicPurse);
                    entityToCreate.setExecutionDateTime(DateUtility.dateTimeNow());
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return electronicPurseOperationRepository.save(entityToCreateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseOperationDTO> update(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id)))
                        .flatMap(existing -> {
                            // Update the entity
                            mapper.map(updateDTO, existing);
                        
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return electronicPurseOperationRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<ElectronicPurseOperationDTO> patch(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id)))
                        .flatMap(existing -> {
                            // Update the entity
                            mapper.map(patchDTO, existing);
                        
                            // Proceed to the update
                            return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                return electronicPurseOperationRepository.save(entityToUpdateWithFields).map(mapper::map);
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
    public Mono<Void> cancel(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id)))
                        .flatMap(originalOperation -> {
                            ElectronicPurseOperationType cancellingOperationType;
                            switch (originalOperation.getKind()) {
                                case CREDIT:
                                    cancellingOperationType = ElectronicPurseOperationType.DEBIT_CREDIT_OPERATION_CANCELLED;
                                    break;
                                case DEBIT:
                                    cancellingOperationType = ElectronicPurseOperationType.CREDIT_DEBIT_OPERATION_CANCELLED;
                                    break;
                                default:
                                    return Mono.error(new UnsupportedOperationException());
                            }

                            // Create cancel operation.
                            ElectronicPurseOperationCreationDTO cancelOperation = new ElectronicPurseOperationCreationDTO();
                            cancelOperation.setAmount(moneyAmountMapper.map(originalOperation.getAmount()));
                            cancelOperation.setLocalizedMessage(new HashMap<>());
                            cancelOperation.setSendNotification(true);
                            cancelOperation.setType(cancellingOperationType);

                            // If an operation already exists, fail. Otherwise create the cancel operation.
                            return electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndTypeAndOriginalOperation(tm, electronicPurseId, cancellingOperationType, originalOperation)
                                .flatMap(existingCancellingOperation -> Mono.error(new ResourceAlreadyExistsException(ElectronicPurseOperationEntity.class, Map.of("electronicPurseId", electronicPurseId, "type", cancellingOperationType.toString(), "originalOperation", originalOperation.getId()))))
                                .or(create(electronicPurseId, cancelOperation))
                                .then();
                        });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String electronicPurseId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseId(tm, electronicPurseId)
                        .flatMap(existing -> {
                            // Delete entity.
                            return electronicPurseOperationRepository.delete(existing).then(this.onDelete(existing));
                        }).then();
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return electronicPurseRepository.findByTmAndId(tm, electronicPurseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId)))
                .flatMap(electronicPurse -> {
                    return electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id)))
                        .flatMap(existing -> {
                            // Delete entity.
                            return electronicPurseOperationRepository.delete(existing)
                                .then(this.onDelete(existing))
                                .then();
                        });
                });
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an electronic purse operation.
     * @param entity the entity.
     */
    private Mono<ElectronicPurseOperationEntity> onPersist(String tm, ElectronicPurseOperationEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connnectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());

            // Add author.
            entity.setUser(userRefMmapper.map(connnectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a electronic purse operation.
     * @param entity the entity.
     */
    private Mono<ElectronicPurseOperationEntity> onUpdate(ElectronicPurseOperationEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a electronic purse operation.
     * @param entity the entity.
     */
    private Mono<ElectronicPurseOperationEntity> onDelete(ElectronicPurseOperationEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<ElectronicPurseOperationEntity> postResourceEvent(ElectronicPurseOperationEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.ELECTRONIC_PURSE_OPERATION)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
