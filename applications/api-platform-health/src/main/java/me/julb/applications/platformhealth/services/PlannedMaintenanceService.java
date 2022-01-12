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

package me.julb.applications.platformhealth.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenancePatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The planned maintenance service.
 * <br>
 * @author Julb.
 */
public interface PlannedMaintenanceService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available planned maintenances (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of planned maintenances.
     */
    Flux<PlannedMaintenanceDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets the planned maintenances linked to given component (paged).
     * @param componentId the component ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of planned maintenances.
     */
    Flux<PlannedMaintenanceDTO> findAll(@NotNull @Identifier String componentId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a planned maintenance through its ID.
     * @param id the planned maintenance identifier.
     * @return the planned maintenance.
     */
    Mono<PlannedMaintenanceDTO> findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a planned maintenance.
     * @param creationDTO the DTO to create a planned maintenance.
     * @return the created planned maintenance.
     */
    Mono<PlannedMaintenanceDTO> create(@NotNull @Valid PlannedMaintenanceCreationDTO creationDTO);

    /**
     * Updates a planned maintenance.
     * @param id the planned maintenance identifier.
     * @param updateDTO the DTO to update a planned maintenance.
     * @return the updated planned maintenance.
     */
    Mono<PlannedMaintenanceDTO> update(@NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceUpdateDTO updateDTO);

    /**
     * Patches a planned maintenance.
     * @param id the planned maintenance identifier.
     * @param patchDTO the DTO to update a planned maintenance.
     * @return the updated planned maintenance.
     */
    Mono<PlannedMaintenanceDTO> patch(@NotNull @Identifier String id, @NotNull @Valid PlannedMaintenancePatchDTO patchDTO);

    /**
     * Deletes a planned maintenance.
     * @param id the id of the planned maintenance to delete.
     */
    Mono<Void> delete(@NotNull @Identifier String id);

}
