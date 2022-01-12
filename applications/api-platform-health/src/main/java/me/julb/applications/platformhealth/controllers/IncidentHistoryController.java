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

import me.julb.applications.platformhealth.services.IncidentHistoryService;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage history of an incident.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/incidents/{incidentId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
public class IncidentHistoryController {

    /**
     * The incident history service.
     */
    @Autowired
    private IncidentHistoryService incidentHistoryService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the incident history.
     * @param incidentId the incident identifier.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the incident history paged list.
     */
    @Operation(summary = "list incident history")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'read')")
    public Flux<IncidentHistoryDTO> findAll(@PathVariable @Identifier String incidentId, Searchable searchable, Pageable pageable) {
        return incidentHistoryService.findAll(incidentId, searchable, pageable);
    }

    /**
     * Finds a incident history by its ID.
     * @param incidentId the incident identifier.
     * @param id the ID of the incident history to fetch.
     * @return the incident history fetched.
     */
    @Operation(summary = "gets an history of an incident")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'read')")
    public Mono<IncidentHistoryDTO> get(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String id) {
        return incidentHistoryService.findOne(incidentId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an incident history.
     * @param incidentId the incident identifier.
     * @param creationDTO the DTO to create the incident history.
     * @return the created incident history.
     */
    @Operation(summary = "creates an incident history")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<IncidentHistoryDTO> create(@PathVariable @Identifier String incidentId, @RequestBody @NotNull @Valid IncidentHistoryCreationDTO creationDTO) {
        return incidentHistoryService.create(incidentId, creationDTO);
    }

    /**
     * Updates a incident history.
     * @param incidentId the incident identifier.
     * @param id the ID of the incident history to update.
     * @param updateDTO the DTO to update the incident history.
     * @return the response.
     */
    @Operation(summary = "updates a incident history")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<IncidentHistoryDTO> update(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid IncidentHistoryUpdateDTO updateDTO) {
        return incidentHistoryService.update(incidentId, id, updateDTO);
    }

    /**
     * Patches a incident history.
     * @param incidentId the incident identifier.
     * @param id the ID of the incident history to patch.
     * @param patchDTO the DTO to patch the incident history.
     * @return the response.
     */
    @Operation(summary = "patches a incident history")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<IncidentHistoryDTO> patch(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid IncidentHistoryPatchDTO patchDTO) {
        return incidentHistoryService.patch(incidentId, id, patchDTO);
    }

    /**
     * Deletes a incident history.
     * @param incidentId the incident identifier.
     * @param id the id of the incident history to delete.
     */
    @Operation(summary = "deletes an incident history")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<Void> delete(@PathVariable @Identifier String incidentId, @PathVariable String id) {
        return incidentHistoryService.delete(incidentId, id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
