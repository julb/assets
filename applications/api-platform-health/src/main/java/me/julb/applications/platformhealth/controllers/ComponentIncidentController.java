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

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.platformhealth.services.IncidentService;
import me.julb.applications.platformhealth.services.dto.incident.IncidentDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to return incidents linked to a component.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/components/{componentId}/incidents", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentIncidentController {

    /**
     * The incident service.
     */
    @Autowired
    private IncidentService incidentService;

    /**
     * Lists the incidents linked to the given component.
     * @param componentId the component ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the incident paged list.
     */
    @Operation(summary = "list the incidents linked to this component")
    @OpenApiSearchable
    @OpenApiPageable
    @GetMapping
    public Page<IncidentDTO> listIncidents(@PathVariable("componentId") @Identifier String componentId, Searchable searchable, Pageable pageable) {
        return incidentService.findAll(componentId, searchable, pageable);
    }
}
