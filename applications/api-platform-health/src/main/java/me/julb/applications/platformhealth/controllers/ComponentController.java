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

import me.julb.applications.platformhealth.services.ComponentService;
import me.julb.applications.platformhealth.services.dto.component.ComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.component.ComponentDTO;
import me.julb.applications.platformhealth.services.dto.component.ComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.component.ComponentUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage components of a category.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/component-categories/{componentCategoryId}/components", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentController {

    /**
     * The component service.
     */
    @Autowired
    private ComponentService componentService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the components.
     * @param componentCategoryId the component category identifier.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the components paged list.
     */
    @Operation(summary = "list components")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('component', 'read')")
    public Page<ComponentDTO> findAll(@PathVariable @Identifier String componentCategoryId, Searchable searchable, Pageable pageable) {
        return componentService.findAll(componentCategoryId, searchable, pageable);
    }

    /**
     * Finds a component by its ID.
     * @param componentCategoryId the component category identifier.
     * @param id the ID of the component to fetch.
     * @return the component fetched.
     */
    @Operation(summary = "gets a component of a component category")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission('component', 'read')")
    public ComponentDTO get(@PathVariable @Identifier String componentCategoryId, @PathVariable @Identifier String id) {
        return componentService.findOne(componentCategoryId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a component.
     * @param componentCategoryId the component category identifier.
     * @param creationDTO the DTO to create the component.
     * @return the created component.
     */
    @Operation(summary = "creates a component")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#id, 'component', 'create')")
    public ComponentDTO create(@PathVariable @Identifier String componentCategoryId, @RequestBody @NotNull @Valid ComponentCreationDTO creationDTO) {
        return componentService.create(componentCategoryId, creationDTO);
    }

    /**
     * Updates a component.
     * @param componentCategoryId the component category identifier.
     * @param id the ID of the component to update.
     * @param updateDTO the DTO to update the component.
     * @return the response.
     */
    @Operation(summary = "updates a component")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'component', 'update')")
    public ComponentDTO update(@PathVariable @Identifier String componentCategoryId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ComponentUpdateDTO updateDTO) {
        return componentService.update(componentCategoryId, id, updateDTO);
    }

    /**
     * Patches a component.
     * @param componentCategoryId the component category identifier.
     * @param id the ID of the component to patch.
     * @param patchDTO the DTO to patch the component.
     * @return the response.
     */
    @Operation(summary = "patches a component")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'component', 'update')")
    public ComponentDTO patch(@PathVariable @Identifier String componentCategoryId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ComponentPatchDTO patchDTO) {
        return componentService.patch(componentCategoryId, id, patchDTO);
    }

    /**
     * Deletes a component.
     * @param componentCategoryId the component category identifier.
     * @param id the id of the component to delete.
     */
    @Operation(summary = "deletes a component")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'component', 'delete')")
    public void delete(@PathVariable @Identifier String componentCategoryId, @PathVariable String id) {
        componentService.delete(componentCategoryId, id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
