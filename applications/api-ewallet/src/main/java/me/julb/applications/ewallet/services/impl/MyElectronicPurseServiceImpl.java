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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.services.MyElectronicPurseService;
import me.julb.applications.ewallet.services.UserElectronicPurseService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Mono;

/**
 * The electronic purse service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyElectronicPurseServiceImpl implements MyElectronicPurseService {

    /**
     * The electronic purse service.
     */
    @Autowired
    private UserElectronicPurseService userElectronicPurseService;

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ElectronicPurseDTO> findOne() {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userElectronicPurseService.findOne(userId);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseDTO> redeemMoneyVoucher(@NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucher) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userElectronicPurseService.redeemMoneyVoucher(userId, redeemMoneyVoucher);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseDTO> update(@NotNull @Valid ElectronicPurseUpdateDTO updateDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userElectronicPurseService.update(userId, updateDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<ElectronicPurseDTO> patch(@NotNull @Valid ElectronicPursePatchDTO patchDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userElectronicPurseService.patch(userId, patchDTO);
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.
}
