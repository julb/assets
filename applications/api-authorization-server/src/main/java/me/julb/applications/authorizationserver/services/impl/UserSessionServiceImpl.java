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

import java.time.temporal.ChronoUnit;
import java.util.List;

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

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.UserSessionRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.UserSessionToAccessTokenMapper;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFirstCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFromIdTokenCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenWithIdTokenDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionCredentialsDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionPatchDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionWithRawIdTokenDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.exceptions.UnauthorizedException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.SecureIdToken;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The user session service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserSessionServiceImpl implements UserSessionService {

    /**
     * The item repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserSessionRepository userSessionRepository;

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

    /**
     * The user session to access token mapper.
     */
    @Autowired
    private UserSessionToAccessTokenMapper userSessionToAccessTokenMapper;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserSessionDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        ISpecification<UserSessionEntity> spec = new SearchSpecification<UserSessionEntity>(searchable).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
        Page<UserSessionEntity> result = userSessionRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, UserSessionDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSessionDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserSessionEntity result = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (result == null) {
            throw new ResourceNotFoundException(UserSessionEntity.class, id);
        }

        return mappingService.map(result, UserSessionDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSessionCredentialsDTO findOneCredentials(@NotNull @NotBlank @SecureIdToken String idToken) {
        String tm = TrademarkContextHolder.getTrademark();

        // Extract user ID.
        String userId = getUserIdFromRawIdToken(idToken);

        // Extract user session id.
        String userSessionId = getUserSessionIdFromRawIdToken(idToken);

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserSessionEntity result = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, userSessionId);
        if (result == null) {
            throw new ResourceNotFoundException(UserSessionEntity.class, userSessionId);
        }

        // Get credentials
        UserSessionCredentialsDTO credentials = new UserSessionCredentialsDTO();
        credentials.setCredentials(result.getSecuredIdToken());
        credentials.setCredentialsNonExpired(true);
        credentials.setUserSession(mappingService.map(result, UserSessionDTO.class));
        credentials.setUser(mappingService.map(result.getUser(), UserDTO.class));

        return credentials;
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserSessionWithRawIdTokenDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserSessionCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        UserSessionEntity entityToCreate = mappingService.map(creationDTO, UserSessionEntity.class);
        entityToCreate.setUser(user);
        entityToCreate.setExpiryDateTime(DateUtility.dateTimePlus(creationDTO.getDurationInSeconds(), ChronoUnit.SECONDS));
        this.onPersist(entityToCreate);

        // Generate key.
        String rawIdToken = buildIdToken(userId, entityToCreate.getId());
        entityToCreate.setSecuredIdToken(passwordEncoderService.encode(rawIdToken));
        UserSessionEntity result = userSessionRepository.save(entityToCreate);

        // Add raw id token to the session.
        UserSessionWithRawIdTokenDTO map = mappingService.map(result, UserSessionWithRawIdTokenDTO.class);
        map.setRawIdToken(rawIdToken);

        // Return the result.
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSessionAccessTokenWithIdTokenDTO createAccessTokenFromIdToken(@NotNull @Valid UserSessionAccessTokenFromIdTokenCreationDTO accessTokenCreation) {
        String tm = TrademarkContextHolder.getTrademark();

        // Extract user ID.
        String userId = getUserIdFromRawIdToken(accessTokenCreation.getRawIdToken());

        // Extract user session id.
        String userSessionId = getUserSessionIdFromRawIdToken(accessTokenCreation.getRawIdToken());

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new UnauthorizedException();
        }

        // Check that the item exists
        UserSessionEntity existing = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, userSessionId);
        if (existing == null) {
            throw new UnauthorizedException();
        }

        // Check if session matches.
        if (!passwordEncoderService.matches(accessTokenCreation.getRawIdToken(), existing.getSecuredIdToken())) {
            throw new UnauthorizedException();
        }

        if (DateUtility.dateTimeBeforeNow(existing.getExpiryDateTime())) {
            throw new UnauthorizedException();
        }

        // Update
        mappingService.map(accessTokenCreation, existing);
        this.onUpdate(existing);
        UserSessionEntity result = userSessionRepository.save(existing);

        // Generate JWT.
        UserSessionAccessTokenDTO accessToken = userSessionToAccessTokenMapper.map(result);

        // Return result.
        UserSessionAccessTokenWithIdTokenDTO sessionToken = new UserSessionAccessTokenWithIdTokenDTO();
        sessionToken.setAccessToken(accessToken.getAccessToken());
        sessionToken.setExpiresAt(accessToken.getExpiresAt());
        sessionToken.setExpiresIn(accessToken.getExpiresIn());
        sessionToken.setType(accessToken.getType());
        sessionToken.setIdToken(accessTokenCreation.getRawIdToken());
        sessionToken.setIdTokenExpiresAt(result.getExpiryDateTime());
        sessionToken.setIdTokenExpiresIn(DateUtility.secondsUntil(result.getExpiryDateTime()));
        return sessionToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSessionAccessTokenDTO createAccessTokenFirst(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionAccessTokenFirstCreationDTO accessTokenCreation) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new UnauthorizedException();
        }

        // Check that the item exists
        UserSessionEntity existing = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new UnauthorizedException();
        }

        if (DateUtility.dateTimeBeforeNow(existing.getExpiryDateTime())) {
            throw new UnauthorizedException();
        }

        // Update
        mappingService.map(accessTokenCreation, existing);
        this.onUpdate(existing);
        UserSessionEntity result = userSessionRepository.save(existing);

        // Generate JWT.
        return userSessionToAccessTokenMapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserSessionDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserSessionEntity existing = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserSessionEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserSessionEntity result = userSessionRepository.save(existing);
        return mappingService.map(result, UserSessionDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserSessionDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserSessionEntity existing = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserSessionEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserSessionEntity result = userSessionRepository.save(existing);
        return mappingService.map(result, UserSessionDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserSessionDTO markMfaAsVerified(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserSessionEntity existing = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserSessionEntity.class, id);
        }

        // Update the entity
        existing.setMfaVerified(Boolean.TRUE);
        this.onUpdate(existing);

        UserSessionEntity result = userSessionRepository.save(existing);
        return mappingService.map(result, UserSessionDTO.class);
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
        List<UserSessionEntity> existings = userSessionRepository.findByTmAndUser_Id(tm, userId);
        for (UserSessionEntity existing : existings) {
            // Delete entity.
            userSessionRepository.delete(existing);

            // Handle deletion.
            this.onDelete(existing);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserSessionEntity existing = userSessionRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserSessionEntity.class, id);
        }

        // Delete entity.
        userSessionRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Generates a random ID token.
     * @return the random ID token.
     */
    protected String generateRandomIdToken() {
        // Generate an URI.
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
     * @param userSessionId the api key id.
     * @return the full API key.
     */
    protected String buildIdToken(String userId, String userSessionId) {
        String userSessionIdUserId = StringUtils.join(userSessionId, userId);
        String randomIdToken = generateRandomIdToken();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < Integers.SIXTY_FOUR; i++) {
            sb.append(userSessionIdUserId.charAt(i));
            sb.append(randomIdToken.charAt(i));
        }

        return sb.toString();
    }

    /**
     * Gets the user session ID from the raw ID token.
     * @param rawIdToken the raw ID token.
     * @return the user session ID.
     */
    protected String getUserSessionIdFromRawIdToken(String rawIdToken) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < Integers.SIXTY_FOUR; i += 2) {
            sb.append(rawIdToken.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Gets the user ID from the raw ID token.
     * @param rawIdToken the raw ID token.
     * @return the user ID.
     */
    protected String getUserIdFromRawIdToken(String rawIdToken) {
        StringBuffer sb = new StringBuffer();
        for (int i = Integers.SIXTY_FOUR; i < Integers.ONE_HUNDRED_TWENTY_EIGHT; i += 2) {
            sb.append(rawIdToken.charAt(i));
        }
        return sb.toString();
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserSessionEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        entity.setLastUseDateTime(DateUtility.dateTimeNow());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private void onUpdate(UserSessionEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserSessionEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserSessionEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_SESSION)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
