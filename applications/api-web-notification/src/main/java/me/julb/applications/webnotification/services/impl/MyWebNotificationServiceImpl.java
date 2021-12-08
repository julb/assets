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

package me.julb.applications.webnotification.services.impl;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.webnotification.entities.WebNotificationEntity;
import me.julb.applications.webnotification.entities.mappers.WebNotificationEntityMapper;
import me.julb.applications.webnotification.repositories.WebNotificationRepository;
import me.julb.applications.webnotification.services.MyWebNotificationService;
import me.julb.applications.webnotification.services.dto.WebNotificationDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;

/**
 * The web notification service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyWebNotificationServiceImpl implements MyWebNotificationService {

    /**
     * The web notification repository.
     */
    @Autowired
    private WebNotificationRepository webNotificationRepository;

    /**
     * The mapper.
     */
    @Autowired
    private WebNotificationEntityMapper mapper;

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
    public Page<WebNotificationDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<WebNotificationEntity> spec = new SearchSpecification<WebNotificationEntity>(searchable).and(new TmSpecification<>(tm));
        Page<WebNotificationEntity> result = webNotificationRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebNotificationDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get user ID.
        String connectedUserId = securityService.getConnectedUserId();

        // Check that the web notification exists
        WebNotificationEntity result = webNotificationRepository.findByTmAndUserIdAndId(tm, connectedUserId, id);
        if (result == null) {
            throw new ResourceNotFoundException(WebNotificationEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void markAllAsRead() {
        String tm = TrademarkContextHolder.getTrademark();

        // Get user ID.
        String connectedUserId = securityService.getConnectedUserId();

        // Check that the web notification exists
        List<WebNotificationEntity> existing = webNotificationRepository.findByTmAndUserId(tm, connectedUserId);
        for (WebNotificationEntity webNotification : existing) {
            // Update.
            webNotification.setRead(true);

            // Handle deletion.
            this.onUpdate(webNotification);
        }
        webNotificationRepository.saveAll(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public WebNotificationDTO markAsRead(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get user ID.
        String connectedUserId = securityService.getConnectedUserId();

        // Check that the web notification exists
        WebNotificationEntity existing = webNotificationRepository.findByTmAndUserIdAndId(tm, connectedUserId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(WebNotificationEntity.class, id);
        }

        // Update flag.
        existing.setRead(Boolean.TRUE);
        this.onUpdate(existing);

        // Return update
        WebNotificationEntity result = webNotificationRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteAll() {
        String tm = TrademarkContextHolder.getTrademark();

        // Get user ID.
        String connectedUserId = securityService.getConnectedUserId();

        // Check that the web notification exists
        List<WebNotificationEntity> existing = webNotificationRepository.findByTmAndUserId(tm, connectedUserId);
        for (WebNotificationEntity webNotification : existing) {
            // Handle deletion.
            this.onDelete(webNotification);
        }
        webNotificationRepository.deleteAll(existing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get user ID.
        String connectedUserId = securityService.getConnectedUserId();

        // Check that the web notification exists
        WebNotificationEntity existing = webNotificationRepository.findByTmAndUserIdAndId(tm, connectedUserId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(WebNotificationEntity.class, id);
        }

        // Delete entity.
        webNotificationRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when updating a web notification.
     * @param entity the entity.
     */
    private void onUpdate(WebNotificationEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setBusinessCategory(entity.getKind().category());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a web notification.
     * @param entity the entity.
     */
    private void onDelete(WebNotificationEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(WebNotificationEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.WEB_NOTIFICATION)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
