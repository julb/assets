/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.entities.ElectronicPurseEntity;
import me.julb.applications.ewallet.entities.ElectronicPurseOperationEntity;
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
import me.julb.library.dto.simple.moneyamount.MoneyAmountDTO;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The electronic purse service implementation.
 * <P>
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
    private IMappingService mappingService;

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
    public Page<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String electronicPurseId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        ISpecification<ElectronicPurseOperationEntity> spec = new SearchSpecification<ElectronicPurseOperationEntity>(searchable).and(new TmSpecification<>(tm)).and(new ElectronicPurseOperationByElectronicPurseSpecification(electronicPurse));
        Page<ElectronicPurseOperationEntity> result = electronicPurseOperationRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, ElectronicPurseOperationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String electronicPurseId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        List<ElectronicPurseOperationEntity> result = electronicPurseOperationRepository.findByTmAndElectronicPurseId(tm, electronicPurseId);
        return mappingService.mapAsList(result, ElectronicPurseOperationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElectronicPurseOperationDTO findOne(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that the electronic purse operation exists
        ElectronicPurseOperationEntity result = electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id);
        if (result == null) {
            throw new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id);
        }

        return mappingService.map(result, ElectronicPurseOperationDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO create(@NotNull @Identifier String electronicPurseId, @NotNull @Valid ElectronicPurseOperationCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that operation currency is matching electronic purse currency.
        if (!Objects.equal(electronicPurse.getAmount().getCurrency(), creationDTO.getAmount().getCurrency())) {
            throw new ElectronicPurseOperationCannotBeExecutedCurrencyMismatch(electronicPurse.getId(), electronicPurse.getAmount().getCurrency(), creationDTO.getAmount().getCurrency());
        }

        // Update the entity
        ElectronicPurseOperationEntity entityToCreate = mappingService.map(creationDTO, ElectronicPurseOperationEntity.class);
        entityToCreate.setElectronicPurse(electronicPurse);
        entityToCreate.setExecutionDateTime(DateUtility.dateTimeNow());
        this.onPersist(entityToCreate);

        // Get result back.
        ElectronicPurseOperationEntity result = electronicPurseOperationRepository.save(entityToCreate);

        return mappingService.map(result, ElectronicPurseOperationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO update(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that the electronic purse operation exists
        ElectronicPurseOperationEntity existing = electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        ElectronicPurseOperationEntity result = electronicPurseOperationRepository.save(existing);
        return mappingService.map(result, ElectronicPurseOperationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO patch(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that the electronic purse operation exists
        ElectronicPurseOperationEntity existing = electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        ElectronicPurseOperationEntity result = electronicPurseOperationRepository.save(existing);
        return mappingService.map(result, ElectronicPurseOperationDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cancel(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that the electronic purse operation exists
        ElectronicPurseOperationEntity originalOperation = electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id);
        if (originalOperation == null) {
            throw new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id);
        }

        // Check if the operation to cancel has not already been cancelled in another context
        ElectronicPurseOperationType cancellingOperationType;
        switch (originalOperation.getKind()) {
            case CREDIT:
                cancellingOperationType = ElectronicPurseOperationType.DEBIT_CREDIT_OPERATION_CANCELLED;
                break;
            case DEBIT:
                cancellingOperationType = ElectronicPurseOperationType.CREDIT_DEBIT_OPERATION_CANCELLED;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        ElectronicPurseOperationEntity existingCancellingOperation = electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndTypeAndOriginalOperation(tm, electronicPurseId, cancellingOperationType, originalOperation);
        if (existingCancellingOperation != null) {
            throw new ResourceAlreadyExistsException(ElectronicPurseOperationEntity.class, Map.of("electronicPurseId", electronicPurseId, "type", cancellingOperationType.toString(), "originalOperation", originalOperation.getId()));
        }

        // Create cancel operation.
        ElectronicPurseOperationCreationDTO cancelOperation = new ElectronicPurseOperationCreationDTO();
        cancelOperation.setAmount(mappingService.map(originalOperation.getAmount(), MoneyAmountDTO.class));
        cancelOperation.setLocalizedMessage(new HashMap<>());
        cancelOperation.setSendNotification(true);
        cancelOperation.setType(cancellingOperationType);
        create(electronicPurseId, cancelOperation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String electronicPurseId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that the electronic purse operation exists
        List<ElectronicPurseOperationEntity> existings = electronicPurseOperationRepository.findByTmAndElectronicPurseId(tm, electronicPurseId);
        for (ElectronicPurseOperationEntity existing : existings) {
            // Delete entity.
            electronicPurseOperationRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the electronic purse exists
        ElectronicPurseEntity electronicPurse = electronicPurseRepository.findByTmAndId(tm, electronicPurseId);
        if (electronicPurse == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, electronicPurseId);
        }

        // Check that the electronic purse operation exists
        ElectronicPurseOperationEntity existing = electronicPurseOperationRepository.findByTmAndElectronicPurseIdAndId(tm, electronicPurseId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseOperationEntity.class, id);
        }

        // Delete entity.
        electronicPurseOperationRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an electronic purse operation.
     * @param entity the entity.
     */
    private void onPersist(ElectronicPurseOperationEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        // Add author.
        UserRefDTO connnectedUser = securityService.getConnectedUserRefIdentity();
        entity.setUser(mappingService.map(connnectedUser, UserRefEntity.class));

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a electronic purse operation.
     * @param entity the entity.
     */
    private void onUpdate(ElectronicPurseOperationEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a electronic purse operation.
     * @param entity the entity.
     */
    private void onDelete(ElectronicPurseOperationEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(ElectronicPurseOperationEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.ELECTRONIC_PURSE_OPERATION)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
