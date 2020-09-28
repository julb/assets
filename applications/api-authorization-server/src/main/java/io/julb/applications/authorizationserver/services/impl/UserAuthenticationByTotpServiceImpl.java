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
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import io.julb.applications.authorizationserver.repositories.UserAuthenticationByPasswordRepository;
import io.julb.applications.authorizationserver.repositories.UserAuthenticationByPincodeRepository;
import io.julb.applications.authorizationserver.repositories.UserAuthenticationByTotpRepository;
import io.julb.applications.authorizationserver.repositories.UserRepository;
import io.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import io.julb.applications.authorizationserver.repositories.specifications.UserAuthenticationOfGivenTypeSpecification;
import io.julb.applications.authorizationserver.services.UserAuthenticationByTotpService;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpPatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpWithRawSecretDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import io.julb.applications.authorizationserver.services.dto.user.UserDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.utility.constants.Integers;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.exceptions.ResourceStillReferencedException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.otp.TotpUtility;
import io.julb.library.utility.validator.constraints.Identifier;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
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
public class UserAuthenticationByTotpServiceImpl implements UserAuthenticationByTotpService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user authentication repository.
     */
    @Autowired
    private UserAuthenticationByPasswordRepository userAuthenticationByPasswordRepository;

    /**
     * The user authentication repository.
     */
    @Autowired
    private UserAuthenticationByPincodeRepository userAuthenticationByPincodeRepository;

    /**
     * The user authentication by api key repository.
     */
    @Autowired
    private UserAuthenticationByTotpRepository userAuthenticationByTotpRepository;

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
    public Page<UserAuthenticationByTotpDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        ISpecification<UserAuthenticationByTotpEntity> spec =
            new SearchSpecification<UserAuthenticationByTotpEntity>(searchable).and(new UserAuthenticationOfGivenTypeSpecification<>(UserAuthenticationType.TOTP)).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
        Page<UserAuthenticationByTotpEntity> result = userAuthenticationByTotpRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, UserAuthenticationByTotpDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationByTotpDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByTotpEntity result = userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id);
        }

        return mappingService.map(result, UserAuthenticationByTotpDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationCredentialsDTO findOneCredentials(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByTotpEntity result = userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id);
        if (result == null) {
            throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id);
        }

        // Generate valid OTP
        Iterable<String> validTotps = TotpUtility.generateValidTotps(result.getSecret(), Integers.ONE);
        List<String> encodedValidTotps = new ArrayList<>();
        for (String validTotp : validTotps) {
            encodedValidTotps.add(passwordEncoderService.encode(validTotp));
        }

        // Get credentials
        UserAuthenticationCredentialsDTO credentials = new UserAuthenticationCredentialsDTO();
        credentials.setCredentials(encodedValidTotps.toArray(new String[0]));
        credentials.setCredentialsNonExpired(true);
        credentials.setUserAuthentication(mappingService.map(result, UserAuthenticationByTotpDTO.class));
        credentials.setUser(mappingService.map(result.getUser(), UserDTO.class));

        return credentials;
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByTotpWithRawSecretDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByTotpCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userAuthenticationByTotpRepository.existsByTmAndUser_IdAndTypeAndNameIgnoreCase(tm, userId, UserAuthenticationType.TOTP, creationDTO.getName())) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByTotpEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.TOTP.toString(), "name", creationDTO.getName()));
        }

        // Generate random reset.
        String secret = TotpUtility.generateRandomTotpSecret();

        UserAuthenticationByTotpEntity entityToCreate = mappingService.map(creationDTO, UserAuthenticationByTotpEntity.class);
        entityToCreate.setUser(user);
        entityToCreate.setSecret(secret);

        this.onPersist(entityToCreate);

        UserAuthenticationByTotpEntity result = userAuthenticationByTotpRepository.save(entityToCreate);

        // Return result
        UserAuthenticationByTotpWithRawSecretDTO userAuthenticationByTotpWithRawSecret = mappingService.map(result, UserAuthenticationByTotpWithRawSecretDTO.class);
        userAuthenticationByTotpWithRawSecret.setRawSecret(secret);
        userAuthenticationByTotpWithRawSecret.setQrCodeUri(TotpUtility.generateQrcodeUri(user.getMail(), tm, secret));
        return userAuthenticationByTotpWithRawSecret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByTotpDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByTotpEntity existing = userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id);
        }

        // Check that the item exists
        if (userAuthenticationByTotpRepository.existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(tm, userId, UserAuthenticationType.TOTP, id, updateDTO.getName())) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByTotpEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.TOTP.toString(), "name", updateDTO.getName()));
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByTotpEntity result = userAuthenticationByTotpRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByTotpDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserAuthenticationByTotpDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserAuthenticationByTotpEntity existing = userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id);
        }

        // Check that the item exists
        if (StringUtils.isNotBlank(patchDTO.getName()) && userAuthenticationByTotpRepository.existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(tm, userId, UserAuthenticationType.TOTP, id, patchDTO.getName())) {
            throw new ResourceAlreadyExistsException(UserAuthenticationByTotpEntity.class, Map.<String, String> of("user", userId, "type", UserAuthenticationType.TOTP.toString(), "name", patchDTO.getName()));
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserAuthenticationByTotpEntity result = userAuthenticationByTotpRepository.save(existing);
        return mappingService.map(result, UserAuthenticationByTotpDTO.class);
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
        UserAuthenticationByTotpEntity existing = userAuthenticationByTotpRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, UserAuthenticationType.TOTP, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserAuthenticationByTotpEntity.class, id);
        }

        // If this is the last one, check MFA is not enabled
        if (userAuthenticationByTotpRepository.countByTmAndUser_IdAndType(tm, userId, UserAuthenticationType.TOTP) == Integers.ONE) {
            if (userAuthenticationByPasswordRepository.existsByTmAndUser_IdAndTypeAndMfaEnabledIsTrue(tm, userId, UserAuthenticationType.PASSWORD)) {
                throw new ResourceStillReferencedException(UserAuthenticationByTotpEntity.class, id, UserAuthenticationByPasswordEntity.class);
            }

            if (userAuthenticationByPincodeRepository.existsByTmAndUser_IdAndTypeAndMfaEnabledIsTrue(tm, userId, UserAuthenticationType.PINCODE)) {
                throw new ResourceStillReferencedException(UserAuthenticationByTotpEntity.class, id, UserAuthenticationByPincodeEntity.class);
            }

        }

        // Delete entity.
        userAuthenticationByTotpRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserAuthenticationByTotpEntity entity) {
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
    private void onUpdate(UserAuthenticationByTotpEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserAuthenticationByTotpEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserAuthenticationByTotpEntity entity, ResourceEventType resourceEventType) {
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
