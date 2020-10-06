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

package me.julb.applications.platformhealth.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceHistoryUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The planned maintenance service.
 * <P>
 * @author Julb.
 */
public interface PlannedMaintenanceHistoryService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available planned maintenance history (paged).
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of planned maintenances.
     */
    Page<PlannedMaintenanceHistoryDTO> findAll(@NotNull @Identifier String plannedMaintenanceId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a planned maintenance history through its ID.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param id the planned maintenance history identifier.
     * @return the planned maintenance history.
     */
    PlannedMaintenanceHistoryDTO findOne(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param creationDTO the DTO to create a planned maintenance history.
     * @return the created planned maintenance history.
     */
    PlannedMaintenanceHistoryDTO create(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Valid PlannedMaintenanceHistoryCreationDTO creationDTO);

    /**
     * Updates a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param id the planned maintenance history identifier.
     * @param updateDTO the DTO to update a planned maintenance history.
     * @return the updated planned maintenance history.
     */
    PlannedMaintenanceHistoryDTO update(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceHistoryUpdateDTO updateDTO);

    /**
     * Patches a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param id the planned maintenance history identifier.
     * @param patchDTO the DTO to update a planned maintenance history.
     * @return the updated planned maintenance history.
     */
    PlannedMaintenanceHistoryDTO patch(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id, @NotNull @Valid PlannedMaintenanceHistoryPatchDTO patchDTO);

    /**
     * Deletes all history of a planned maintenance.
     * @param plannedMaintenanceId the planned maintenance ID.
     */
    void delete(@NotNull @Identifier String plannedMaintenanceId);

    /**
     * Deletes a planned maintenance history.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param id the id of the planned maintenance history to delete.
     */
    void delete(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String id);

}
