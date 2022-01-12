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

package me.julb.applications.ewallet.services.impl;

import com.google.common.base.Splitter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.entities.MoneyVoucherEntity;
import me.julb.applications.ewallet.entities.mappers.MoneyVoucherEntityMapper;
import me.julb.applications.ewallet.repositories.MoneyVoucherRepository;
import me.julb.applications.ewallet.services.MoneyVoucherService;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherCreationDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherPatchDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherUpdateDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherWithRawCodeDTO;
import me.julb.applications.ewallet.services.exceptions.MoneyVoucherCannotBeRedeemedAlreadyRedeemed;
import me.julb.applications.ewallet.services.exceptions.MoneyVoucherCannotBeRedeemedVoucherDisabled;
import me.julb.applications.ewallet.services.exceptions.MoneyVoucherCannotBeRedeemedVoucherExpired;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.random.RandomUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.MoneyVoucherCode;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The money voucher service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MoneyVoucherServiceImpl implements MoneyVoucherService {

    /**
     * The money voucher repository.
     */
    @Autowired
    private MoneyVoucherRepository moneyVoucherRepository;

    /**
     * The mapper.
     */
    @Autowired
    private MoneyVoucherEntityMapper mapper;

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

    /**
     * The password encoder.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * The config source service.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<MoneyVoucherDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<MoneyVoucherEntity> spec = new SearchSpecification<MoneyVoucherEntity>(searchable).and(new TmSpecification<>(tm));
            return moneyVoucherRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MoneyVoucherDTO> findByCode(@NotNull @NotBlank @MoneyVoucherCode String code) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            String voucherCodeHash = hash(code);

            // Check that the announcement exists
            return moneyVoucherRepository.findByTmAndHashIgnoreCase(tm, voucherCodeHash)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(MoneyVoucherEntity.class, "code", code)))
                .map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<MoneyVoucherDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return moneyVoucherRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(MoneyVoucherEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<MoneyVoucherWithRawCodeDTO> create(@NotNull @Valid MoneyVoucherCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Gets the currency of the current setup
            ISO4217Currency currency = configSourceService.getTypedProperty(tm, "ewallet.currency", ISO4217Currency.class);

            // Generate voucher code
            String rawVoucherCode = buildVoucherCode();
            String voucherCodeHash = hash(rawVoucherCode);

            // Check if the code hasn't already used.
            return moneyVoucherRepository.existsByTmAndHashIgnoreCase(tm, voucherCodeHash)
                .flatMap(alreadyExists -> {
                    if(alreadyExists.booleanValue()) {
                        return Mono.error(new ResourceAlreadyExistsException(MoneyVoucherEntity.class, "hash", voucherCodeHash));
                    }

                    // Create the voucher code
                    MoneyVoucherEntity entityToCreate = mapper.map(creationDTO);
                    entityToCreate.getAmount().setCurrency(currency);
                    entityToCreate.setEnabled(false);
                    entityToCreate.setHash(voucherCodeHash);
                    entityToCreate.setRedeemed(false);
                    entityToCreate.setSecuredCode(passwordEncoderService.encode(rawVoucherCode));
                    return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                        return moneyVoucherRepository.save(entityToCreateWithFields).map(result -> {
                            MoneyVoucherWithRawCodeDTO moneyVoucherWithRawCode = mapper.mapWithRawCode(result);
                            moneyVoucherWithRawCode.setRawCode(rawVoucherCode);
                            return moneyVoucherWithRawCode;
                        });
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<MoneyVoucherDTO> redeem(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return moneyVoucherRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(MoneyVoucherEntity.class, id)))
                .flatMap(existing -> {
                    // Check if money voucher is already redeemed
                    if (BooleanUtils.isTrue(existing.getRedeemed())) {
                        return Mono.error(new MoneyVoucherCannotBeRedeemedAlreadyRedeemed(existing.getId(), existing.getRedemptionDateTime()));
                    }

                    // Check if money voucher is enabled
                    if (BooleanUtils.isFalse(existing.getEnabled())) {
                        return Mono.error(new MoneyVoucherCannotBeRedeemedVoucherDisabled(existing.getId()));
                    }

                    // Check if money voucher is expired
                    if (existing.getExpiryDateTime() != null && DateUtility.dateTimeBeforeNow(existing.getExpiryDateTime())) {
                        return Mono.error(new MoneyVoucherCannotBeRedeemedVoucherExpired(existing.getId(), existing.getExpiryDateTime()));
                    }

                    return securityService.getConnectedUserRefIdentity().flatMap(connectedUser -> {
                        // Redeem
                        existing.setRedeemed(true);
                        existing.setRedemptionDateTime(DateUtility.dateTimeNow());
                        existing.setRedeemedBy(userRefMapper.map(connectedUser));
                        
                        // Proceed to the update
                        return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                            return moneyVoucherRepository.save(entityToUpdateWithFields).map(mapper::map);
                        });
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<MoneyVoucherDTO> update(@NotNull @Identifier String id, @NotNull @Valid MoneyVoucherUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return moneyVoucherRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(MoneyVoucherEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(updateDTO, existing);
                    
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return moneyVoucherRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<MoneyVoucherDTO> patch(@NotNull @Identifier String id, @NotNull @Valid MoneyVoucherPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return moneyVoucherRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(MoneyVoucherEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    mapper.map(patchDTO, existing);
                    
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return moneyVoucherRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return moneyVoucherRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(MoneyVoucherEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return moneyVoucherRepository.delete(existing).then(
                        this.onDelete(existing)
                    ).then();
                });
        });
    }

    // ------------------------------------------ Utility methods.

    /**
     * Builds a random voucher code.
     * @return a voucher code.
     */
    protected String buildVoucherCode() {
        String voucherCodeValue = RandomUtility.generateAlphaNumericToken(Integers.SIXTEEN).toUpperCase();
        Iterable<String> splittedVoucherCode = Splitter.fixedLength(Integers.FOUR).split(voucherCodeValue);
        return StringUtils.join(splittedVoucherCode, Chars.DASH);
    }

    /**
     * Gets the hash from the given code.
     * @param code the code.
     * @return the hash of a voucher code.
     */
    protected String hash(String code) {
        return DigestUtils.sha256Hex(code);
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a money voucher.
     * @param tm the trademark.
     * @param entity the entity.
     */
    private Mono<MoneyVoucherEntity> onPersist(String tm, MoneyVoucherEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connnectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());

            // Add author.
            entity.setUser(userRefMapper.map(connnectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a money voucher.
     * @param entity the entity.
     */
    private Mono<MoneyVoucherEntity> onUpdate(MoneyVoucherEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a money voucher.
     * @param entity the entity.
     */
    private Mono<MoneyVoucherEntity> onDelete(MoneyVoucherEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<MoneyVoucherEntity> postResourceEvent(MoneyVoucherEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.MONEY_VOUCHER)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
