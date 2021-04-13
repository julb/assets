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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.services.ElectronicPurseService;
import me.julb.applications.ewallet.services.UserElectronicPurseService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The electronic purse service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserElectronicPurseServiceImpl implements UserElectronicPurseService {

    /**
     * The electronic purse service.
     */
    @Autowired
    private ElectronicPurseService electronicPurseService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public ElectronicPurseDTO findOne(@NotNull @Identifier String userId) {
        return electronicPurseService.findByUserId(userId);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO redeemMoneyVoucher(@NotNull @Identifier String userId, @NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseService.redeemMoneyVoucher(electronicPurse.getId(), redeemMoneyVoucher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO update(@NotNull @Identifier String userId, @NotNull @Valid ElectronicPurseUpdateDTO updateDTO) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseService.update(electronicPurse.getId(), updateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseDTO patch(@NotNull @Identifier String userId, @NotNull @Valid ElectronicPursePatchDTO patchDTO) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseService.patch(electronicPurse.getId(), patchDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        electronicPurseService.delete(electronicPurse.getId());
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
