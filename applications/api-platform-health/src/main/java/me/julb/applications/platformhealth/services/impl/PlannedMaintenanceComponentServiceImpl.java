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

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.entities.ComponentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceComponentEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.services.PlannedMaintenanceComponentService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenancecomponent.PlannedMaintenanceComponentDTO;
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
import me.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The planned maintenance service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PlannedMaintenanceComponentServiceImpl implements PlannedMaintenanceComponentService {

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The planned maintenance component service.
     */
    @Autowired
    private PlannedMaintenanceComponentRepository plannedMaintenanceComponentRepository;

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
    public Page<PlannedMaintenanceComponentDTO> findAll(@NotNull @Identifier String plannedMaintenanceId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Try to search within the link.
        ISpecification<PlannedMaintenanceComponentEntity> spec = new SearchSpecification<PlannedMaintenanceComponentEntity>(searchable).and(new TmSpecification<>(tm));
        Page<PlannedMaintenanceComponentEntity> result = plannedMaintenanceComponentRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, PlannedMaintenanceComponentDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlannedMaintenanceComponentDTO findOne(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the planned maintenance component exists
        PlannedMaintenanceComponentEntity result = plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceIdAndComponentId(tm, plannedMaintenanceId, componentId);
        if (result == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceComponentEntity.class, Map.<String, String> of("plannedMaintenance", plannedMaintenanceId, "component", componentId));
        }

        return mappingService.map(result, PlannedMaintenanceComponentDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public PlannedMaintenanceComponentDTO create(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the planned maintenance component exists
        PlannedMaintenanceComponentEntity existing = plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceIdAndComponentId(tm, plannedMaintenanceId, componentId);
        if (existing != null) {
            throw new ResourceAlreadyExistsException(PlannedMaintenanceComponentEntity.class, Map.<String, String> of("plannedMaintenance", plannedMaintenanceId, "component", componentId));
        }

        // Add component item.
        PlannedMaintenanceComponentEntity plannedMaintenanceComponentToCreate = new PlannedMaintenanceComponentEntity();
        plannedMaintenanceComponentToCreate.setPlannedMaintenance(plannedMaintenance);
        plannedMaintenanceComponentToCreate.setComponent(component);
        this.onPersist(plannedMaintenanceComponentToCreate);

        PlannedMaintenanceComponentEntity result = plannedMaintenanceComponentRepository.save(plannedMaintenanceComponentToCreate);
        return mappingService.map(result, PlannedMaintenanceComponentDTO.class);
    }

    /**
     * /** {@inheritDoc}
     */
    @Override
    public void delete(@NotNull @Identifier String plannedMaintenanceId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the planned maintenance component exists
        List<PlannedMaintenanceComponentEntity> existings = plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceId(tm, plannedMaintenanceId);
        for (PlannedMaintenanceComponentEntity existing : existings) {
            // Delete entity.
            plannedMaintenanceComponentRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the planned maintenance component exists
        PlannedMaintenanceComponentEntity result = plannedMaintenanceComponentRepository.findByTmAndPlannedMaintenanceIdAndComponentId(tm, plannedMaintenanceId, componentId);
        if (result == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceComponentEntity.class, Map.<String, String> of("plannedMaintenance", plannedMaintenanceId, "component", componentId));
        }

        // Delete entity.
        plannedMaintenanceComponentRepository.delete(result);

        // Handle deletion.
        this.onDelete(result);

    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an planned maintenance component.
     * @param entity the entity.
     */
    private void onPersist(PlannedMaintenanceComponentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when deleting a planned maintenance component.
     * @param entity the entity.
     */
    private void onDelete(PlannedMaintenanceComponentEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(PlannedMaintenanceComponentEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.PLANNED_MAINTENANCE_COMPONENT)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
