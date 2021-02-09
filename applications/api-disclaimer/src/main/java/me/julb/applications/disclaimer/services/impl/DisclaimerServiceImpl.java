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

package me.julb.applications.disclaimer.services.impl;

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

import me.julb.applications.disclaimer.entities.DisclaimerEntity;
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
 * The disclaimer service implementation.
 * <P>
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
    public Page<DisclaimerDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<DisclaimerEntity> spec = new SearchSpecification<DisclaimerEntity>(searchable).and(new TmSpecification<>(tm));
        Page<DisclaimerEntity> result = disclaimerRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, DisclaimerDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DisclaimerDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity result = disclaimerRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, id);
        }

        return mappingService.map(result, DisclaimerDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DisclaimerDTO create(@NotNull @Valid DisclaimerCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check if not overlapping another one.
        if (disclaimerRepository.existsByTmAndCodeIgnoreCaseAndVersion(tm, creationDTO.getCode(), creationDTO.getVersion())) {
            throw new ResourceAlreadyExistsException(DisclaimerEntity.class, Map.<String, String> of("code", creationDTO.getCode(), "version", creationDTO.getVersion().toString()));
        }

        // Update the entity
        DisclaimerEntity entityToCreate = mappingService.map(creationDTO, DisclaimerEntity.class);
        this.onPersist(entityToCreate);

        DisclaimerEntity result = disclaimerRepository.save(entityToCreate);
        return mappingService.map(result, DisclaimerDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DisclaimerDTO update(@NotNull @Identifier String id, @NotNull @Valid DisclaimerUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity existing = disclaimerRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, id);
        }

        // Check if not frozen.
        if (existing.getFrozen()) {
            throw new DisclaimerIsFrozenException(id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        DisclaimerEntity result = disclaimerRepository.save(existing);
        return mappingService.map(result, DisclaimerDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DisclaimerDTO publish(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity existing = disclaimerRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, id);
        }

        // Finds the other version to deactivate
        List<DisclaimerEntity> disclaimersToDeactivate = disclaimerRepository.findByTmAndIdNotAndCodeIgnoreCaseAndActiveIsTrue(tm, id, existing.getCode());
        for (DisclaimerEntity disclaimerToDeactivate : disclaimersToDeactivate) {
            disclaimerToDeactivate.setActive(Boolean.FALSE);
            disclaimerToDeactivate.setActivatedAt(null);
            this.onUpdate(disclaimerToDeactivate);
        }
        disclaimerRepository.saveAll(disclaimersToDeactivate);

        // Update the entity
        existing.setActive(true);
        existing.setFrozen(Boolean.TRUE);
        existing.setActivatedAt(DateUtility.dateTimeNow());
        this.onUpdate(existing);

        DisclaimerEntity result = disclaimerRepository.save(existing);
        return mappingService.map(result, DisclaimerDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DisclaimerDTO unpublish(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity existing = disclaimerRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, id);
        }

        // Update the entity
        existing.setActive(false);
        existing.setActivatedAt(null);
        this.onUpdate(existing);

        DisclaimerEntity result = disclaimerRepository.save(existing);
        return mappingService.map(result, DisclaimerDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DisclaimerDTO patch(@NotNull @Identifier String id, @NotNull @Valid DisclaimerPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity existing = disclaimerRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, id);
        }

        // Check if not frozen.
        if (existing.getFrozen()) {
            throw new DisclaimerIsFrozenException(id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        DisclaimerEntity result = disclaimerRepository.save(existing);
        return mappingService.map(result, DisclaimerDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity existing = disclaimerRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, id);
        }

        // Check if not frozen.
        if (existing.getFrozen()) {
            throw new DisclaimerIsFrozenException(id);
        }

        // Delete entity.
        disclaimerRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a disclaimer.
     * @param entity the entity.
     */
    private void onPersist(DisclaimerEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setActive(Boolean.FALSE);
        entity.setActivatedAt(null);
        entity.setFrozen(Boolean.FALSE);

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a disclaimer.
     * @param entity the entity.
     */
    private void onUpdate(DisclaimerEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a disclaimer.
     * @param entity the entity.
     */
    private void onDelete(DisclaimerEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(DisclaimerEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.DISCLAIMER)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
