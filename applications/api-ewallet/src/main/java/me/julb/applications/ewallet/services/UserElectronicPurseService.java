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
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The electronic purse service.
 * <br>
 * @author Julb.
 */
public interface UserElectronicPurseService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a electronic purse through its user ID.
     * @param userId the user identifier.
     * @return the electronic purse.
     */
    ElectronicPurseDTO findOne(@NotNull @Identifier String userId);

    // ------------------------------------------ Write methods.

    /**
     * Redeems a money voucher to the user's electronic purse.
     * @param userId the user identifier.
     * @param redeemMoneyVoucher the DTO to redeem a money voucher.
     * @return the updated electronic purse.
     */
    ElectronicPurseDTO redeemMoneyVoucher(@NotNull @Identifier String userId, @NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher);

    /**
     * Updates a electronic purse.
     * @param userId the user identifier.
     * @param electronicPurseUpdateDTO the DTO to update a electronic purse.
     * @return the updated electronic purse.
     */
    ElectronicPurseDTO update(@NotNull @Identifier String userId, @NotNull @Valid ElectronicPurseUpdateDTO electronicPurseUpdateDTO);

    /**
     * Patches a electronic purse.
     * @param userId the user identifier.
     * @param electronicPursePatchDTO the DTO to update a electronic purse.
     * @return the updated electronic purse.
     */
    ElectronicPurseDTO patch(@NotNull @Identifier String userId, @NotNull @Valid ElectronicPursePatchDTO electronicPursePatchDTO);

    /**
     * Deletes all electronic purses of a user.
     * @param userId the user identifier.
     */
    void delete(@NotNull @Identifier String userId);
}
