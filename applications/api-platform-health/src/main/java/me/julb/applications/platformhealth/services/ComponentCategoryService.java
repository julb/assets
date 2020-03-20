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

import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The component category service.
 * <P>
 * @author Julb.
 */
public interface ComponentCategoryService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available component categories (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of component categories.
     */
    Page<ComponentCategoryDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a component category through its ID.
     * @param id the component category identifier.
     * @return the component category.
     */
    ComponentCategoryDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a component category.
     * @param creationDTO the DTO to create a component category.
     * @return the created component category.
     */
    ComponentCategoryDTO create(@NotNull @Valid ComponentCategoryCreationDTO creationDTO);

    /**
     * Updates a component category.
     * @param id the component category identifier.
     * @param updateDTO the DTO to update a component category.
     * @return the updated component category.
     */
    ComponentCategoryDTO update(@NotNull @Identifier String id, @NotNull @Valid ComponentCategoryUpdateDTO updateDTO);

    /**
     * Patches a component category.
     * @param id the component category identifier.
     * @param patchDTO the DTO to update a component category.
     * @return the updated component category.
     */
    ComponentCategoryDTO patch(@NotNull @Identifier String id, @NotNull @Valid ComponentCategoryPatchDTO patchDTO);

    /**
     * Deletes a component category.
     * @param id the id of the component category to delete.
     */
    void delete(@NotNull @Identifier String id);

}
