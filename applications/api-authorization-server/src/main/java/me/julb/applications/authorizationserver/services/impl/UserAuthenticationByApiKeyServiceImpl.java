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

package me.julb.applications.authorizationserver.services.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByApiKeyEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.applications.authorizationserver.entities.authentication.mappers.UserAuthenticationEntityMapper;
import me.julb.applications.authorizationserver.entities.mappers.UserEntityMapper;
import me.julb.applications.authorizationserver.repositories.UserAuthenticationByApiKeyRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.repositories.specifications.UserAuthenticationOfGivenTypeSpecification;
import me.julb.applications.authorizationserver.services.UserAuthenticationByApiKeyService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyWithRawKeyDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.random.RandomUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.SecureApiKey;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The user authentication service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAuthenticationByApiKeyServiceImpl implements UserAuthenticationByApiKeyService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user authentication by api key repository.
     */
    @Autowired
    private UserAuthenticationByApiKeyRepository userAuthenticationByApiKeyRepository;

    /**
     * The mapper.
     */
    @Autowired
    private UserAuthenticationEntityMapper mapper;

    /**
     * The user mapper.
     */
    @Autowired
    private UserEntityMapper userMapper;

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

    /**
     * The password encoder.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserAuthenticationByApiKeyDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        ISpecification<UserAuthenticationByApiKeyEntity> spec = new SearchSpecification<UserAuthenticationByApiKeyEntity>(searchable).and(new UserAuthenticationOfGivenTypeSpecification<>(UserAuthenticationType.API_KEY)).and(new TmSpecification<>(tm))
            .and(new ObjectBelongsToUserIdSpecification<>(userId));
        Page<UserAuthenticationByApiKeyEntity> result = userAuthenticationByApiKeyRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByApiKeyDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.API_KEY, id);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByApiKeyEntity.class, id);
        }

        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationCredentialsDTO findOneCredentials(@NotNull @NotBlank @SecureApiKey String apiKey) {
        String tm = TrademarkContextHolder.getTrademark();

        // Extract user ID.
        String userId = getUserIdFromApiKey(apiKey);

        // Extract api key id.
        String apiKeyId = getApiKeyIdFromApiKey(apiKey);

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.API_KEY, apiKeyId);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByApiKeyEntity.class, apiKeyId);
        }

        // Get credentials
        UserAuthenticationCredentialsDTO credentials = new UserAuthenticationCredentialsDTO();
        credentials.setUniqueCredentials(result.getSecuredKey());
        credentials.setCredentialsNonExpired(true);
        credentials.setUserAuthentication(mapper.map(result));
        credentials.setUser(userMapper.map(result.getUser()));

        return credentials;
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByApiKeyWithRawKeyDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByApiKeyCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userAuthenticationByApiKeyRepository.existsByTmAndUser_IdAndTypeAndNameIgnoreCase(tm, userId, UserAuthenticationType.API_KEY, creationDTO.getName())) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByApiKeyEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.API_KEY.toString(), "name", creationDTO.getName()));
        }

        // Prepare a new user authentication entry.
        UserAuthenticationByApiKeyEntity entityToCreate = mapper.map(creationDTO);
        entityToCreate.setUser(user);
        this.onPersist(entityToCreate);

        // Generate key.
        String apiKey = buildApiKey(userId, entityToCreate.getId());
        entityToCreate.setSecuredKey(passwordEncoderService.encode(apiKey));

        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.save(entityToCreate);

        // Add key to result
        UserAuthenticationByApiKeyWithRawKeyDTO userAuthenticationByApiKeyWithRawKey = mapper.mapWithRawKey(result);
        userAuthenticationByApiKeyWithRawKey.setRawKey(apiKey);
        return userAuthenticationByApiKeyWithRawKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByApiKeyDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByApiKeyUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByApiKeyEntity existing = userAuthenticationByApiKeyRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.API_KEY, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByApiKeyEntity.class, id);
        }

        // Check that the item exists
        if (userAuthenticationByApiKeyRepository.existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(tm, userId, UserAuthenticationType.API_KEY, id, updateDTO.getName())) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByApiKeyEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.API_KEY.toString(), "name", updateDTO.getName()));
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByApiKeyDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByApiKeyPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByApiKeyEntity existing = userAuthenticationByApiKeyRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.API_KEY, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByApiKeyEntity.class, id);
        }

        // Check that the item exists
        if (StringUtils.isNotBlank(patchDTO.getName()) && userAuthenticationByApiKeyRepository.existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(tm, userId, UserAuthenticationType.API_KEY, id, patchDTO.getName())) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByApiKeyEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.API_KEY.toString(), "name", patchDTO.getName()));
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByApiKeyEntity existing = userAuthenticationByApiKeyRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.API_KEY, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByApiKeyEntity.class, id);
        }

        // Delete entity.
        userAuthenticationByApiKeyRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Builds the API key.
     * @param userId the user ID.
     * @param apiKeyId the api key id.
     * @return the full API key.
     */
    protected String buildApiKey(String userId, String apiKeyId) {
        String apiKeyIddUserId = StringUtils.join(apiKeyId, userId);
        String key = RandomUtility.generateAlphaNumericToken(Integers.SIXTY_FOUR).toLowerCase();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < Integers.SIXTY_FOUR; i++) {
            sb.append(apiKeyIddUserId.charAt(i));
            sb.append(key.charAt(i));
        }

        return sb.toString();
    }

    /**
     * Gets the api key ID.
     * @param apiKey the API key.
     * @return the api key ID.
     */
    protected String getApiKeyIdFromApiKey(String apiKey) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < Integers.SIXTY_FOUR; i += 2) {
            sb.append(apiKey.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Gets the user ID.
     * @param apiKey the API key.
     * @return the user ID.
     */
    protected String getUserIdFromApiKey(String apiKey) {
        StringBuffer sb = new StringBuffer();
        for (int i = Integers.SIXTY_FOUR; i < Integers.ONE_HUNDRED_TWENTY_EIGHT; i += 2) {
            sb.append(apiKey.charAt(i));
        }
        return sb.toString();
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserAuthenticationByApiKeyEntity entity) {
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
    private void onUpdate(UserAuthenticationByApiKeyEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserAuthenticationByApiKeyEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserAuthenticationByApiKeyEntity entity, ResourceEventType resourceEventType) {
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
