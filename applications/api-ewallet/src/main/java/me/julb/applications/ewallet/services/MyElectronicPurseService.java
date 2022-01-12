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
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;

import reactor.core.publisher.Mono;

/**
 * The user electronic purse service.
 * <br>
 * @author Julb.
 */
public interface MyElectronicPurseService {

    // ------------------------------------------ Read methods.

    /**
     * Gets my electronic purse.
     * @return the electronic purse.
     */
    Mono<ElectronicPurseDTO> findOne();

    // ------------------------------------------ Write methods.

    /**
     * Redeems a moneey voucher to my electronic purse.
     * @param redeemMoneyVoucher the DTO to redeem a money voucher.
     * @return the updated electronic purse.
     */
    Mono<ElectronicPurseDTO> redeemMoneyVoucher(@NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher);

    /**
     * Updates my electronic purse.
     * @param electronicPurseUpdateDTO the DTO to update a electronic purse.
     * @return the updated electronic purse.
     */
    Mono<ElectronicPurseDTO> update(@NotNull @Valid ElectronicPurseUpdateDTO electronicPurseUpdateDTO);

    /**
     * Patches my electronic purse.
     * @param electronicPursePatchDTO the DTO to update a electronic purse.
     * @return the updated electronic purse.
     */
    Mono<ElectronicPurseDTO> patch(@NotNull @Valid ElectronicPursePatchDTO electronicPursePatchDTO);

}
