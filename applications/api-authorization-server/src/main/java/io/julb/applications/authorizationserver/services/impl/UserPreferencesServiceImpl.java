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

package io.julb.applications.authorizationserver.services.impl;

import io.julb.applications.authorizationserver.entities.UserEntity;
import io.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import io.julb.applications.authorizationserver.repositories.UserPreferencesRepository;
import io.julb.applications.authorizationserver.repositories.UserRepository;
import io.julb.applications.authorizationserver.services.UserPreferencesService;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesPatchDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesUpdateDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The user preferences service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserPreferencesServiceImpl implements UserPreferencesService {

    /**
     * The item repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

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
    public UserPreferencesDTO findOne(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserPreferencesEntity result = userPreferencesRepository.findByTmAndUser_Id(tm, userId);
        if (result == null) {
            throw new ResourceNotFoundException(UserPreferencesEntity.class, userId);
        }

        return mappingService.map(result, UserPreferencesDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserPreferencesDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserPreferencesCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userPreferencesRepository.existsByTmAndUser_Id(tm, userId)) {
            throw new ResourceAlreadyExistsException(UserPreferencesEntity.class, "user", userId);
        }

        UserPreferencesEntity entityToCreate = mappingService.map(creationDTO, UserPreferencesEntity.class);
        entityToCreate.setUser(user);
        this.onPersist(entityToCreate);

        UserPreferencesEntity result = userPreferencesRepository.save(entityToCreate);
        return mappingService.map(result, UserPreferencesDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserPreferencesDTO update(@NotNull @Identifier String userId, @NotNull @Valid UserPreferencesUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserPreferencesEntity existing = userPreferencesRepository.findByTmAndUser_Id(tm, userId);
        if (existing == null) {
            throw new ResourceNotFoundException(UserPreferencesEntity.class, userId);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserPreferencesEntity result = userPreferencesRepository.save(existing);
        return mappingService.map(result, UserPreferencesDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserPreferencesDTO patch(@NotNull @Identifier String userId, @NotNull @Valid UserPreferencesPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserPreferencesEntity existing = userPreferencesRepository.findByTmAndUser_Id(tm, userId);
        if (existing == null) {
            throw new ResourceNotFoundException(UserPreferencesEntity.class, userId);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserPreferencesEntity result = userPreferencesRepository.save(existing);
        return mappingService.map(result, UserPreferencesDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserPreferencesEntity existing = userPreferencesRepository.findByTmAndUser_Id(tm, userId);
        if (existing == null) {
            throw new ResourceNotFoundException(UserPreferencesEntity.class, userId);
        }

        // Delete entity.
        userPreferencesRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserPreferencesEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private void onUpdate(UserPreferencesEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserPreferencesEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserPreferencesEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_PREFERENCES)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
