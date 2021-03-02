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

package me.julb.applications.webnotification.services.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.webnotification.entities.WebNotificationEntity;
import me.julb.applications.webnotification.repositories.WebNotificationRepository;
import me.julb.applications.webnotification.services.WebNotificationIngestionService;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.dto.webnotification.WebNotificationMessageDTO;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The web notification service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class WebNotificationIngestionServiceImpl implements WebNotificationIngestionService {

    /**
     * The web notification repository.
     */
    @Autowired
    private WebNotificationRepository webNotificationRepository;

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

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void ingest(WebNotificationMessageDTO webNotificationMessage) {
        Collection<WebNotificationEntity> webNotifications = new ArrayList<>();

        // Create a notification for all users.
        for (UserRefDTO user : webNotificationMessage.getUsers()) {
            WebNotificationEntity webNotificationEntity = new WebNotificationEntity();
            webNotificationEntity.setBusinessCategory(webNotificationMessage.getKind().category());
            webNotificationEntity.setExpiryDateTime(webNotificationMessage.getExpiryDateTime());
            webNotificationEntity.setKind(webNotificationMessage.getKind());
            webNotificationEntity.setParameters(webNotificationMessage.getParameters());
            webNotificationEntity.setPriority(webNotificationMessage.getPriority());
            webNotificationEntity.setRead(Boolean.FALSE);
            webNotificationEntity.setUser(new UserRefEntity());
            webNotificationEntity.getUser().setDisplayName(user.getDisplayName());
            webNotificationEntity.getUser().setE164Number(user.getE164Number());
            webNotificationEntity.getUser().setFirstName(user.getFirstName());
            webNotificationEntity.getUser().setId(user.getId());
            webNotificationEntity.getUser().setLastName(user.getLastName());
            webNotificationEntity.getUser().setLocale(user.getLocale());
            webNotificationEntity.getUser().setMail(user.getMail());
            this.onPersist(webNotificationEntity);
            webNotifications.add(webNotificationEntity);
        }

        // Save all notifications.
        webNotificationRepository.saveAll(webNotifications);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a web notification.
     * @param entity the entity.
     */
    private void onPersist(WebNotificationEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setTm(TrademarkContextHolder.getTrademark());

        postResourceEvent(entity, ResourceEventType.CREATED);
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
