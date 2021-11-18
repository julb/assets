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
import me.julb.applications.platformhealth.entities.IncidentComponentEntity;
import me.julb.applications.platformhealth.entities.IncidentEntity;
import me.julb.applications.platformhealth.entities.mappers.IncidentEntityMapper;
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentRepository;
import me.julb.applications.platformhealth.services.IncidentComponentService;
import me.julb.applications.platformhealth.services.IncidentHistoryService;
import me.julb.applications.platformhealth.services.IncidentService;
import me.julb.applications.platformhealth.services.dto.incident.IncidentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentStatus;
import me.julb.applications.platformhealth.services.dto.incident.IncidentUpdateDTO;
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
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The incident service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class IncidentServiceImpl implements IncidentService {

    /**
     * The incident repository.
     */
    @Autowired
    private IncidentRepository incidentRepository;

    /**
     * The incident history service.
     */
    @Autowired
    private IncidentHistoryService incidentHistoryService;

    /**
     * The incident component service.
     */
    @Autowired
    private IncidentComponentService incidentComponentService;

    /**
     * The component repository.
     */
    @Autowired
    private ComponentRepository componentRepository;

    /**
     * The incident component repository.
     */
    @Autowired
    private IncidentComponentRepository incidentComponentRepository;

    /**
     * The mapper.
     */
    @Autowired
    private IncidentEntityMapper mapper;

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
    public Page<IncidentDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<IncidentEntity> spec = new SearchSpecification<IncidentEntity>(searchable).and(new TmSpecification<>(tm));
        Page<IncidentEntity> result = incidentRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<IncidentDTO> findAll(@NotNull @Identifier String componentId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check component
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Fetch incidents linked.
        List<IncidentComponentEntity> incidentComponents = incidentComponentRepository.findByTmAndComponentId(tm, componentId);
        Collection<String> incidentIds = incidentComponents.stream().map(IncidentComponentEntity::getIncident).map(IncidentEntity::getId).collect(Collectors.toSet());

        ISpecification<IncidentEntity> spec = new SearchSpecification<IncidentEntity>(searchable).and(new TmSpecification<>(tm)).and(new IdInSpecification<>(incidentIds));
        Page<IncidentEntity> result = incidentRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity result = incidentRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentDTO create(@NotNull @Valid IncidentCreationDTO creationDTO) {
        // Update the entity
        IncidentEntity entityToCreate = mapper.map(creationDTO);

        this.onPersist(entityToCreate);

        // Get result back.
        IncidentEntity result = incidentRepository.save(entityToCreate);

        // Add history item.
        IncidentHistoryCreationDTO dto = new IncidentHistoryCreationDTO();
        dto.setLocalizedMessage(creationDTO.getLocalizedMessage());
        dto.setSendNotification(false);
        dto.setStatus(result.getStatus());
        incidentHistoryService.create(result.getId(), dto);

        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentDTO update(@NotNull @Identifier String id, @NotNull @Valid IncidentUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity existing = incidentRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, id);
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        IncidentEntity result = incidentRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentDTO patch(@NotNull @Identifier String id, @NotNull @Valid IncidentPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity existing = incidentRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, id);
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        IncidentEntity result = incidentRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity existing = incidentRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, id);
        }

        // Delete dependent items.
        incidentHistoryService.delete(existing.getId());
        incidentComponentService.delete(existing.getId());

        // Delete entity.
        incidentRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an incident.
     * @param entity the entity.
     */
    private void onPersist(IncidentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setStatus(IncidentStatus.DRAFT);

        // Add author.
        UserRefDTO connnectedUser = securityService.getConnectedUserRefIdentity();
        entity.setUser(userRefMapper.map(connnectedUser));

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a incident.
     * @param entity the entity.
     */
    private void onUpdate(IncidentEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a incident.
     * @param entity the entity.
     */
    private void onDelete(IncidentEntity entity) {
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
}
