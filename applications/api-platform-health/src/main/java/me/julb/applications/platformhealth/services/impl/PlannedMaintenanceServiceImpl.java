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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
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
import me.julb.applications.platformhealth.entities.mappers.PlannedMaintenanceEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceComponentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.services.PlannedMaintenanceComponentService;
import me.julb.applications.platformhealth.services.PlannedMaintenanceHistoryService;
import me.julb.applications.platformhealth.services.PlannedMaintenanceService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenancePatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceUpdateDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.IdInSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;

/**
 * The planned maintenance service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PlannedMaintenanceServiceImpl implements PlannedMaintenanceService {

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The planned maintenance history service.
     */
    @Autowired
    private PlannedMaintenanceHistoryService plannedMaintenanceHistoryService;

    /**
     * The planned maintenance component service.
     */
    @Autowired
    private PlannedMaintenanceComponentService plannedMaintenanceComponentService;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The planned maintenance component repository.
     */
    @Autowired
    private PlannedMaintenanceComponentRepository plannedMaintenanceComponentRepository;

    /**
     * The mapper.
     */
    @Autowired
    private PlannedMaintenanceEntityMapper mapper;

    /**
     * The user ref mapper.
     */
    @Autowired
    private UserRefEntityMapper userRefMapper;

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
    public Page<PlannedMaintenanceDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<PlannedMaintenanceEntity> spec = new SearchSpecification<PlannedMaintenanceEntity>(searchable).and(new TmSpecification<>(tm));
        Page<PlannedMaintenanceEntity> result = plannedMaintenanceRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PlannedMaintenanceDTO> findAll(@NotNull @Identifier String componentId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check component
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Fetch planned maintenances linked.
        List<PlannedMaintenanceComponentEntity> plannedMaintenanceComponents = plannedMaintenanceComponentRepository.findByTmAndComponentId(tm, componentId);
        Collection<String> plannedMaintenanceIds = plannedMaintenanceComponents.stream().map(PlannedMaintenanceComponentEntity::getPlannedMaintenance).map(PlannedMaintenanceEntity::getId).collect(Collectors.toSet());

        ISpecification<PlannedMaintenanceEntity> spec = new SearchSpecification<PlannedMaintenanceEntity>(searchable).and(new TmSpecification<>(tm)).and(new IdInSpecification<>(plannedMaintenanceIds));
        Page<PlannedMaintenanceEntity> result = plannedMaintenanceRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlannedMaintenanceDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity result = plannedMaintenanceRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PlannedMaintenanceDTO create(@NotNull @Valid PlannedMaintenanceCreationDTO creationDTO) {
        // Update the entity
        PlannedMaintenanceEntity entityToCreate = mapper.map(creationDTO);
        this.onPersist(entityToCreate);

        // Get result back.
        PlannedMaintenanceEntity result = plannedMaintenanceRepository.save(entityToCreate);

        // Add history item.
        PlannedMaintenanceHistoryCreationDTO dto = new PlannedMaintenanceHistoryCreationDTO();
        dto.setLocalizedMessage(creationDTO.getLocalizedMessage());
        dto.setSendNotification(false);
        dto.setStatus(result.getStatus());
        plannedMaintenanceHistoryService.create(result.getId(), dto);

        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PlannedMaintenanceDTO update(@NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity existing = plannedMaintenanceRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, id);
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        PlannedMaintenanceEntity result = plannedMaintenanceRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PlannedMaintenanceDTO patch(@NotNull @Identifier String id, @NotNull @Valid PlannedMaintenancePatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity existing = plannedMaintenanceRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, id);
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        PlannedMaintenanceEntity result = plannedMaintenanceRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity existing = plannedMaintenanceRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, id);
        }

        // Delete dependent items.
        plannedMaintenanceHistoryService.delete(existing.getId());
        plannedMaintenanceComponentService.delete(existing.getId());

        // Delete entity.
        plannedMaintenanceRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an planned maintenance.
     * @param entity the entity.
     */
    private void onPersist(PlannedMaintenanceEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setStatus(PlannedMaintenanceStatus.DRAFT);

        // Add author.
        UserRefDTO connnectedUser = securityService.getConnectedUserRefIdentity();
        entity.setUser(userRefMapper.map(connnectedUser));

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a planned maintenance.
     * @param entity the entity.
     */
    private void onUpdate(PlannedMaintenanceEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a planned maintenance.
     * @param entity the entity.
     */
    private void onDelete(PlannedMaintenanceEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(PlannedMaintenanceEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.PLANNED_MAINTENANCE)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
