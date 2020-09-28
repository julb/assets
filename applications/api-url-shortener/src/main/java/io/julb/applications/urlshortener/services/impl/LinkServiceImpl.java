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

package io.julb.applications.urlshortener.services.impl;

import io.julb.applications.urlshortener.entities.LinkEntity;
import io.julb.applications.urlshortener.repositories.LinkRepository;
import io.julb.applications.urlshortener.services.HostService;
import io.julb.applications.urlshortener.services.LinkService;
import io.julb.applications.urlshortener.services.dto.LinkCreationDTO;
import io.julb.applications.urlshortener.services.dto.LinkDTO;
import io.julb.applications.urlshortener.services.dto.LinkPatchDTO;
import io.julb.applications.urlshortener.services.dto.LinkUpdateDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.dto.security.AuthenticatedUserDTO;
import io.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The link service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class LinkServiceImpl implements LinkService {

    /**
     * The link repository.
     */
    @Autowired
    private LinkRepository linkRepository;

    /**
     * The host service.
     */
    @Autowired
    private HostService hostService;

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
    public Page<LinkDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<LinkEntity> spec = new SearchSpecification<LinkEntity>(searchable).and(new TmSpecification<>(tm));
        Page<LinkEntity> result = linkRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, LinkDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the link exists
        LinkEntity result = linkRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(LinkEntity.class, id);
        }

        return mappingService.map(result, LinkDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LinkDTO create(@NotNull @Valid LinkCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check if host exists.
        if (!hostService.exists(creationDTO.getHost())) {
            throw new ResourceNotFoundException(String.class, creationDTO.getHost());
        }

        // Check if not overlapping another one.
        if (linkRepository.existsByTmAndHostIgnoreCaseAndUriIgnoreCase(tm, creationDTO.getHost(), creationDTO.getUri())) {
            throw new ResourceAlreadyExistsException(LinkEntity.class, Map.<String, String> of("host", creationDTO.getHost(), "uri", creationDTO.getUri()));
        }

        // Update the entity
        LinkEntity entityToCreate = mappingService.map(creationDTO, LinkEntity.class);
        this.onPersist(entityToCreate);

        LinkEntity result = linkRepository.save(entityToCreate);
        return mappingService.map(result, LinkDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkDTO incrementNumberOfHits(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the link exists
        LinkEntity existing = linkRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(LinkEntity.class, id);
        }

        // Update the entity
        existing.incrementNumberOfHits();
        this.onUpdate(existing);

        LinkEntity result = linkRepository.save(existing);
        return mappingService.map(result, LinkDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinkDTO resetNumberOfHits(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the link exists
        LinkEntity existing = linkRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(LinkEntity.class, id);
        }

        // Update the entity
        existing.setHits(0);
        this.onUpdate(existing);

        LinkEntity result = linkRepository.save(existing);
        return mappingService.map(result, LinkDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LinkDTO update(@NotNull @Identifier String id, @NotNull @Valid LinkUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the link exists
        LinkEntity existing = linkRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(LinkEntity.class, id);
        }

        // Check if host exists.
        if (!hostService.exists(updateDTO.getHost())) {
            throw new ResourceNotFoundException(String.class, updateDTO.getHost());
        }

        // Check if not overlapping another one.
        if (linkRepository.existsByTmAndIdNotAndHostIgnoreCaseAndUriIgnoreCase(tm, id, updateDTO.getHost(), updateDTO.getUri())) {
            throw new ResourceAlreadyExistsException(LinkEntity.class, Map.<String, String> of("host", updateDTO.getHost(), "uri", updateDTO.getUri()));
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        LinkEntity result = linkRepository.save(existing);
        return mappingService.map(result, LinkDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LinkDTO patch(@NotNull @Identifier String id, @NotNull @Valid LinkPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the link exists
        LinkEntity existing = linkRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(LinkEntity.class, id);
        }

        // Check if not overlapping another one.
        String host = existing.getHost();
        if (StringUtils.isNotBlank(patchDTO.getHost())) {
            host = patchDTO.getHost();
        }
        String uri = existing.getUri();
        if (StringUtils.isNotBlank(patchDTO.getUri())) {
            uri = patchDTO.getUri();
        }

        // Check if host exists.
        if (!hostService.exists(host)) {
            throw new ResourceNotFoundException(String.class, host);
        }

        // Check if not overlapping another one.
        if (linkRepository.existsByTmAndIdNotAndHostIgnoreCaseAndUriIgnoreCase(tm, id, host, uri)) {
            throw new ResourceAlreadyExistsException(LinkEntity.class, Map.<String, String> of("host", host, "uri", uri));
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        LinkEntity result = linkRepository.save(existing);
        return mappingService.map(result, LinkDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the link exists
        LinkEntity existing = linkRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(LinkEntity.class, id);
        }

        // Delete entity.
        linkRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a link.
     * @param entity the entity.
     */
    private void onPersist(LinkEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setEnabled(true);
        entity.setHits(0);

        // Add author.
        AuthenticatedUserDTO connnectedUser = securityService.getConnectedUserIdentity();
        entity.setUser(new UserRefEntity());
        entity.getUser().setDisplayName(connnectedUser.getDisplayName());
        entity.getUser().setFirstName(connnectedUser.getFirstName());
        entity.getUser().setId(connnectedUser.getUserId());
        entity.getUser().setLastName(connnectedUser.getLastName());
        entity.getUser().setMail(connnectedUser.getMail());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a link.
     * @param entity the entity.
     */
    private void onUpdate(LinkEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a link.
     * @param entity the entity.
     */
    private void onDelete(LinkEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(LinkEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.LINK)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
