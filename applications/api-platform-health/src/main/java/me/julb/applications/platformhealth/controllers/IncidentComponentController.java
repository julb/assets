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

import me.julb.applications.platformhealth.services.IncidentComponentService;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to manage the impact on the components of an incident.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/incidents/{incidentId}/components", produces = MediaType.APPLICATION_JSON_VALUE)
public class IncidentComponentController {

    /**
     * The incident component service.
     */
    @Autowired
    private IncidentComponentService incidentComponentService;

    // ------------------------------------------ Read methods.

    /**
     * List the components impacted by the incident.
     * @param incidentId the incident identifier.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the paged list of components impacted.
     */
    @Operation(summary = "list the components impacted by the incident")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'read')")
    public Flux<IncidentComponentDTO> findAll(@PathVariable @Identifier String incidentId, Searchable searchable, Pageable pageable) {
        return incidentComponentService.findAll(incidentId, searchable, pageable);
    }

    /**
     * Gets the component with the impact level
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @return the component with the impact level fetched.
     */
    @Operation(summary = "gets the component with the impact level")
    @GetMapping(path = "/{componentId}")
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'read')")
    public Mono<IncidentComponentDTO> get(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String componentId) {
        return incidentComponentService.findOne(incidentId, componentId);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates the link between the incident and the component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @param creationDTO the DTO to create the link between the incident and the component.
     * @return the response.
     */
    @Operation(summary = "creates the link between the incident and the component")
    @PostMapping(path = "/{componentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<IncidentComponentDTO> create(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String componentId, @RequestBody @NotNull @Valid IncidentComponentCreationDTO creationDTO) {
        return incidentComponentService.create(incidentId, componentId, creationDTO);
    }

    /**
     * Updates the link between the incident and the component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @param updateDTO the DTO to update the link between the incident and the component.
     * @return the response.
     */
    @Operation(summary = "updates the link between the incident and the component")
    @PutMapping(path = "/{componentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<IncidentComponentDTO> update(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String componentId, @RequestBody @NotNull @Valid IncidentComponentUpdateDTO updateDTO) {
        return incidentComponentService.update(incidentId, componentId, updateDTO);
    }

    /**
     * Patches the link between the incident and the component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @param patchDTO the DTO to patch the link between the incident and the component.
     * @return the response.
     */
    @Operation(summary = "patches the link between the incident and the component")
    @PatchMapping(path = "/{componentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<IncidentComponentDTO> patch(@PathVariable @Identifier String incidentId, @PathVariable @Identifier String componentId, @RequestBody @NotNull @Valid IncidentComponentPatchDTO patchDTO) {
        return incidentComponentService.patch(incidentId, componentId, patchDTO);
    }

    /**
     * Unlink an incident and a component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     */
    @Operation(summary = "unlink an incident and a component")
    @DeleteMapping(path = "/{componentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#incidentId, 'incident', 'update')")
    public Mono<Void> delete(@PathVariable @Identifier String incidentId, @PathVariable String componentId) {
        return incidentComponentService.delete(incidentId, componentId);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
