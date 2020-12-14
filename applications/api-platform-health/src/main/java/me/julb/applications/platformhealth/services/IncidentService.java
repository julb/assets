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

import me.julb.applications.platformhealth.services.dto.incident.IncidentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The incident service.
 * <P>
 * @author Julb.
 */
public interface IncidentService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available incidents (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of incidents.
     */
    Page<IncidentDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets the incidents linked to given component (paged).
     * @param componentId the component ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of incidents.
     */
    Page<IncidentDTO> findAll(@NotNull @Identifier String componentId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a incident through its ID.
     * @param id the incident identifier.
     * @return the incident.
     */
    IncidentDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a incident.
     * @param creationDTO the DTO to create a incident.
     * @return the created incident.
     */
    IncidentDTO create(@NotNull @Valid IncidentCreationDTO creationDTO);

    /**
     * Updates a incident.
     * @param id the incident identifier.
     * @param updateDTO the DTO to update a incident.
     * @return the updated incident.
     */
    IncidentDTO update(@NotNull @Identifier String id, @NotNull @Valid IncidentUpdateDTO updateDTO);

    /**
     * Patches a incident.
     * @param id the incident identifier.
     * @param patchDTO the DTO to update a incident.
     * @return the updated incident.
     */
    IncidentDTO patch(@NotNull @Identifier String id, @NotNull @Valid IncidentPatchDTO patchDTO);

    /**
     * Deletes a incident.
     * @param id the id of the incident to delete.
     */
    void delete(@NotNull @Identifier String id);

}
