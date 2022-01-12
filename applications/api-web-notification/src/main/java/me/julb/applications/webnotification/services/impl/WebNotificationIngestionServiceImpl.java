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
import me.julb.library.dto.webnotification.WebNotificationMessageDTO;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The web notification service implementation.
 * <br>
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
     * The mapping service.
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

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> ingest(WebNotificationMessageDTO webNotificationMessage) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            Flux<WebNotificationEntity> webNotifications = Flux.fromIterable(webNotificationMessage.getUsers()).flatMap(user -> {
                WebNotificationEntity webNotificationEntity = new WebNotificationEntity();
                webNotificationEntity.setBusinessCategory(webNotificationMessage.getKind().category());
                webNotificationEntity.setExpiryDateTime(webNotificationMessage.getExpiryDateTime());
                webNotificationEntity.setKind(webNotificationMessage.getKind());
                webNotificationEntity.setParameters(webNotificationMessage.getParameters());
                webNotificationEntity.setPriority(webNotificationMessage.getPriority());
                webNotificationEntity.setRead(Boolean.FALSE);
                webNotificationEntity.setUser(userRefMapper.map(user));
                return this.onPersist(tm, webNotificationEntity);
            });

            // Save all notifications.
            return webNotificationRepository.saveAll(webNotifications).then();
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a web notification.
     * @param entity the entity.
     * @param tm the trademark.
     */
    private Mono<WebNotificationEntity> onPersist(String tm, WebNotificationEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setTm(tm);

        return postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<WebNotificationEntity> postResourceEvent(WebNotificationEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.WEB_NOTIFICATION)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
