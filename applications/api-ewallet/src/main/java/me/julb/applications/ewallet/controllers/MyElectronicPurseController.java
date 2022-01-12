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

package me.julb.applications.ewallet.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.ewallet.services.MyElectronicPurseService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.RedeemMoneyVoucherDTO;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage my electronic purse.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/electronic-purse", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyElectronicPurseController {

    /**
     * The my electronic purse service.
     */
    @Autowired
    private MyElectronicPurseService myElectronicPurseService;

    // ------------------------------------------ Read methods.

    /**
     * Finds my electronic purse.
     * @return the electronic purse fetched.
     */
    @Operation(summary = "gets my electronic purse")
    @GetMapping
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseDTO> get() {
        return myElectronicPurseService.findOne();
    }

    // ------------------------------------------ Write methods.

    /**
     * Redeems a money voucher in my electronic purse.
     * @param redeemMoneyVoucherDTO the DTO to update the electronic purse.
     * @return the response.
     */
    @Operation(summary = "redeem a money voucher into my electronic purse")
    @PostMapping(path = "/.redeem", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseDTO> redeem(@RequestBody @NotNull @Valid RedeemMoneyVoucherDTO redeemMoneyVoucherDTO) {
        return myElectronicPurseService.redeemMoneyVoucher(redeemMoneyVoucherDTO);
    }

    /**
     * Updates a electronic purse.
     * @param updateDTO the DTO to update the electronic purse.
     * @return the response.
     */
    @Operation(summary = "updates my electronic purse")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseDTO> update(@RequestBody @NotNull @Valid ElectronicPurseUpdateDTO updateDTO) {
        return myElectronicPurseService.update(updateDTO);
    }

    /**
     * Patches a electronic purse.
     * @param patchDTO the DTO to patch the electronic purse.
     * @return the response.
     */
    @Operation(summary = "patches my electronic purse")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseDTO> patch(@RequestBody @NotNull @Valid ElectronicPursePatchDTO patchDTO) {
        return myElectronicPurseService.patch(patchDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
