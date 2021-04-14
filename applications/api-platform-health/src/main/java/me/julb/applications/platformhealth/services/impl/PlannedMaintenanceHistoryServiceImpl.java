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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.entities.PlannedMaintenanceHistoryEntity;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceHistoryRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.repositories.specifications.PlannedMaintenanceHistoryByPlannedMaintenanceSpecification;
import me.julb.applications.platformhealth.services.PlannedMaintenanceHistoryService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryUpdateDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
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
 * The planned maintenance service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class PlannedMaintenanceHistoryServiceImpl implements PlannedMaintenanceHistoryService {

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The planned maintenance history service.
     */
    @Autowired
    private PlannedMaintenanceHistoryRepository plannedMaintenanceHistoryRepository;

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
    public Page<PlannedMaintenanceHistoryDTO> findAll(@NotNull @Identifier String plannedMaintenanceId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        ISpecification<PlannedMaintenanceHistoryEntity> spec =
            new SearchSpecification<PlannedMaintenanceHistoryEntity>(searchable).and(new TmSpecification<>(tm)).and(new PlannedMaintenanceHistoryByPlannedMaintenanceSpecification(plannedMaintenance));
        Page<PlannedMaintenanceHistoryEntity> result = plannedMaintenanceHistoryRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, PlannedMaintenanceHistoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlannedMaintenanceHistoryDTO findOne(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the planned maintenance history exists
        PlannedMaintenanceHistoryEntity result = plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id);
        if (result == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id);
        }

        return mappingService.map(result, PlannedMaintenanceHistoryDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PlannedMaintenanceHistoryDTO create(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Valid PlannedMaintenanceHistoryCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Update the entity
        PlannedMaintenanceHistoryEntity entityToCreate = mappingService.map(creationDTO, PlannedMaintenanceHistoryEntity.class);
        entityToCreate.setPlannedMaintenance(plannedMaintenance);
        entityToCreate.setPreviousStatus(plannedMaintenance.getStatus());
        this.onPersist(entityToCreate);

        // Get result back.
        PlannedMaintenanceHistoryEntity result = plannedMaintenanceHistoryRepository.save(entityToCreate);

        // Add history item.
        plannedMaintenance.setStatus(creationDTO.getStatus());
        plannedMaintenance.setLastUpdatedAt(DateUtility.dateTimeNow());
        plannedMaintenanceRepository.save(plannedMaintenance);
        postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED);

        return mappingService.map(result, PlannedMaintenanceHistoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PlannedMaintenanceHistoryDTO update(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceHistoryUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the planned maintenance history exists
        PlannedMaintenanceHistoryEntity existing = plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        // Update the planned maintenance.
        postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED);

        PlannedMaintenanceHistoryEntity result = plannedMaintenanceHistoryRepository.save(existing);
        return mappingService.map(result, PlannedMaintenanceHistoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PlannedMaintenanceHistoryDTO patch(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceHistoryPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the planned maintenance history exists
        PlannedMaintenanceHistoryEntity existing = plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        // Update the planned maintenance.
        postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED);

        PlannedMaintenanceHistoryEntity result = plannedMaintenanceHistoryRepository.save(existing);
        return mappingService.map(result, PlannedMaintenanceHistoryDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String plannedMaintenanceId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the planned maintenance history exists
        List<PlannedMaintenanceHistoryEntity> existings = plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceId(tm, plannedMaintenanceId);
        for (PlannedMaintenanceHistoryEntity existing : existings) {
            // Delete entity.
            plannedMaintenanceHistoryRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the planned maintenance exists
        PlannedMaintenanceEntity plannedMaintenance = plannedMaintenanceRepository.findByTmAndId(tm, plannedMaintenanceId);
        if (plannedMaintenance == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceEntity.class, plannedMaintenanceId);
        }

        // Check that the planned maintenance history exists
        PlannedMaintenanceHistoryEntity existing = plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndId(tm, plannedMaintenanceId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(PlannedMaintenanceHistoryEntity.class, id);
        }

        // Delete entity.
        plannedMaintenanceHistoryRepository.delete(existing);

        // Update status of the incident.
        PlannedMaintenanceHistoryEntity latestIncidentHistory = plannedMaintenanceHistoryRepository.findTopByTmAndPlannedMaintenanceIdOrderByCreatedAtDesc(tm, plannedMaintenanceId);
        if (latestIncidentHistory != null) {
            plannedMaintenance.setStatus(latestIncidentHistory.getStatus());
            plannedMaintenance.setLastUpdatedAt(DateUtility.dateTimeNow());
            plannedMaintenanceRepository.save(plannedMaintenance);

            // Update the planned maintenance.
            postResourceEvent(plannedMaintenance, ResourceEventType.UPDATED);
        }

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an planned maintenance history.
     * @param entity the entity.
     */
    private void onPersist(PlannedMaintenanceHistoryEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        // Add author.
        AuthenticatedUserDTO connnectedUser = securityService.getConnectedUserIdentity();
        entity.setUser(new UserRefEntity());
        entity.getUser().setDisplayName(connnectedUser.getDisplayName());
        entity.getUser().setE164Number(connnectedUser.getE164Number());
        entity.getUser().setFirstName(connnectedUser.getFirstName());
        entity.getUser().setId(connnectedUser.getUserId());
        entity.getUser().setLastName(connnectedUser.getLastName());
        entity.getUser().setLocale(connnectedUser.getLocale());
        entity.getUser().setMail(connnectedUser.getMail());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a planned maintenance history.
     * @param entity the entity.
     */
    private void onUpdate(PlannedMaintenanceHistoryEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a planned maintenance history.
     * @param entity the entity.
     */
    private void onDelete(PlannedMaintenanceHistoryEntity entity) {
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

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(PlannedMaintenanceHistoryEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.PLANNED_MAINTENANCE_HISTORY)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
