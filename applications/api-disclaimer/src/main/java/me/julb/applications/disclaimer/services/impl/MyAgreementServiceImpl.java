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

package me.julb.applications.disclaimer.services.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.disclaimer.entities.AgreementEntity;
import me.julb.applications.disclaimer.entities.DisclaimerEntity;
import me.julb.applications.disclaimer.entities.mappers.AgreementEntityMapper;
import me.julb.applications.disclaimer.repositories.AgreementRepository;
import me.julb.applications.disclaimer.repositories.DisclaimerRepository;
import me.julb.applications.disclaimer.services.MyAgreementService;
import me.julb.applications.disclaimer.services.UserAgreementService;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementCreationDTO;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import me.julb.applications.disclaimer.services.exceptions.DisclaimerIsNotActiveException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The connected user agreement service implementation.
 * <br>
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
    private AgreementEntityMapper mapper;

    /**
     * The user ref mapper.
     */
    @Autowired
    private UserRefEntityMapper userRefMapper;

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
    public Flux<AgreementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return securityService.getConnectedUserId().flatMapMany(userId -> {
            return userAgreementService.findAll(userId, searchable, pageable);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AgreementDTO> findOne(@NotNull @Identifier String disclaimerId) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAgreementService.findOne(userId, disclaimerId);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AgreementDTO> create(@NotNull @Identifier String disclaimerId, @NotNull @Valid AgreementCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the disclaimer exists
            return disclaimerRepository.findByTmAndId(tm, disclaimerId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(DisclaimerEntity.class, disclaimerId)))
                .flatMap(disclaimer -> {
                    // Check if active
                    if (!disclaimer.getActive().booleanValue()) {
                        return Mono.error(new DisclaimerIsNotActiveException(disclaimerId));
                    }

                    return securityService.getConnectedUserId().flatMap(userId -> {
                        return agreementRepository.existsByTmAndDisclaimerIdAndUser_Id(tm, disclaimerId, userId).flatMap(alreadyExists -> {
                            if(alreadyExists.booleanValue()) {
                                return Mono.error(new ResourceAlreadyExistsException(AgreementEntity.class, Map.<String, String> of("disclaimer", disclaimerId, "user", userId)));
                            }

                            // Update the entity
                            AgreementEntity entityToCreate = mapper.map(creationDTO);
                            entityToCreate.setDisclaimer(disclaimer);
                            return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                                return agreementRepository.save(entityToCreateWithFields).map(mapper::map);
                            });
                        });
                    });
            });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a disclaimer.
     * @param entity the entity.
     */
    private Mono<AgreementEntity> onPersist(String tm, AgreementEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connnectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());
            entity.setAgreedAt(DateUtility.dateTimeNow());

            // Add author.
            entity.setUser(userRefMapper.map(connnectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<AgreementEntity> postResourceEvent(AgreementEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.AGREEMENT)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
