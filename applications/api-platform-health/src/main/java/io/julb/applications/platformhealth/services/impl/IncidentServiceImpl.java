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

package io.julb.applications.platformhealth.services.impl;

import io.julb.applications.platformhealth.entities.ComponentEntity;
import io.julb.applications.platformhealth.entities.IncidentComponentEntity;
import io.julb.applications.platformhealth.entities.IncidentEntity;
import io.julb.applications.platformhealth.repositories.ComponentRepository;
import io.julb.applications.platformhealth.repositories.IncidentComponentRepository;
import io.julb.applications.platformhealth.repositories.IncidentRepository;
import io.julb.applications.platformhealth.services.IncidentComponentService;
import io.julb.applications.platformhealth.services.IncidentHistoryService;
import io.julb.applications.platformhealth.services.IncidentService;
import io.julb.applications.platformhealth.services.dto.incident.IncidentCreationDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentHistoryCreationDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentPatchDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentStatus;
import io.julb.applications.platformhealth.services.dto.incident.IncidentUpdateDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.dto.security.AuthenticatedUserIdentityDTO;
import io.julb.library.persistence.mongodb.entities.user.UserEntity;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.IdInSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;

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

/**
 * The incident service implementation.
 * <P>
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
    public Page<IncidentDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<IncidentEntity> spec = new SearchSpecification<IncidentEntity>(searchable).and(new TmSpecification<>(tm));
        Page<IncidentEntity> result = incidentRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, IncidentDTO.class);
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
        return mappingService.mapAsPage(result, IncidentDTO.class);
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

        return mappingService.map(result, IncidentDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IncidentDTO create(@NotNull @Valid IncidentCreationDTO creationDTO) {
        // Update the entity
        IncidentEntity entityToCreate = mappingService.map(creationDTO, IncidentEntity.class);

        this.onPersist(entityToCreate);

        // Get result back.
        IncidentEntity result = incidentRepository.save(entityToCreate);

        // Add history item.
        IncidentHistoryCreationDTO dto = new IncidentHistoryCreationDTO();
        dto.setLocalizedMessage(creationDTO.getLocalizedMessage());
        dto.setSendNotification(false);
        dto.setStatus(result.getStatus());
        incidentHistoryService.create(result.getId(), dto);

        return mappingService.map(result, IncidentDTO.class);
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
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        IncidentEntity result = incidentRepository.save(existing);
        return mappingService.map(result, IncidentDTO.class);
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
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        IncidentEntity result = incidentRepository.save(existing);
        return mappingService.map(result, IncidentDTO.class);
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
        AuthenticatedUserIdentityDTO connnectedUser = securityService.getConnectedUserIdentity();
        entity.setUser(new UserEntity());
        entity.getUser().setFirstName(connnectedUser.getFirstName());
        entity.getUser().setId(connnectedUser.getId());
        entity.getUser().setLastName(connnectedUser.getLastName());
        entity.getUser().setMail(connnectedUser.getMail());

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
