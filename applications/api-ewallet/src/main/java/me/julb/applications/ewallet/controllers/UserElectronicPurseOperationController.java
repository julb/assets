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

package me.julb.applications.ewallet.controllers;

import io.swagger.v3.oas.annotations.Operation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import me.julb.applications.ewallet.services.UserElectronicPurseOperationService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage the operation of an electronic purse.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/electronic-purse/operations", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserElectronicPurseOperationController {

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private UserElectronicPurseOperationService userElectronicPurseOperationService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the operations of an electronic purse.
     * @param userId the electronic purse ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the electronic purse paged list.
     */
    @Operation(summary = "list operations of a user electronic purse")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('electronic-purse-operation', 'read')")
    public Page<ElectronicPurseOperationDTO> findAll(@PathVariable @Identifier String userId, Searchable searchable, Pageable pageable) {
        return userElectronicPurseOperationService.findAll(userId, searchable, pageable);
    }

    /**
     * Gets an electronic purse operation.
     * @param userId the electronic purse ID.
     * @param id the electronic purse identifier.
     * @return the electronic purse.
     */
    @Operation(summary = "gets an operation of a user electronic purse")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'electronic-purse-operation', 'read')")
    public ElectronicPurseOperationDTO get(@PathVariable @Identifier String userId, @PathVariable @Identifier String id) {
        return userElectronicPurseOperationService.findOne(userId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Updates a electronic purse operation.
     * @param userId the electronic purse ID.
     * @param id the ID.
     * @param updateDTO the DTO to update the electronic purse.
     * @return the response.
     */
    @Operation(summary = "updates an operation of a user electronic purse")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'electronic-purse-operation', 'update')")
    public ElectronicPurseOperationDTO update(@PathVariable @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ElectronicPurseOperationUpdateDTO updateDTO) {
        return userElectronicPurseOperationService.update(userId, id, updateDTO);
    }

    /**
     * Patches a electronic purse operation.
     * @param userId the electronic purse ID.
     * @param id the ID.
     * @param patchDTO the DTO to patch the electronic purse.
     * @return the response.
     */
    @Operation(summary = "patches an operation of a user electronic purse")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'electronic-purse-operation', 'update')")
    public ElectronicPurseOperationDTO patch(@PathVariable @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ElectronicPurseOperationPatchDTO patchDTO) {
        return userElectronicPurseOperationService.patch(userId, id, patchDTO);
    }

    /**
     * Deletes a electronic purse operation.
     * @param userId the electronic purse ID.
     * @param id the ID.
     */
    @Operation(summary = "deletes an operation of an a user electronic purse")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'electronic-purse-operation', 'delete')")
    public void delete(@PathVariable @Identifier String userId, @PathVariable @Identifier String id) {
        userElectronicPurseOperationService.delete(userId, id);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
