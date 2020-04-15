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

package io.julb.applications.platformhealth.services;

import io.julb.applications.platformhealth.services.dto.component.ComponentCreationDTO;
import io.julb.applications.platformhealth.services.dto.component.ComponentDTO;
import io.julb.applications.platformhealth.services.dto.component.ComponentPatchDTO;
import io.julb.applications.platformhealth.services.dto.component.ComponentUpdateDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The component service.
 * <P>
 * @author Julb.
 */
public interface ComponentService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available component of a component category (paged).
     * @param componentCategoryId the component category ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of incidents.
     */
    Page<ComponentDTO> findAll(@NotNull @Identifier String componentCategoryId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a incident through its ID.
     * @param componentCategoryId the component category ID.
     * @param id the component identifier.
     * @return the incident.
     */
    ComponentDTO findOne(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a component of a component category.
     * @param componentCategoryId the component category ID.
     * @param creationDTO the DTO to create a component.
     * @return the created component.
     */
    ComponentDTO create(@NotNull @Identifier String componentCategoryId, @NotNull @Valid ComponentCreationDTO creationDTO);

    /**
     * Updates a component.
     * @param componentCategoryId the component category ID.
     * @param id the componentidentifier.
     * @param updateDTO the DTO to update a component.
     * @return the updated component.
     */
    ComponentDTO update(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id, @NotNull @Valid ComponentUpdateDTO updateDTO);

    /**
     * Patches a component.
     * @param componentCategoryId the component category ID.
     * @param id the component identifier.
     * @param patchDTO the DTO to update a component.
     * @return the updated component.
     */
    ComponentDTO patch(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id, @NotNull @Valid ComponentPatchDTO patchDTO);

    /**
     * Deletes all component of a component category.
     * @param componentCategoryId the component category ID.
     */
    void delete(@NotNull @Identifier String componentCategoryId);

    /**
     * Deletes a component.
     * @param componentCategoryId the component category ID.
     * @param id the id of the component to delete.
     */
    void delete(@NotNull @Identifier String componentCategoryId, @NotNull @Identifier String id);

}
