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
import io.julb.applications.authorizationserver.repositories.UserRepository;
import io.julb.applications.authorizationserver.services.SignupService;
import io.julb.applications.authorizationserver.services.UserAuthenticationByPasswordService;
import io.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import io.julb.applications.authorizationserver.services.UserMailService;
import io.julb.applications.authorizationserver.services.UserPreferencesService;
import io.julb.applications.authorizationserver.services.UserProfileService;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import io.julb.applications.authorizationserver.services.dto.signup.AbstractSignupCreationDTO;
import io.julb.applications.authorizationserver.services.dto.signup.SignupWithInviteDTO;
import io.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import io.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import io.julb.applications.authorizationserver.services.dto.user.UserDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.dto.security.UserRole;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;

import java.util.HashSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The sign-up service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SignupServiceImpl implements SignupService {

    /**
     * The user repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user mail service.
     */
    @Autowired
    private UserMailService userMailService;

    /**
     * The user profile service.
     */
    @Autowired
    private UserProfileService userProfileService;

    /**
     * The user preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;

    /**
     * The user authentication by password service.
     */
    @Autowired
    private UserAuthenticationByPasswordService userAuthenticationByPasswordService;

    /**
     * The user authentication by pincode service.
     */
    @Autowired
    private UserAuthenticationByPincodeService userAuthenticationByPincodeService;

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
    public UserDTO signup(@NotNull @Valid SignupWithInviteDTO signupWithInvite) {
        UserEntity result = signupUser(signupWithInvite);

        // Return user
        return mappingService.map(result, UserDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserDTO signup(@NotNull @Valid SignupWithPasswordCreationDTO signupWithPassword) {
        UserEntity result = signupUser(signupWithPassword);

        // Save password.
        UserAuthenticationByPasswordCreationDTO dto = new UserAuthenticationByPasswordCreationDTO();
        dto.setPassword(signupWithPassword.getPassword());
        userAuthenticationByPasswordService.create(result.getId(), dto);

        // Return user.
        return mappingService.map(result, UserDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserDTO signup(@NotNull @Valid SignupWithPincodeCreationDTO signupWithPincode) {
        UserEntity result = signupUser(signupWithPincode);

        // Save pincode
        UserAuthenticationByPincodeCreationDTO dto = new UserAuthenticationByPincodeCreationDTO();
        dto.setPincode(signupWithPincode.getPincode());
        userAuthenticationByPincodeService.create(result.getId(), dto);

        // Return user.
        return mappingService.map(result, UserDTO.class);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Sign-ups the user using the sign-up DTO.
     * @param signup the signup DTO.
     * @return the created user.
     */
    protected UserEntity signupUser(AbstractSignupCreationDTO signup) {
        // Check if user exists or not
        if (userMailService.existsByMail(signup.getMail())) {
            throw new ResourceAlreadyExistsException(UserEntity.class, "mail", signup.getMail());
        }

        // Create the user entity
        UserEntity entityToCreate = mappingService.map(signup, UserEntity.class);
        this.onPersist(entityToCreate);
        UserEntity result = userRepository.save(entityToCreate);

        // Create mail settings.
        UserMailCreationDTO creationDTO = new UserMailCreationDTO();
        creationDTO.setMail(signup.getMail());
        creationDTO.setPrimary(true);
        UserMailDTO mailCreated = userMailService.create(result.getId(), creationDTO);
        userMailService.triggerMailVerify(result.getId(), mailCreated.getId());

        // Create profile settings
        userProfileService.create(result.getId(), signup.getProfile());

        // Create preferences settings
        userPreferencesService.create(result.getId(), new UserPreferencesCreationDTO());

        // Return result.
        return result;
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an user.
     * @param entity the entity.
     */
    private void onPersist(UserEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setAccountNonLocked(false);
        entity.setEnabled(true);
        entity.setRoles(new HashSet<UserRole>());
        entity.getRoles().add(UserRole.USER);

        // Post event
        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
