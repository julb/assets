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

package me.julb.applications.disclaimer.services.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.disclaimer.entities.AgreementEntity;
import me.julb.applications.disclaimer.entities.DisclaimerEntity;
import me.julb.applications.disclaimer.repositories.AgreementRepository;
import me.julb.applications.disclaimer.repositories.DisclaimerRepository;
import me.julb.applications.disclaimer.services.MyAgreementService;
import me.julb.applications.disclaimer.services.UserAgreementService;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementCreationDTO;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import me.julb.applications.disclaimer.services.exceptions.DisclaimerIsNotActiveException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The connected user agreement service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyAgreementServiceImpl implements MyAgreementService {

    /**
     * The disclaimer repository.
     */
    @Autowired
    private DisclaimerRepository disclaimerRepository;

    /**
     * The agreement repository.
     */
    @Autowired
    private AgreementRepository agreementRepository;

    /**
     * The user agreement service.
     */
    @Autowired
    private UserAgreementService userAgreementService;

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
    private AsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<AgreementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String userId = securityService.getConnectedUserId();
        return userAgreementService.findAll(userId, searchable, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgreementDTO findOne(@NotNull @Identifier String disclaimerId) {
        String userId = securityService.getConnectedUserId();
        return userAgreementService.findOne(userId, disclaimerId);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AgreementDTO create(@NotNull @Identifier String disclaimerId, @NotNull @Valid AgreementCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();
        String userId = securityService.getConnectedUserId();

        // Check that the disclaimer exists
        DisclaimerEntity disclaimer = disclaimerRepository.findByTmAndId(tm, disclaimerId);
        if (disclaimer == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, disclaimerId);
        }

        // Check if not overlapping another one.
        if (agreementRepository.existsByTmAndDisclaimerIdAndUser_Id(tm, disclaimerId, userId)) {
            throw new ResourceAlreadyExistsException(AgreementEntity.class, Map.<String, String> of("disclaimer", disclaimerId, "user", userId));
        }

        // Check if active
        if (!disclaimer.getActive()) {
            throw new DisclaimerIsNotActiveException(disclaimerId);
        }

        // Update the entity
        AgreementEntity entityToCreate = mappingService.map(creationDTO, AgreementEntity.class);
        entityToCreate.setDisclaimer(disclaimer);
        this.onPersist(entityToCreate);

        AgreementEntity result = agreementRepository.save(entityToCreate);
        return mappingService.map(result, AgreementDTO.class);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a disclaimer.
     * @param entity the entity.
     */
    private void onPersist(AgreementEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        entity.setAgreedAt(DateUtility.dateTimeNow());

        // Add author.
        AuthenticatedUserDTO connnectedUser = securityService.getConnectedUserIdentity();
        entity.setUser(new UserRefEntity());
        entity.getUser().setDisplayName(connnectedUser.getDisplayName());
        entity.getUser().setE164Number(connnectedUser.getE164Number());
        entity.getUser().setFirstName(connnectedUser.getFirstName());
        entity.getUser().setId(connnectedUser.getUserId());
        entity.getUser().setLastName(connnectedUser.getLastName());
        entity.getUser().setLocale(connnectedUser.getLocale());
        entity.getUser().setMail(connnectedUser.getMail());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(AgreementEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.AGREEMENT)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
