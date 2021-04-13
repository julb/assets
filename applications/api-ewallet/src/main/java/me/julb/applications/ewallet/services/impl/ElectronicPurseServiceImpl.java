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

package me.julb.applications.ewallet.services.impl;

import com.google.common.base.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.entities.ElectronicPurseEntity;
import me.julb.applications.ewallet.repositories.ElectronicPurseRepository;
import me.julb.applications.ewallet.services.ElectronicPurseService;
import me.julb.applications.ewallet.services.MoneyVoucherService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseCreationWithUserDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherDTO;
import me.julb.applications.ewallet.services.exceptions.MoneyVoucherCannotBeRedeemedCurrencyMismatch;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.services.IMappingService;
import me.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The electronic purse service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ElectronicPurseServiceImpl implements ElectronicPurseService {

    /**
     * The electronic purse repository.
     */
    @Autowired
    private ElectronicPurseRepository electronicPurseRepository;

    /**
     * The money voucher service.
     */
    @Autowired
    private MoneyVoucherService moneyVoucherService;

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
    public Page<ElectronicPurseDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<ElectronicPurseEntity> spec = new SearchSpecification<ElectronicPurseEntity>(searchable).and(new TmSpecification<>(tm));
        Page<ElectronicPurseEntity> result = electronicPurseRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, ElectronicPurseDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElectronicPurseDTO findOne(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        ElectronicPurseEntity result = electronicPurseRepository.findByTmAndId(tm, id);
        if (result == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "id", id);
        }

        return mappingService.map(result, ElectronicPurseDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElectronicPurseDTO findByUserId(@NotNull @Identifier String userId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        ElectronicPurseEntity result = electronicPurseRepository.findByTmAndUser_Id(tm, userId);
        if (result == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "userId", userId);
        }

        return mappingService.map(result, ElectronicPurseDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO create(@NotNull @Valid ElectronicPurseCreationWithUserDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Gets the currency of the current setup
        ISO4217Currency currency = configSourceService.getTypedProperty("ewallet.currency", ISO4217Currency.class);

        // Check that the item exists
        if (electronicPurseRepository.existsByTmAndUser_Id(tm, creationDTO.getUser().getId())) {
            throw new ResourceAlreadyExistsException(ElectronicPurseEntity.class, "userId", creationDTO.getUser().getId());
        }

        // Create the electronic purse
        ElectronicPurseEntity entityToCreate = mappingService.map(creationDTO, ElectronicPurseEntity.class);
        entityToCreate.setAmountInCts(Long.valueOf(Integers.ZERO));
        entityToCreate.setCurrency(currency);
        this.onPersist(entityToCreate);

        ElectronicPurseEntity result = electronicPurseRepository.save(entityToCreate);
        return mappingService.map(result, ElectronicPurseDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO redeemMoneyVoucher(@NotNull @Identifier String id, @NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        ElectronicPurseEntity existing = electronicPurseRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "id", id);
        }

        // Finds the money voucher
        MoneyVoucherDTO moneyVoucher = moneyVoucherService.findByCode(redeemMoneyVoucher.getCode());
        if (moneyVoucher == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "code", redeemMoneyVoucher.getCode());
        }

        // The currencies are not matching.
        if (!Objects.equal(existing.getCurrency(), moneyVoucher.getCurrency())) {
            throw new MoneyVoucherCannotBeRedeemedCurrencyMismatch(moneyVoucher.getId(), moneyVoucher.getCurrency(), existing.getId(), existing.getCurrency());
        }

        // Everything seems to be fine. Redeem money voucher.
        MoneyVoucherDTO moneyVoucherRedeemed = moneyVoucherService.redeem(moneyVoucher.getId());

        // Add amount to electronic purse.
        existing.setAmountInCts(existing.getAmountInCts() + moneyVoucherRedeemed.getAmountInCts());

        // Track history.
        // TODO

        // Return electronic purse updated.
        ElectronicPurseEntity result = electronicPurseRepository.save(existing);
        return mappingService.map(result, ElectronicPurseDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO update(@NotNull @Identifier String id, @NotNull @Valid ElectronicPurseUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        ElectronicPurseEntity existing = electronicPurseRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "id", id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        ElectronicPurseEntity result = electronicPurseRepository.save(existing);
        return mappingService.map(result, ElectronicPurseDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO patch(@NotNull @Identifier String id, @NotNull @Valid ElectronicPursePatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        ElectronicPurseEntity existing = electronicPurseRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "id", id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        ElectronicPurseEntity result = electronicPurseRepository.save(existing);
        return mappingService.map(result, ElectronicPurseDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        ElectronicPurseEntity existing = electronicPurseRepository.findByTmAndId(tm, id);
        if (existing == null) {
            throw new ResourceNotFoundException(ElectronicPurseEntity.class, "id", id);
        }

        // Delete entity.
        electronicPurseRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(ElectronicPurseEntity entity) {
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
    private void onUpdate(ElectronicPurseEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(ElectronicPurseEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(ElectronicPurseEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.ELECTRONIC_PURSE)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
