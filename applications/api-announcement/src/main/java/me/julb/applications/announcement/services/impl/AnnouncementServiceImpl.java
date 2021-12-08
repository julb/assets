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

package me.julb.applications.announcement.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.announcement.entities.AnnouncementEntity;
import me.julb.applications.announcement.entities.mappers.AnnouncementEntityMapper;
import me.julb.applications.announcement.repositories.AnnouncementRepository;
import me.julb.applications.announcement.services.AnnouncementService;
import me.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import me.julb.applications.announcement.services.dto.AnnouncementDTO;
import me.julb.applications.announcement.services.dto.AnnouncementPatchDTO;
import me.julb.applications.announcement.services.dto.AnnouncementUpdateDTO;
import me.julb.applications.announcement.services.exceptions.AnnouncementAlreadyExistsInIntervalException;
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
 * The announcement service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class AnnouncementServiceImpl implements AnnouncementService {

    /**
     * The announcement repository.
     */
    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * The mapper.
     */
    @Autowired
    private AnnouncementEntityMapper mapper;

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
    public Page<AnnouncementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<AnnouncementEntity> spec = new SearchSpecification<AnnouncementEntity>(searchable).and(new TmSpecification<>(tm));
        Page<AnnouncementEntity> result = announcementRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnouncementDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the announcement exists
        AnnouncementEntity result = announcementRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(AnnouncementEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AnnouncementDTO create(@NotNull @Valid AnnouncementCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check if not overlapping another one.
        if (announcementRepository.existsByTmAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(tm, creationDTO.getVisibilityDateTime().getFrom(), creationDTO.getVisibilityDateTime().getTo())) {
            throw new AnnouncementAlreadyExistsInIntervalException(creationDTO.getVisibilityDateTime().getFrom(), creationDTO.getVisibilityDateTime().getTo());
        }

        // Update the entity
        AnnouncementEntity entityToCreate = mapper.map(creationDTO);
        this.onPersist(entityToCreate);

        AnnouncementEntity result = announcementRepository.save(entityToCreate);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AnnouncementDTO update(@NotNull @Identifier String id, @NotNull @Valid AnnouncementUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the announcement exists
        AnnouncementEntity existing = announcementRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AnnouncementEntity.class, id);
        }

        // Check if not overlapping another one.
        if (announcementRepository.existsByTmAndIdNotAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(tm, id, updateDTO.getVisibilityDateTime().getFrom(), updateDTO.getVisibilityDateTime().getTo())) {
            throw new AnnouncementAlreadyExistsInIntervalException(updateDTO.getVisibilityDateTime().getFrom(), updateDTO.getVisibilityDateTime().getTo());
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        AnnouncementEntity result = announcementRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AnnouncementDTO patch(@NotNull @Identifier String id, @NotNull @Valid AnnouncementPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the announcement exists
        AnnouncementEntity existing = announcementRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AnnouncementEntity.class, id);
        }

        // Check if not overlapping another one.
        String from = existing.getVisibilityDateTime().getFrom();
        if (patchDTO.getVisibilityDateTime() != null && patchDTO.getVisibilityDateTime().getFrom() != null) {
            from = patchDTO.getVisibilityDateTime().getFrom();
        }
        String to = existing.getVisibilityDateTime().getTo();
        if (patchDTO.getVisibilityDateTime() != null && patchDTO.getVisibilityDateTime().getTo() != null) {
            to = patchDTO.getVisibilityDateTime().getTo();
        }

        if (announcementRepository.existsByTmAndIdNotAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(tm, id, from, to)) {
            throw new AnnouncementAlreadyExistsInIntervalException(from, to);
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        AnnouncementEntity result = announcementRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the announcement exists
        AnnouncementEntity existing = announcementRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AnnouncementEntity.class, id);
        }

        // Delete entity.
        announcementRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an announcement.
     * @param entity the entity.
     */
    private void onPersist(AnnouncementEntity entity) {
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
     * Method called when updating a announcement.
     * @param entity the entity.
     */
    private void onUpdate(AnnouncementEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a announcement.
     * @param entity the entity.
     */
    private void onDelete(AnnouncementEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(AnnouncementEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.ANNOUNCEMENT)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
