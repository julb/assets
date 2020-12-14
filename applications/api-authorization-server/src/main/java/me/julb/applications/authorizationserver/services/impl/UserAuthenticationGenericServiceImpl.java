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

package me.julb.applications.authorizationserver.services.impl;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.authentication.AbstractUserAuthenticationEntity;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.services.UserAuthenticationGenericService;
import me.julb.applications.authorizationserver.services.dto.authentication.AbstractUserAuthenticationDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The user authentication generic service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAuthenticationGenericServiceImpl implements UserAuthenticationGenericService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user authentication by api key repository.
     */
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;

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

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractUserAuthenticationDTO updateSuccessfulUse(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        AbstractUserAuthenticationEntity existing = userAuthenticationRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AbstractUserAuthenticationEntity.class, id);
        }

        // Update the entity
        existing.setLastSuccessfulUseDateTime(DateUtility.dateTimeNow());
        existing.setFailedAttempts(0);
        this.onUpdate(existing);

        AbstractUserAuthenticationEntity result = userAuthenticationRepository.save(existing);
        return mappingService.map(result, AbstractUserAuthenticationDTO.class);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private void onUpdate(AbstractUserAuthenticationEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(AbstractUserAuthenticationEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_AUTHENTICATION)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
