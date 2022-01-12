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

package me.julb.applications.platformhealth.controllers;

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

import me.julb.applications.platformhealth.services.PlannedMaintenanceHistoryService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage history of an planned maintenance.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/planned-maintenances/{plannedMaintenanceId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlannedMaintenanceHistoryController {

    /**
     * The planned maintenance history service.
     */
    @Autowired
    private PlannedMaintenanceHistoryService plannedMaintenanceHistoryService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the planned maintenance history paged list.
     */
    @Operation(summary = "list planned maintenance history")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'read')")
    public Flux<PlannedMaintenanceHistoryDTO> findAll(@PathVariable @Identifier String plannedMaintenanceId, Searchable searchable, Pageable pageable) {
        return plannedMaintenanceHistoryService.findAll(plannedMaintenanceId, searchable, pageable);
    }

    /**
     * Finds a planned maintenance history by its ID.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param id the ID of the planned maintenance history to fetch.
     * @return the planned maintenance history fetched.
     */
    @Operation(summary = "gets an history of an planned maintenance")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'read')")
    public Mono<PlannedMaintenanceHistoryDTO> get(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable @Identifier String id) {
        return plannedMaintenanceHistoryService.findOne(plannedMaintenanceId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param creationDTO the DTO to create the planned maintenance history.
     * @return the created planned maintenance history.
     */
    @Operation(summary = "creates an planned maintenance history")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'update')")
    public Mono<PlannedMaintenanceHistoryDTO> create(@PathVariable @Identifier String plannedMaintenanceId, @RequestBody @NotNull @Valid PlannedMaintenanceHistoryCreationDTO creationDTO) {
        return plannedMaintenanceHistoryService.create(plannedMaintenanceId, creationDTO);
    }

    /**
     * Updates a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param id the ID of the planned maintenance history to update.
     * @param updateDTO the DTO to update the planned maintenance history.
     * @return the response.
     */
    @Operation(summary = "updates a planned maintenance history")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'update')")
    public Mono<PlannedMaintenanceHistoryDTO> update(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid PlannedMaintenanceHistoryUpdateDTO updateDTO) {
        return plannedMaintenanceHistoryService.update(plannedMaintenanceId, id, updateDTO);
    }

    /**
     * Patches a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param id the ID of the planned maintenance history to patch.
     * @param patchDTO the DTO to patch the planned maintenance history.
     * @return the response.
     */
    @Operation(summary = "patches a planned maintenance history")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'update')")
    public Mono<PlannedMaintenanceHistoryDTO> patch(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid PlannedMaintenanceHistoryPatchDTO patchDTO) {
        return plannedMaintenanceHistoryService.patch(plannedMaintenanceId, id, patchDTO);
    }

    /**
     * Deletes a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance identifier.
     * @param id the id of the planned maintenance history to delete.
     */
    @Operation(summary = "deletes an planned maintenance history")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#plannedMaintenanceId, 'planned-maintenance', 'update')")
    public Mono<Void> delete(@PathVariable @Identifier String plannedMaintenanceId, @PathVariable String id) {
        return plannedMaintenanceHistoryService.delete(plannedMaintenanceId, id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
