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

import me.julb.applications.platformhealth.entities.IncidentEntity;
import me.julb.applications.platformhealth.entities.IncidentHistoryEntity;
import me.julb.applications.platformhealth.entities.mappers.IncidentHistoryEntityMapper;
import me.julb.applications.platformhealth.repositories.IncidentHistoryRepository;
import me.julb.applications.platformhealth.repositories.IncidentRepository;
import me.julb.applications.platformhealth.repositories.specifications.IncidentHistoryByIncidentSpecification;
import me.julb.applications.platformhealth.services.IncidentHistoryService;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryUpdateDTO;
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
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;

/**
 * The incident service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class IncidentHistoryServiceImpl implements IncidentHistoryService {

    /**
     * The incident repository.
     */
    @Autowired
    private IncidentRepository incidentRepository;

    /**
     * The incident history service.
     */
    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    /**
     * The mapper.
     */
    @Autowired
    private IncidentHistoryEntityMapper mapper;

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
    public Page<IncidentHistoryDTO> findAll(@NotNull @Identifier String incidentId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        ISpecification<IncidentHistoryEntity> spec = new SearchSpecification<IncidentHistoryEntity>(searchable).and(new TmSpecification<>(tm)).and(new IncidentHistoryByIncidentSpecification(incident));
        Page<IncidentHistoryEntity> result = incidentHistoryRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentHistoryDTO findOne(@NotNull @Identifier String incidentId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the incident history exists
        IncidentHistoryEntity result = incidentHistoryRepository.findByTmAndIncidentIdAndId(tm, incidentId, id);
        if (result == null) {
            throw new ResourceNotFoundException(IncidentHistoryEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentHistoryDTO create(@NotNull @Identifier String incidentId, @NotNull @Valid IncidentHistoryCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Update the entity
        IncidentHistoryEntity entityToCreate = mapper.map(creationDTO);
        entityToCreate.setIncident(incident);
        entityToCreate.setPreviousStatus(incident.getStatus());
        this.onPersist(entityToCreate);

        // Get result back.
        IncidentHistoryEntity result = incidentHistoryRepository.save(entityToCreate);

        // Add history item.
        incident.setStatus(creationDTO.getStatus());
        incident.setLastUpdatedAt(DateUtility.dateTimeNow());
        incidentRepository.save(incident);
        postResourceEvent(incident, ResourceEventType.UPDATED);

        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentHistoryDTO update(@NotNull @Identifier String incidentId, @NotNull @Identifier String id, @NotNull @Valid IncidentHistoryUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the incident history exists
        IncidentHistoryEntity existing = incidentHistoryRepository.findByTmAndIncidentIdAndId(tm, incidentId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(IncidentHistoryEntity.class, id);
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        // Incident updated
        postResourceEvent(incident, ResourceEventType.UPDATED);

        IncidentHistoryEntity result = incidentHistoryRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentHistoryDTO patch(@NotNull @Identifier String incidentId, @NotNull @Identifier String id, @NotNull @Valid IncidentHistoryPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the incident history exists
        IncidentHistoryEntity existing = incidentHistoryRepository.findByTmAndIncidentIdAndId(tm, incidentId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(IncidentHistoryEntity.class, id);
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        // Incident updated
        postResourceEvent(incident, ResourceEventType.UPDATED);

        IncidentHistoryEntity result = incidentHistoryRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String incidentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the incident history exists
        List<IncidentHistoryEntity> existings = incidentHistoryRepository.findByTmAndIncidentId(tm, incidentId);
        for (IncidentHistoryEntity existing : existings) {
            // Delete entity.
            incidentHistoryRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String incidentId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the incident history exists
        IncidentHistoryEntity existing = incidentHistoryRepository.findByTmAndIncidentIdAndId(tm, incidentId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(IncidentHistoryEntity.class, id);
        }

        // Delete entity.
        incidentHistoryRepository.delete(existing);

        // Update status of the incident.
        IncidentHistoryEntity latestIncidentHistory = incidentHistoryRepository.findTopByTmAndIncidentIdOrderByCreatedAtDesc(tm, incidentId);
        if (latestIncidentHistory != null) {
            incident.setStatus(latestIncidentHistory.getStatus());
            incident.setLastUpdatedAt(DateUtility.dateTimeNow());
            incidentRepository.save(incident);

            // Incident updated
            postResourceEvent(incident, ResourceEventType.UPDATED);
        }

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an incident history.
     * @param entity the entity.
     */
    private void onPersist(IncidentHistoryEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        // Add author.
        UserRefDTO connnectedUser = securityService.getConnectedUserRefIdentity();
        entity.setUser(userRefMapper.map(connnectedUser));

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a incident history.
     * @param entity the entity.
     */
    private void onUpdate(IncidentHistoryEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a incident history.
     * @param entity the entity.
     */
    private void onDelete(IncidentHistoryEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(IncidentEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.INCIDENT)
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
    private void postResourceEvent(IncidentHistoryEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.INCIDENT_HISTORY)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
