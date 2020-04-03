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
import io.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;
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

/**
 * The rest controller to return the planned maintenances linked to a component.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/components/{componentId}/planned-maintenances", produces = MediaType.APPLICATION_JSON_VALUE)
public class ComponentPlannedMaintenanceController {

    /**
     * The planned maintenance service.
     */
    @Autowired
    private PlannedMaintenanceService plannedMaintenanceService;

    /**
     * Lists the planned maintenances linked to the given component.
     * @param componentId the component ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the planned maintenance paged list.
     */
    @Operation(summary = "list the planned maintenances linked to this component")
    @OpenApiSearchable
    @OpenApiPageable
    @GetMapping
    public Page<PlannedMaintenanceDTO> listPlannedMaintenances(@PathVariable("componentId") @Identifier String componentId, Searchable searchable, Pageable pageable) {
        return plannedMaintenanceService.findAll(componentId, searchable, pageable);
    }
}
