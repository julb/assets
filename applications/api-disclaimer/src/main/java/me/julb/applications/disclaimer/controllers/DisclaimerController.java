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

package me.julb.applications.disclaimer.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.disclaimer.services.DisclaimerService;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerCreationDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerPatchDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage disclaimers.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/disclaimers", produces = MediaType.APPLICATION_JSON_VALUE)
public class DisclaimerController {

    /**
     * The disclaimer service.
     */
    @Autowired
    private DisclaimerService disclaimerService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the disclaimers.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the disclaimer paged list.
     */
    @Operation(summary = "list disclaimers")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('disclaimer', 'read')")
    public Flux<DisclaimerDTO> findAll(Searchable searchable, Pageable pageable) {
        return disclaimerService.findAll(searchable, pageable);
    }

    /**
     * Finds a disclaimer by its ID.
     * @param id the ID of the disclaimer to fetch.
     * @return the disclaimer fetched.
     */
    @Operation(summary = "gets a disclaimer")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'disclaimer', 'read')")
    public Mono<DisclaimerDTO> get(@PathVariable @Identifier String id) {
        return disclaimerService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a disclaimer.
     * @param creationDTO the DTO to create the disclaimer.
     * @return the created disclaimer.
     */
    @Operation(summary = "creates a disclaimer")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('disclaimer', 'create')")
    public Mono<DisclaimerDTO> create(@RequestBody @NotNull @Valid DisclaimerCreationDTO creationDTO) {
        return disclaimerService.create(creationDTO);
    }

    /**
     * Updates a disclaimer.
     * @param id the ID of the disclaimer to update.
     * @param updateDTO the DTO to update the disclaimer.
     * @return the response.
     */
    @Operation(summary = "updates a disclaimer")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'disclaimer', 'update')")
    public Mono<DisclaimerDTO> update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid DisclaimerUpdateDTO updateDTO) {
        return disclaimerService.update(id, updateDTO);
    }

    /**
     * Publishes a disclaimer.
     * @param id the ID of the disclaimer to publish.
     * @return the response.
     */
    @Operation(summary = "publishes a disclaimer")
    @PutMapping(path = "/{id}/active")
    @PreAuthorize("hasPermission(#id, 'disclaimer', 'update')")
    public Mono<DisclaimerDTO> publish(@PathVariable @Identifier String id) {
        return disclaimerService.publish(id);
    }

    /**
     * Unpublishes a disclaimer.
     * @param id the ID of the disclaimer to unpublish.
     * @return the response.
     */
    @Operation(summary = "unpublishes a disclaimer")
    @DeleteMapping(path = "/{id}/active")
    @PreAuthorize("hasPermission(#id, 'disclaimer', 'update')")
    public Mono<DisclaimerDTO> unpublish(@PathVariable @Identifier String id) {
        return disclaimerService.unpublish(id);
    }

    /**
     * Patches a disclaimer.
     * @param id the ID of the disclaimer to patch.
     * @param patchDTO the DTO to patch the disclaimer.
     * @return the response.
     */
    @Operation(summary = "patches a disclaimer")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'disclaimer', 'update')")
    public Mono<DisclaimerDTO> patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid DisclaimerPatchDTO patchDTO) {
        return disclaimerService.patch(id, patchDTO);
    }

    /**
     * Deletes a disclaimer.
     * @param id the id of the disclaimer to delete.
     * @return the void result.
     */
    @Operation(summary = "deletes a disclaimer")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'disclaimer', 'delete')")
    public Mono<Void> delete(@PathVariable String id) {
        return disclaimerService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
