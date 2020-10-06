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

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserRepository;
import me.julb.applications.authorizationserver.repositories.specifications.ObjectBelongsToUserIdSpecification;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailPatchDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailVerifyDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.applications.authorizationserver.services.exceptions.InvalidMailVerifyTokenException;
import me.julb.applications.authorizationserver.services.exceptions.MailVerifyTokenExpiredException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
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
 * The user mail service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserMailServiceImpl implements UserMailService {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMailServiceImpl.class);

    /**
     * The item repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The item repository.
     */
    @Autowired
    private UserMailRepository userMailRepository;

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
    public Page<UserMailDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        ISpecification<UserMailEntity> spec = new SearchSpecification<UserMailEntity>(searchable).and(new TmSpecification<>(tm)).and(new ObjectBelongsToUserIdSpecification<>(userId));
        Page<UserMailEntity> result = userMailRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMailDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMailEntity result = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (result == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByMail(@NotNull @NotBlank @Email String mail) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        return userMailRepository.existsByTmAndMailIgnoreCase(tm, mail);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMailDTO findByMail(@NotNull @NotBlank @Email String mail) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserMailEntity result = userMailRepository.findByTmAndMailIgnoreCase(tm, mail);
        if (result == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, "mail", mail);
        }

        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMailDTO findByMailVerified(@NotNull @NotBlank @Email String mail) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserMailEntity result = userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail);
        if (result == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, "mail", mail);
        }

        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO findUserByMailVerified(@NotNull @NotBlank @Email String mail) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        UserMailEntity result = userMailRepository.findByTmAndMailIgnoreCaseAndVerifiedIsTrue(tm, mail);
        if (result == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, "mail", mail);
        }

        return mappingService.map(result.getUser(), UserDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMailDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserMailCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        if (userMailRepository.existsByTmAndMailIgnoreCase(tm, creationDTO.getMail())) {
            throw new ResourceAlreadyExistsException(UserMailEntity.class, "mail", creationDTO.getMail());
        }

        UserMailEntity entityToCreate = mappingService.map(creationDTO, UserMailEntity.class);
        entityToCreate.setUser(user);
        entityToCreate.setVerified(false);
        this.onPersist(entityToCreate);

        UserMailEntity result = userMailRepository.save(entityToCreate);
        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMailDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMailEntity existing = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        UserMailEntity result = userMailRepository.save(existing);
        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMailDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMailEntity existing = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        UserMailEntity result = userMailRepository.save(existing);
        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMailDTO triggerMailVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMailEntity existing = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        if (!existing.getVerified()) {
            // Trigger verification.
            String verifyToken = generateRandomVerifyToken();

            // Enable the reset.
            existing.setUser(user);
            existing.setSecuredMailVerifyToken(passwordEncoderService.encode(verifyToken));
            existing.setMailVerifyTokenExpiryDateTime(DateUtility.dateTimePlus(Integers.TWO, ChronoUnit.HOURS));
            this.onUpdate(existing);

            // FIXME Send notification => PLACE HOLDER TO SEND TOKEN
            LOGGER.info("*** SIMULATE SEND NOTIF: {}, {}", existing.getId(), verifyToken);
        }

        UserMailEntity result = userMailRepository.save(existing);
        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMailDTO updateVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailVerifyDTO verifyDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMailEntity existing = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        // If email is verified, return the result.
        if (!existing.getVerified()) {
            // Check token.
            if (StringUtils.isBlank(existing.getSecuredMailVerifyToken()) || !passwordEncoderService.matches(verifyDTO.getVerifyToken(), existing.getSecuredMailVerifyToken())) {
                throw new InvalidMailVerifyTokenException();
            }

            // Check if token is not expired
            if (DateUtility.dateTimeNow().compareTo(existing.getMailVerifyTokenExpiryDateTime()) > 0) {
                throw new MailVerifyTokenExpiredException();
            }

            // Update verified status.
            existing.setMailVerifyTokenExpiryDateTime(null);
            existing.setVerified(true);
            existing.setSecuredMailVerifyToken(null);

            // If mail is primary, unlock user account.
            if (existing.getPrimary() && !existing.getUser().getAccountNonLocked()) {
                existing.getUser().setAccountNonLocked(true);
                userRepository.save(existing.getUser());
            }
        }

        UserMailEntity result = userMailRepository.save(existing);
        return mappingService.map(result, UserMailDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMailDTO updateVerifyWithoutToken(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the user exists
        UserEntity user = userRepository.findByTmAndId(tm, userId);
        if (user == null) {
            throw new ResourceNotFoundException(UserEntity.class, userId);
        }

        // Check that the item exists
        UserMailEntity existing = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        // If email is verified, return the result.
        if (!existing.getVerified()) {
            // Update verified status.
            existing.setMailVerifyTokenExpiryDateTime(null);
            existing.setVerified(true);
            existing.setSecuredMailVerifyToken(null);

            // If mail is primary, unlock user account.
            if (existing.getPrimary() && !existing.getUser().getAccountNonLocked()) {
                existing.getUser().setAccountNonLocked(true);
                userRepository.save(existing.getUser());
            }
        }

        UserMailEntity result = userMailRepository.save(existing);
        return mappingService.map(result, UserMailDTO.class);
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
        UserMailEntity existing = userMailRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(UserMailEntity.class, id);
        }

        // Delete entity.
        userMailRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Generates a random verify token.
     * @return a verify token.
     */
    protected String generateRandomVerifyToken() {
        // Generate an URI.
        //@formatter:off
            return new RandomStringGenerator.Builder()
                .withinRange(new char[] {Chars.ZERO, Chars.NINE}, new char[] {Chars.A_LOWERCASE, Chars.Z_LOWERCASE}, new char[] {Chars.A_UPPERCASE, Chars.Z_UPPERCASE})
                .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LETTERS)
                .build()
                .generate(Integers.ONE_HUNDRED_TWENTY_EIGHT);
            //@formatter:on
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(UserMailEntity entity) {
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
    private void onUpdate(UserMailEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(UserMailEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(UserMailEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.USER_MAIL)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
