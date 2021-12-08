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
import org.springframework.data.domain.Page;
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
import me.julb.library.dto.simple.user.UserRefDTO;
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
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

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
    public Page<MoneyVoucherDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<MoneyVoucherEntity> spec = new SearchSpecification<MoneyVoucherEntity>(searchable).and(new TmSpecification<>(tm));
        Page<MoneyVoucherEntity> result = moneyVoucherRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoneyVoucherDTO findByCode(@NotNull @NotBlank @MoneyVoucherCode String code) {
        String tm = TrademarkContextHolder.getTrademark();

        // Get hash
        String voucherCodeHash = hash(code);

        // Finds money voucher by code.
        MoneyVoucherEntity result = moneyVoucherRepository.findByTmAndHashIgnoreCase(tm, voucherCodeHash);
        if (result == null) {
            throw new ResourceNotFoundException(MoneyVoucherEntity.class, "code", code);
        }

        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MoneyVoucherDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the money voucher exists
        MoneyVoucherEntity result = moneyVoucherRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(MoneyVoucherEntity.class, id);
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MoneyVoucherWithRawCodeDTO create(@NotNull @Valid MoneyVoucherCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Gets the currency of the current setup
        ISO4217Currency currency = configSourceService.getTypedProperty("ewallet.currency", ISO4217Currency.class);

        // Generate voucher code
        String rawVoucherCode = buildVoucherCode();
        String voucherCodeHash = hash(rawVoucherCode);

        // Check if the code hasn't already used.
        if (moneyVoucherRepository.existsByTmAndHashIgnoreCase(tm, voucherCodeHash)) {
            throw new ResourceAlreadyExistsException(MoneyVoucherEntity.class, "hash", voucherCodeHash);
        }

        // Create the voucher code
        MoneyVoucherEntity entityToCreate = mapper.map(creationDTO);
        entityToCreate.getAmount().setCurrency(currency);
        entityToCreate.setEnabled(false);
        entityToCreate.setHash(voucherCodeHash);
        entityToCreate.setRedeemed(false);
        entityToCreate.setSecuredCode(passwordEncoderService.encode(rawVoucherCode));
        this.onPersist(entityToCreate);

        MoneyVoucherEntity result = moneyVoucherRepository.save(entityToCreate);
        MoneyVoucherWithRawCodeDTO moneyVoucherWithRawCode = mapper.mapWithRawCode(result);

        // Add code to the return value.
        moneyVoucherWithRawCode.setRawCode(rawVoucherCode);

        // Return value.
        return moneyVoucherWithRawCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MoneyVoucherDTO redeem(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the money voucher exists
        MoneyVoucherEntity existing = moneyVoucherRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(MoneyVoucherEntity.class, id);
        }

        // Check if money voucher is already redeemed
        if (BooleanUtils.isTrue(existing.getRedeemed())) {
            throw new MoneyVoucherCannotBeRedeemedAlreadyRedeemed(existing.getId(), existing.getRedemptionDateTime());
        }

        // Check if money voucher is enabled
        if (BooleanUtils.isFalse(existing.getEnabled())) {
            throw new MoneyVoucherCannotBeRedeemedVoucherDisabled(existing.getId());
        }

        // Check if money voucher is expired
        if (existing.getExpiryDateTime() != null && DateUtility.dateTimeBeforeNow(existing.getExpiryDateTime())) {
            throw new MoneyVoucherCannotBeRedeemedVoucherExpired(existing.getId(), existing.getExpiryDateTime());
        }

        // Redeem
        existing.setRedeemed(true);
        existing.setRedemptionDateTime(DateUtility.dateTimeNow());
        UserRefDTO connnectedUser = securityService.getConnectedUserRefIdentity();
        existing.setRedeemedBy(userRefMapper.map(connnectedUser));

        // Update
        this.onUpdate(existing);

        MoneyVoucherEntity result = moneyVoucherRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MoneyVoucherDTO update(@NotNull @Identifier String id, @NotNull @Valid MoneyVoucherUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the money voucher exists
        MoneyVoucherEntity existing = moneyVoucherRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(MoneyVoucherEntity.class, id);
        }

        // Update the entity
        mapper.map(updateDTO, existing);
        this.onUpdate(existing);

        MoneyVoucherEntity result = moneyVoucherRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MoneyVoucherDTO patch(@NotNull @Identifier String id, @NotNull @Valid MoneyVoucherPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the money voucher exists
        MoneyVoucherEntity existing = moneyVoucherRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(MoneyVoucherEntity.class, id);
        }

        // Update the entity
        mapper.map(patchDTO, existing);
        this.onUpdate(existing);

        MoneyVoucherEntity result = moneyVoucherRepository.save(existing);
        return mapper.map(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the money voucher exists
        MoneyVoucherEntity existing = moneyVoucherRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(MoneyVoucherEntity.class, id);
        }

        // Delete entity.
        moneyVoucherRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
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
     * @param entity the entity.
     */
    private void onPersist(MoneyVoucherEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        // Add author.
        UserRefDTO connnectedUser = securityService.getConnectedUserRefIdentity();
        entity.setUser(userRefMapper.map(connnectedUser));

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a money voucher.
     * @param entity the entity.
     */
    private void onUpdate(MoneyVoucherEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a money voucher.
     * @param entity the entity.
     */
    private void onDelete(MoneyVoucherEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(MoneyVoucherEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.MONEY_VOUCHER)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
