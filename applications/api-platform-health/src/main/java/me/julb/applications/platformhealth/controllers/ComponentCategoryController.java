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

package me.julb.applications.platformhealth.controllers;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.platformhealth.services.ComponentCategoryService;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage component categories.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/component-categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentCategoryController {

    /**
     * The component category service.
     */
    @Autowired
    private ComponentCategoryService componentCategoryService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the component categories.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the component category paged list.
     */
    @Operation(summary = "list component categories")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('component-category', 'read')")
    public Page<ComponentCategoryDTO> findAll(Searchable searchable, Pageable pageable) {
        return componentCategoryService.findAll(searchable, pageable);
    }

    /**
     * Finds a component category by its ID.
     * @param id the ID of the component category to fetch.
     * @return the component category fetched.
     */
    @Operation(summary = "gets a component category")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'component-category', 'read')")
    public ComponentCategoryDTO get(@PathVariable @Identifier String id) {
        return componentCategoryService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an component category.
     * @param creationDTO the DTO to create the component category.
     * @return the created component category.
     */
    @Operation(summary = "creates an component category")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('component-category', 'create')")
    public ComponentCategoryDTO create(@RequestBody @NotNull @Valid ComponentCategoryCreationDTO creationDTO) {
        return componentCategoryService.create(creationDTO);
    }

    /**
     * Updates a component category.
     * @param id the ID of the component category to update.
     * @param updateDTO the DTO to update the component category.
     * @return the response.
     */
    @Operation(summary = "updates a component category")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'component-category', 'update')")
    public ComponentCategoryDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ComponentCategoryUpdateDTO updateDTO) {
        return componentCategoryService.update(id, updateDTO);
    }

    /**
     * Patches a component category.
     * @param id the ID of the component category to patch.
     * @param patchDTO the DTO to patch the component category.
     * @return the response.
     */
    @Operation(summary = "patches a component category")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'component-category', 'update')")
    public ComponentCategoryDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ComponentCategoryPatchDTO patchDTO) {
        return componentCategoryService.patch(id, patchDTO);
    }

    /**
     * Deletes a component category.
     * @param id the id of the component category to delete.
     */
    @Operation(summary = "deletes an component category")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'component-category', 'delete')")
    public void delete(@PathVariable String id) {
        componentCategoryService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
