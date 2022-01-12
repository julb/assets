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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.ewallet.services.UserElectronicPurseService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.library.utility.validator.constraints.Identifier;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage electronic purse of a user.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/electronic-purse", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserElectronicPurseController {

    /**
     * The electronic purse service.
     */
    @Autowired
    private UserElectronicPurseService electronicPurseService;

    // ------------------------------------------ Read methods.

    /**
     * Finds a electronic purse by the user ID.
     * @param userId the ID of the user.
     * @return the electronic purse fetched.
     */
    @Operation(summary = "gets a electronic purse")
    @GetMapping
    @PreAuthorize("hasPermission('electronic-purse', 'read')")
    public Mono<ElectronicPurseDTO> get(@PathVariable @Identifier String userId) {
        return electronicPurseService.findOne(userId);
    }

    // ------------------------------------------ Write methods.

    /**
     * Updates a electronic purse.
     * @param userId the ID of the user.
     * @param updateDTO the DTO to update the electronic purse.
     * @return the response.
     */
    @Operation(summary = "updates a electronic purse")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('electronic-purse', 'update')")
    public Mono<ElectronicPurseDTO> update(@PathVariable @Identifier String userId, @RequestBody @NotNull @Valid ElectronicPurseUpdateDTO updateDTO) {
        return electronicPurseService.update(userId, updateDTO);
    }

    /**
     * Patches a electronic purse.
     * @param userId the ID of the user.
     * @param patchDTO the DTO to patch the electronic purse.
     * @return the response.
     */
    @Operation(summary = "patches a electronic purse")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('electronic-purse', 'update')")
    public Mono<ElectronicPurseDTO> patch(@PathVariable @Identifier String userId, @RequestBody @NotNull @Valid ElectronicPursePatchDTO patchDTO) {
        return electronicPurseService.patch(userId, patchDTO);
    }

    /**
     * Deletes a electronic purse.
     * @param userId the ID of the user.
     */
    @Operation(summary = "deletes a electronic purse")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('electronic-purse', 'delete')")
    public Mono<Void> delete(@PathVariable @Identifier String userId) {
        return electronicPurseService.delete(userId);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
