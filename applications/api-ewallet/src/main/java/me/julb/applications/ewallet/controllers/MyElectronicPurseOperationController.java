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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.ewallet.services.MyElectronicPurseOperationService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage the operations of my electronic purse.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/electronic-purse/operations", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyElectronicPurseOperationController {

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private MyElectronicPurseOperationService myElectronicPurseOperationService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the operations of an electronic purse.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the electronic purse paged list.
     */
    @Operation(summary = "list operations of my electronic purse")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Flux<ElectronicPurseOperationDTO> findAll(Searchable searchable, Pageable pageable) {
        return myElectronicPurseOperationService.findAll(searchable, pageable);
    }

    /**
     * Gets an electronic purse operation.
     * @param id the electronic purse identifier.
     * @return the electronic purse.
     */
    @Operation(summary = "gets an operation of my electronic purse")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseOperationDTO> get(@PathVariable @Identifier String id) {
        return myElectronicPurseOperationService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Updates a electronic purse operation.
     * @param id the ID.
     * @param updateDTO the DTO to update the electronic purse.
     * @return the response.
     */
    @Operation(summary = "updates an operation of my electronic purse")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseOperationDTO> update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ElectronicPurseOperationUpdateDTO updateDTO) {
        return myElectronicPurseOperationService.update(id, updateDTO);
    }

    /**
     * Patches a electronic purse operation.
     * @param id the ID.
     * @param patchDTO the DTO to patch the electronic purse.
     * @return the response.
     */
    @Operation(summary = "patches an operation of my electronic purse")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<ElectronicPurseOperationDTO> patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ElectronicPurseOperationPatchDTO patchDTO) {
        return myElectronicPurseOperationService.patch(id, patchDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
