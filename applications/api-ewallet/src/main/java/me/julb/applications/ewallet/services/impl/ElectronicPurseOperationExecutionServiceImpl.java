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

import java.util.HashMap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.services.ElectronicPurseOperationExecutionService;
import me.julb.applications.ewallet.services.ElectronicPurseOperationService;
import me.julb.applications.ewallet.services.ElectronicPurseService;
import me.julb.applications.ewallet.services.MoneyVoucherService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationCreationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationType;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherDTO;
import me.julb.applications.ewallet.services.exceptions.MoneyVoucherCannotBeRedeemedCurrencyMismatch;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The electronic purse service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ElectronicPurseOperationExecutionServiceImpl implements ElectronicPurseOperationExecutionService {

    /**
     * The electronic purse service.
     */
    @Autowired
    private ElectronicPurseService electronicPurseService;

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private ElectronicPurseOperationService electronicPurseOperationService;

    /**
     * The money voucher service.
     */
    @Autowired
    private MoneyVoucherService moneyVoucherService;

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO redeemMoneyVoucher(@NotNull @Identifier String id, @NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher) {
        // Check that the item exists
        ElectronicPurseDTO existing = electronicPurseService.findOne(id);

        // Finds the money voucher
        MoneyVoucherDTO moneyVoucher = moneyVoucherService.findByCode(redeemMoneyVoucher.getCode());

        // The currencies are not matching.
        if (!Objects.equal(existing.getCurrency(), moneyVoucher.getCurrency())) {
            throw new MoneyVoucherCannotBeRedeemedCurrencyMismatch(moneyVoucher.getId(), moneyVoucher.getCurrency(), existing.getId(), existing.getCurrency());
        }

        // Everything seems to be fine. Redeem money voucher.
        MoneyVoucherDTO moneyVoucherRedeemed = moneyVoucherService.redeem(moneyVoucher.getId());

        // Record operation.
        ElectronicPurseOperationCreationDTO operation = new ElectronicPurseOperationCreationDTO();
        operation.setAmountInCts(moneyVoucherRedeemed.getAmountInCts());
        operation.setCurrency(moneyVoucherRedeemed.getCurrency());
        operation.setLocalizedMessage(new HashMap<>());
        operation.setSendNotification(false);
        operation.setType(ElectronicPurseOperationType.CREDIT_MONEY_VOUCHER_REDEMPTION);
        electronicPurseOperationService.create(existing.getId(), operation);

        // Refresh balance and get electronic purse updated.
        return electronicPurseService.refreshBalance(existing.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO deleteOperationExecution(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id) {
        // Delete operation.
        electronicPurseOperationService.delete(electronicPurseId, id);

        // Refresh balance and get electronic purse updated.
        return electronicPurseService.refreshBalance(electronicPurseId);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
