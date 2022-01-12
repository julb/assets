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

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.webnotification.entities.WebNotificationEntity;
import me.julb.applications.webnotification.entities.mappers.WebNotificationEntityMapper;
import me.julb.applications.webnotification.repositories.WebNotificationRepository;
import me.julb.applications.webnotification.repositories.specifications.WebNotificationByUserIdSpecification;
import me.julb.applications.webnotification.services.WebNotificationService;
import me.julb.applications.webnotification.services.dto.WebNotificationDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.TmSpecification;
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
public class WebNotificationServiceImpl implements WebNotificationService {

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
    public Flux<WebNotificationDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<WebNotificationEntity> spec = new SearchSpecification<WebNotificationEntity>(searchable)
                .and(new TmSpecification<>(tm))
                .and(new WebNotificationByUserIdSpecification(userId));
            return webNotificationRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<WebNotificationDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the web notification exists
            return webNotificationRepository.findByTmAndUserIdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(WebNotificationEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> markAllAsRead(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            
            Flux<WebNotificationEntity> existing = webNotificationRepository.findByTmAndUserId(tm, userId).flatMap(webNotification -> {
                // Update.
                webNotification.setRead(true);

                // Handle deletion.
                return this.onUpdate(webNotification);
            });
            return webNotificationRepository.saveAll(existing).then();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<WebNotificationDTO> markAsRead(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return webNotificationRepository.findByTmAndUserIdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(WebNotificationEntity.class, id)))
                .flatMap(existing -> {
                    // Update flag.
                    existing.setRead(Boolean.TRUE);
                    this.onUpdate(existing);

                    // Return update
                    return webNotificationRepository.save(existing).map(mapper::map);
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> deleteAll(@NotNull @Identifier String userId) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            Flux<WebNotificationEntity> userWebNotifications = webNotificationRepository.findByTmAndUserId(tm, userId)
                    .flatMap(this::onDelete);
            return webNotificationRepository.deleteAll(userWebNotifications);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return webNotificationRepository.findByTmAndUserIdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(WebNotificationEntity.class, id)))
                .flatMap(existing -> {
                    return webNotificationRepository.delete(existing).then(
                        this.onDelete(existing)
                    ).then();
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when updating a web notification.
     * @param entity the entity.
     */
    private Mono<WebNotificationEntity> onUpdate(WebNotificationEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setBusinessCategory(entity.getKind().category());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a web notification.
     * @param entity the entity.
     */
    private Mono<WebNotificationEntity> onDelete(WebNotificationEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
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
