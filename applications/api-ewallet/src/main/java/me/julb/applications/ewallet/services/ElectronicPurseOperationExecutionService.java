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

package me.julb.applications.ewallet.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Mono;

/**
 * The electronic purse operation execution service.
 * <br>
 * @author Julb.
 */
public interface ElectronicPurseOperationExecutionService {

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * Redeems a money voucher to the electronic purse.
     * @param electronicPurseId the electronic purse ID.
     * @param redeemMoneyVoucher the DTO to redeem a money voucher.
     * @return the updated electronic purse.
     */
    Mono<ElectronicPurseDTO> redeemMoneyVoucher(@NotNull @Identifier String electronicPurseId, @NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher);

    /**
     * Cancels the operation by doing the opposite action.
     * @param electronicPurseId the electronic purse ID.
     * @param id the operation id.
     * @return the updated electronic purse.
     */
    Mono<ElectronicPurseDTO> cancelOperation(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id);

    /**
     * Delete any traces of an operation to the electronic purse.
     * @param electronicPurseId the electronic purse ID.
     * @param id the operation id.
     * @return the updated electronic purse.
     */
    Mono<ElectronicPurseDTO> deleteOperationExecution(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id);
}
