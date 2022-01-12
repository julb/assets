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

package me.julb.applications.urlshortener.controllers;

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

import me.julb.applications.urlshortener.services.LinkService;
import me.julb.applications.urlshortener.services.dto.LinkCreationDTO;
import me.julb.applications.urlshortener.services.dto.LinkDTO;
import me.julb.applications.urlshortener.services.dto.LinkPatchDTO;
import me.julb.applications.urlshortener.services.dto.LinkUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage links.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/links", produces = MediaType.APPLICATION_JSON_VALUE)
public class LinkController {

    /**
     * The link service.
     */
    @Autowired
    private LinkService linkService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the links.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the link paged list.
     */
    @Operation(summary = "list links")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('link', 'read')")
    public Flux<LinkDTO> findAll(Searchable searchable, Pageable pageable) {
        return linkService.findAll(searchable, pageable);
    }

    /**
     * Finds a link by its ID.
     * @param id the ID of the link to fetch.
     * @return the link fetched.
     */
    @Operation(summary = "gets a link")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'link', 'read')")
    public Mono<LinkDTO> get(@PathVariable @Identifier String id) {
        return linkService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a link.
     * @param creationDTO the DTO to create the link.
     * @return the created link.
     */
    @Operation(summary = "creates a link")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('link', 'create')")
    public Mono<LinkDTO> create(@RequestBody @NotNull @Valid LinkCreationDTO creationDTO) {
        return linkService.create(creationDTO);
    }

    /**
     * Updates a link.
     * @param id the ID of the link to update.
     * @param updateDTO the DTO to update the link.
     * @return the response.
     */
    @Operation(summary = "updates a link")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'link', 'update')")
    public Mono<LinkDTO> update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid LinkUpdateDTO updateDTO) {
        return linkService.update(id, updateDTO);
    }

    /**
     * Patches a link.
     * @param id the ID of the link to patch.
     * @param patchDTO the DTO to patch the link.
     * @return the response.
     */
    @Operation(summary = "patches a link")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'link', 'update')")
    public Mono<LinkDTO> patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid LinkPatchDTO patchDTO) {
        return linkService.patch(id, patchDTO);
    }

    /**
     * Resets the hits for a link.
     * @param id the id of the link.
     */
    @Operation(summary = "resets the number of hits of a link")
    @DeleteMapping(path = "/{id}/hits")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'link', 'update')")
    public Mono<Void> resetNumberOfHits(@PathVariable String id) {
        return linkService.resetNumberOfHits(id).then();
    }

    /**
     * Deletes a link.
     * @param id the id of the link to delete.
     */
    @Operation(summary = "deletes a link")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'link', 'delete')")
    public Mono<Void> delete(@PathVariable String id) {
        return linkService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
