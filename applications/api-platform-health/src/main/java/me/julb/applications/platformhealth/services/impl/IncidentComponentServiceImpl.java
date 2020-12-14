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
import me.julb.applications.platformhealth.repositories.ComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentComponentRepository;
import me.julb.applications.platformhealth.repositories.IncidentRepository;
import me.julb.applications.platformhealth.services.IncidentComponentService;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentUpdateDTO;
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
 * The incident service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class IncidentComponentServiceImpl implements IncidentComponentService {

    /**
     * The incident repository.
     */
    @Autowired
    private IncidentRepository incidentRepository;

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
    public Page<IncidentComponentDTO> findAll(@NotNull @Identifier String incidentId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Try to search within the link.
        ISpecification<IncidentComponentEntity> spec = new SearchSpecification<IncidentComponentEntity>(searchable).and(new TmSpecification<>(tm));
        Page<IncidentComponentEntity> result = incidentComponentRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, IncidentComponentDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentComponentDTO findOne(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the incident component exists
        IncidentComponentEntity result = incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId);
        if (result == null) {
            throw new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId));
        }

        return mappingService.map(result, IncidentComponentDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentComponentDTO create(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the incident component exists
        IncidentComponentEntity existing = incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId);
        if (existing != null) {
            throw new ResourceAlreadyExistsException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId));
        }

        // Add component item.
        IncidentComponentEntity incidentComponentToCreate = mappingService.map(creationDTO, IncidentComponentEntity.class);
        incidentComponentToCreate.setIncident(incident);
        incidentComponentToCreate.setComponent(component);
        this.onPersist(incidentComponentToCreate);

        IncidentComponentEntity result = incidentComponentRepository.save(incidentComponentToCreate);
        return mappingService.map(result, IncidentComponentDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentComponentDTO update(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the incident component exists
        IncidentComponentEntity exising = incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId);
        if (exising == null) {
            throw new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId));
        }

        // Add component item.
        mappingService.map(updateDTO, exising);
        this.onUpdate(exising);

        IncidentComponentEntity result = incidentComponentRepository.save(exising);
        return mappingService.map(result, IncidentComponentDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentComponentDTO patch(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the incident component exists
        IncidentComponentEntity exising = incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId);
        if (exising == null) {
            throw new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId));
        }

        // Add component item.
        mappingService.map(patchDTO, exising);
        this.onUpdate(exising);

        IncidentComponentEntity result = incidentComponentRepository.save(exising);
        return mappingService.map(result, IncidentComponentDTO.class);
    }

    /**
     * /** {@inheritDoc}
     */
    @Override
    public void delete(@NotNull @Identifier String incidentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the incident component exists
        List<IncidentComponentEntity> existings = incidentComponentRepository.findByTmAndIncidentId(tm, incidentId);
        for (IncidentComponentEntity existing : existings) {
            // Delete entity.
            incidentComponentRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the incident exists
        IncidentEntity incident = incidentRepository.findByTmAndId(tm, incidentId);
        if (incident == null) {
            throw new ResourceNotFoundException(IncidentEntity.class, incidentId);
        }

        // Check that the component exists
        ComponentEntity component = componentRepository.findByTmAndId(tm, componentId);
        if (component == null) {
            throw new ResourceNotFoundException(ComponentEntity.class, componentId);
        }

        // Check that the incident component exists
        IncidentComponentEntity result = incidentComponentRepository.findByTmAndIncidentIdAndComponentId(tm, incidentId, componentId);
        if (result == null) {
            throw new ResourceNotFoundException(IncidentComponentEntity.class, Map.<String, String> of("incident", incidentId, "component", componentId));
        }

        // Delete entity.
        incidentComponentRepository.delete(result);

        // Handle deletion.
        this.onDelete(result);

    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an incident component.
     * @param entity the entity.
     */
    private void onPersist(IncidentComponentEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a incident history.
     * @param entity the entity.
     */
    private void onUpdate(IncidentComponentEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a incident component.
     * @param entity the entity.
     */
    private void onDelete(IncidentComponentEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(IncidentComponentEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.INCIDENT_COMPONENT)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
