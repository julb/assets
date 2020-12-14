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

package me.julb.applications.platformhealth.services.impl;

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
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The component category service implementation.
 * <P>
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
    private IAsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ComponentCategoryDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<ComponentCategoryEntity> spec = new SearchSpecification<ComponentCategoryEntity>(searchable).and(new TmSpecification<>(tm));
        Page<ComponentCategoryEntity> result = componentCategoryRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, ComponentCategoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentCategoryDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity result = componentCategoryRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, id);
        }

        return mappingService.map(result, ComponentCategoryDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentCategoryDTO create(@NotNull @Valid ComponentCategoryCreationDTO creationDTO) {
        // Update the entity
        ComponentCategoryEntity entityToCreate = mappingService.map(creationDTO, ComponentCategoryEntity.class);

        this.onPersist(entityToCreate);

        // Get result back.
        ComponentCategoryEntity result = componentCategoryRepository.save(entityToCreate);
        return mappingService.map(result, ComponentCategoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentCategoryDTO update(@NotNull @Identifier String id, @NotNull @Valid ComponentCategoryUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity existing = componentCategoryRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        ComponentCategoryEntity result = componentCategoryRepository.save(existing);
        return mappingService.map(result, ComponentCategoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ComponentCategoryDTO patch(@NotNull @Identifier String id, @NotNull @Valid ComponentCategoryPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity existing = componentCategoryRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        ComponentCategoryEntity result = componentCategoryRepository.save(existing);
        return mappingService.map(result, ComponentCategoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the component category exists
        ComponentCategoryEntity existing = componentCategoryRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ComponentCategoryEntity.class, id);
        }

        // Delete dependent items.
        componentService.delete(existing.getId());

        // Delete entity.
        componentCategoryRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an component category.
     * @param entity the entity.
     */
    private void onPersist(ComponentCategoryEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setPosition(0);

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a component category.
     * @param entity the entity.
     */
    private void onUpdate(ComponentCategoryEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a component category.
     * @param entity the entity.
     */
    private void onDelete(ComponentCategoryEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(ComponentCategoryEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.COMPONENT_CATEGORY)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
