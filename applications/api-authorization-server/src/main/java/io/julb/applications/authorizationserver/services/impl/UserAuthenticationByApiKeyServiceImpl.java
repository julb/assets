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
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByApiKeyEntity;
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import io.julb.applications.authorizationserver.repositories.UserAuthenticationByApiKeyRepository;
import io.julb.applications.authorizationserver.repositories.UserRepository;
import io.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import io.julb.applications.authorizationserver.repositories.specifications.UserAuthenticationOfGivenTypeSpecification;
import io.julb.applications.authorizationserver.services.UserAuthenticationByApiKeyService;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyPatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyWithRawKeyDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import io.julb.applications.authorizationserver.services.dto.user.UserDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.utility.constants.Chars;
import io.julb.library.utility.constants.Integers;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.library.utility.validator.constraints.SecureApiKey;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;
import io.julb.springbootstarter.security.services.PasswordEncoderService;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The user authentication service implementation.
 * <P>
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
        return mappingService.mapAsPage(result, UserAuthenticationByApiKeyDTO.class);
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

        return mappingService.map(result, UserAuthenticationByApiKeyDTO.class);
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
        credentials.setUserAuthentication(mappingService.map(result, UserAuthenticationByApiKeyDTO.class));
        credentials.setUser(mappingService.map(result.getUser(), UserDTO.class));

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
        UserAuthenticationByApiKeyEntity entityToCreate = mappingService.map(creationDTO, UserAuthenticationByApiKeyEntity.class);
        entityToCreate.setUser(user);
        this.onPersist(entityToCreate);

        // Generate key.
        String apiKey = buildApiKey(userId, entityToCreate.getId());
        entityToCreate.setSecuredKey(passwordEncoderService.encode(apiKey));

        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.save(entityToCreate);

        // Add key to result
        UserAuthenticationByApiKeyWithRawKeyDTO userAuthenticationByApiKeyWithRawKey = mappingService.map(result, UserAuthenticationByApiKeyWithRawKeyDTO.class);
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
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByApiKeyDTO.class);
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
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByApiKeyEntity result = userAuthenticationByApiKeyRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByApiKeyDTO.class);
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
     * Generates a random reset token.
     * @return a reset token.
     */
    protected String generateRandomKey() {
        //@formatter:off
        return new RandomStringGenerator.Builder()
            .withinRange(new char[] {Chars.ZERO, Chars.NINE}, new char[] {Chars.A_LOWERCASE, Chars.Z_LOWERCASE})
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LOWERCASE_LETTERS)
            .build()
            .generate(Integers.SIXTY_FOUR);
        //@formatter:on
    }

    /**
     * Builds the API key.
     * @param userId the user ID.
     * @param apiKeyId the api key id.
     * @return the full API key.
     */
    protected String buildApiKey(String userId, String apiKeyId) {
        String apiKeyIddUserId = StringUtils.join(apiKeyId, userId);
        String key = generateRandomKey();

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
