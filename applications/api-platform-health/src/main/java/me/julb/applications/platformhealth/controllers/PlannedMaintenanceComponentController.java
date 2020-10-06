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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.platformhealth.services.PlannedMaintenanceComponentService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenancecomponent.PlannedMaintenanceComponentDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage components of an planned maintenance.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/planned-maintenances/{plannedMaintenanceId}/components", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlannedMaintenanceComponentController {

    /**
     * The components linked to a planned maintenance service.
     */
    @Autowired
    private PlannedMaintenanceComponentService plannedMaintenanceComponentService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the components linked to a planned maintenance.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the components linked to a planned maintenance paged list.
     */
    @Operation(summary = "list components linked to a planned maintenance")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'read')")
    public Page<PlannedMaintenanceComponentDTO> findAll(@PathVariable @Identifier String plannedMaintenanceId, Searchable searchable, Pageable pageable) {
        return plannedMaintenanceComponentService.findAll(plannedMaintenanceId, searchable, pageable);
    }

    /**
     * Checks if a component is linked to a planned maintenance.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param componentId the ID of the component.
     * @return the planned maintenance component DTO.
     */
    @Operation(summary = "verify if a component is linked to a planned maintenance")
    @GetMapping(path = "/{componentId}")
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'read')")
    public PlannedMaintenanceComponentDTO get(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable @Identifier String componentId) {
        return plannedMaintenanceComponentService.findOne(plannedMaintenanceId, componentId);
    }

    // ------------------------------------------ Write methods.

    /**
     * Links a component to a planned maintenance.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param componentId the ID of the component.
     * @return the link between planned maintenance and component created.
     */
    @Operation(summary = "link a component to a planned maintenance")
    @PostMapping(path = "/{componentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'update')")
    public PlannedMaintenanceComponentDTO create(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable @Identifier String componentId) {
        return plannedMaintenanceComponentService.create(plannedMaintenanceId, componentId);
    }

    /**
     * Unlink a component from a planned maintenance.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param componentId the ID of the component.
     */
    @Operation(summary = "unlink a component from a planned maintenance")
    @DeleteMapping(path = "/{componentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'update')")
    public void delete(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable String componentId) {
        plannedMaintenanceComponentService.delete(plannedMaintenanceId, componentId);
    }
}
