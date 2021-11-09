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

import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The incident service.
 * <br>
 * @author Julb.
 */
public interface IncidentComponentService {

    // ------------------------------------------ Read methods.

    /**
     * List the components impacted by the incident.
     * @param incidentId the incident identifier.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the paged list of components impacted.
     */
    Page<IncidentComponentDTO> findAll(@NotNull @Identifier String incidentId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets the component with the impact level
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @return the component with the impact level fetched.
     */
    IncidentComponentDTO findOne(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId);

    // ------------------------------------------ Write methods.

    /**
     * Creates the link between the incident and the component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @param creationDTO the DTO to create the link between the incident and the component.
     * @return the component with impact level.
     */
    IncidentComponentDTO create(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentCreationDTO creationDTO);

    /**
     * Updates the link between the incident and the component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @param updateDTO the DTO to update the link between the incident and the component.
     * @return the component with impact level.
     */
    IncidentComponentDTO update(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentUpdateDTO updateDTO);

    /**
     * Patches the link between the incident and the component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     * @param patchDTO the DTO to patch the link between the incident and the component.
     * @return the component with impact level.
     */
    IncidentComponentDTO patch(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId, @NotNull @Valid IncidentComponentPatchDTO patchDTO);

    /**
     * Unlink all components from the incident.
     * @param incidentId the incident identifier.
     */
    void delete(@NotNull @Identifier String incidentId);

    /**
     * Unlink an incident and a component.
     * @param incidentId the incident identifier.
     * @param componentId the ID of the component.
     */
    void delete(@NotNull @Identifier String incidentId, @NotNull @Identifier String componentId);

}
