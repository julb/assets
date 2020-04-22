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

package io.julb.applications.platformhealth.controllers;

import io.julb.applications.platformhealth.services.PlannedMaintenanceService;
import io.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceCreationDTO;
import io.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceDTO;
import io.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenancePatchDTO;
import io.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceUpdateDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;
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

/**
 * The rest controller to manage planned maintenances.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/planned-maintenances", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlannedMaintenanceController {

    /**
     * The planned maintenance service.
     */
    @Autowired
    private PlannedMaintenanceService plannedMaintenanceService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the planned maintenances.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the planned maintenance paged list.
     */
    @Operation(summary = "list planned maintenances")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('planned-maintenance', 'read')")
    public Page<PlannedMaintenanceDTO> findAll(Searchable searchable, Pageable pageable) {
        return plannedMaintenanceService.findAll(searchable, pageable);
    }

    /**
     * Finds a planned maintenance by its ID.
     * @param id the ID of the planned maintenance to fetch.
     * @return the planned maintenance fetched.
     */
    @Operation(summary = "gets a planned maintenance")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'planned-maintenance', 'read')")
    public PlannedMaintenanceDTO get(@PathVariable @Identifier String id) {
        return plannedMaintenanceService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an planned maintenance.
     * @param creationDTO the DTO to create the planned maintenance.
     * @return the created planned maintenance.
     */
    @Operation(summary = "creates an planned maintenance")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('planned-maintenance', 'create')")
    public PlannedMaintenanceDTO create(@RequestBody @NotNull @Valid PlannedMaintenanceCreationDTO creationDTO) {
        return plannedMaintenanceService.create(creationDTO);
    }

    /**
     * Updates a planned maintenance.
     * @param id the ID of the planned maintenance to update.
     * @param updateDTO the DTO to update the planned maintenance.
     * @return the response.
     */
    @Operation(summary = "updates a planned maintenance")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'planned-maintenance', 'update')")
    public PlannedMaintenanceDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid PlannedMaintenanceUpdateDTO updateDTO) {
        return plannedMaintenanceService.update(id, updateDTO);
    }

    /**
     * Patches a planned maintenance.
     * @param id the ID of the planned maintenance to patch.
     * @param patchDTO the DTO to patch the planned maintenance.
     * @return the response.
     */
    @Operation(summary = "patches a planned maintenance")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'planned-maintenance', 'update')")
    public PlannedMaintenanceDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid PlannedMaintenancePatchDTO patchDTO) {
        return plannedMaintenanceService.patch(id, patchDTO);
    }

    /**
     * Deletes a planned maintenance.
     * @param id the id of the planned maintenance to delete.
     */
    @Operation(summary = "deletes an planned maintenance")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'planned-maintenance', 'delete')")
    public void delete(@PathVariable String id) {
        plannedMaintenanceService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}