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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;

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
    public Page<ComponentDTO> findAll(@NotNull @Identifier String componentCategoryId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        ISpecification<ComponentEntity> spec = new SearchSpecification<ComponentEntity>(searchable).and(new TmSpecification<>(tm)).and(new ComponentByComponentCategorySpecification(componentCategory));
        Page<ComponentEntity> result = componentRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentDTO findOne(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        // Check that the component exists
        ComponentEntity result = componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id);
        if (result == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO create(@NotNull @Identifier String componentCategoryId, @NotNull @Valid ComponentCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        // Update the entity
        ComponentEntity entityToCreate = mapper.map(creationDTO);
        entityToCreate.setComponentCategory(componentCategory);
        this.onPersist(entityToCreate);

        // Get result back.
        ComponentEntity result = componentRepository.save(entityToCreate);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO update(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id, @NotNull @Valid ComponentUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        // Check that the component exists
        ComponentEntity existing = componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, id);
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        ComponentEntity result = componentRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentDTO patch(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id, @NotNull @Valid ComponentPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        // Check that the component category history exists
        ComponentEntity existing = componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, id);
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        ComponentEntity result = componentRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String componentCategoryId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        // Check that the component exists
        List<ComponentEntity> existings = componentRepository.findByTmAndComponentCategoryId(tm, componentCategoryId);
        for (ComponentEntity existing : existings) {
            // Check if component used.
            if (plannedMaintenanceComponentRepository.existsByComponentId(existing.getId())) {
                throw new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), PlannedMaintenanceEntity.class);
            }

            // Delete entity.
            componentRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity componentCategory = componentCategoryRepository.findByTmAndId(tm, componentCategoryId);
        if (componentCategory == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, componentCategoryId);
        }

        // Check that the component exists
        ComponentEntity existing = componentRepository.findByTmAndComponentCategoryIdAndId(tm, componentCategoryId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, id);
        }

        // Check if component used.
        if (incidentComponentRepository.existsByComponentId(existing.getId())) {
            throw new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), IncidentEntity.class);
        }

        // Check if component used.
        if (plannedMaintenanceComponentRepository.existsByComponentId(existing.getId())) {
            throw new ResourceStillReferencedException(ComponentEntity.class, existing.getId(), PlannedMaintenanceEntity.class);
        }

        // Delete entity.
        componentRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an component.
     * @param entity the entity.
     */
    private void onPersist(ComponentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setPosition(0);

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a component.
     * @param entity the entity.
     */
    private void onUpdate(ComponentEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a component.
     * @param entity the entity.
     */
    private void onDelete(ComponentEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(ComponentEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.COMPONENT)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
