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

import me.julb.applications.platformhealth.services.IncidentService;
import me.julb.applications.platformhealth.services.dto.incident.IncidentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage incidents.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/incidents", produces = MediaType.APPLICATION_JSON_VALUE)
public class IncidentController {

    /**
     * The incident service.
     */
    @Autowired
    private IncidentService incidentService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the incidents.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the incident paged list.
     */
    @Operation(summary = "list incidents")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('incident', 'read')")
    public Flux<IncidentDTO> findAll(Searchable searchable, Pageable pageable) {
        return incidentService.findAll(searchable, pageable);
    }

    /**
     * Finds a incident by its ID.
     * @param id the ID of the incident to fetch.
     * @return the incident fetched.
     */
    @Operation(summary = "gets a incident")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'incident', 'read')")
    public Mono<IncidentDTO> get(@PathVariable @Identifier String id) {
        return incidentService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an incident.
     * @param creationDTO the DTO to create the incident.
     * @return the created incident.
     */
    @Operation(summary = "creates an incident")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('incident', 'create')")
    public Mono<IncidentDTO> create(@RequestBody @NotNull @Valid IncidentCreationDTO creationDTO) {
        return incidentService.create(creationDTO);
    }

    /**
     * Updates a incident.
     * @param id the ID of the incident to update.
     * @param updateDTO the DTO to update the incident.
     * @return the response.
     */
    @Operation(summary = "updates a incident")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'incident', 'update')")
    public Mono<IncidentDTO> update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid IncidentUpdateDTO updateDTO) {
        return incidentService.update(id, updateDTO);
    }

    /**
     * Patches a incident.
     * @param id the ID of the incident to patch.
     * @param patchDTO the DTO to patch the incident.
     * @return the response.
     */
    @Operation(summary = "patches a incident")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'incident', 'update')")
    public Mono<IncidentDTO> patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid IncidentPatchDTO patchDTO) {
        return incidentService.patch(id, patchDTO);
    }

    /**
     * Deletes a incident.
     * @param id the id of the incident to delete.
     */
    @Operation(summary = "deletes an incident")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'incident', 'delete')")
    public Mono<Void> delete(@PathVariable String id) {
        return incidentService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
