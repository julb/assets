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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentHistoryUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The incident service.
 * <br>
 * @author Julb.
 */
public interface IncidentHistoryService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available incident history (paged).
     * @param incidentId the incident ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of incidents.
     */
    Page<IncidentHistoryDTO> findAll(@NotNull @Identifier String incidentId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a incident history through its ID.
     * @param incidentId the incident ID.
     * @param id the incident history identifier.
     * @return the incident history.
     */
    IncidentHistoryDTO findOne(@NotNull @Identifier String incidentId, @NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a incident history.
     * @param incidentId the incident ID.
     * @param creationDTO the DTO to create a incident history.
     * @return the created incident history.
     */
    IncidentHistoryDTO create(@NotNull @Identifier String incidentId, @NotNull @Valid IncidentHistoryCreationDTO creationDTO);

    /**
     * Updates a incident history.
     * @param incidentId the incident ID.
     * @param id the incident history identifier.
     * @param updateDTO the DTO to update a incident history.
     * @return the updated incident history.
     */
    IncidentHistoryDTO update(@NotNull @Identifier String incidentId, @NotNull @Identifier String id, @NotNull @Valid IncidentHistoryUpdateDTO updateDTO);

    /**
     * Patches a incident history.
     * @param incidentId the incident ID.
     * @param id the incident history identifier.
     * @param patchDTO the DTO to update a incident history.
     * @return the updated incident history.
     */
    IncidentHistoryDTO patch(@NotNull @Identifier String incidentId, @NotNull @Identifier String id, @NotNull @Valid IncidentHistoryPatchDTO patchDTO);

    /**
     * Deletes all incident history.
     * @param incidentId the incident ID.
     */
    void delete(@NotNull @Identifier String incidentId);

    /**
     * Deletes a incident history.
     * @param incidentId the incident ID.
     * @param id the id of the incident history to delete.
     */
    void delete(@NotNull @Identifier String incidentId, @NotNull @Identifier String id);

}
