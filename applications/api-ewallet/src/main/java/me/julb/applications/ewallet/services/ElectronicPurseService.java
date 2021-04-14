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

package me.julb.applications.ewallet.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseCreationWithUserDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPursePatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The electronic purse service.
 * <P>
 * @author Julb.
 */
public interface ElectronicPurseService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the electronic purses.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the electronic purses paged list.
     */
    Page<ElectronicPurseDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a electronic purse through its ID.
     * @param id the electronic purse ID.
     * @return the electronic purse.
     */
    ElectronicPurseDTO findOne(@NotNull @Identifier String id);

    /**
     * Gets a electronic purse through its user ID.
     * @param userId the user ID.
     * @return the electronic purse.
     */
    ElectronicPurseDTO findByUserId(@NotNull @Identifier String userId);

    // ------------------------------------------ Write methods.

    /**
     * Creates a electronic purse.
     * @param electronicPurseCreationDTO the DTO to create a electronic purse.
     * @return the created electronic purse.
     */
    ElectronicPurseDTO create(@NotNull @Valid ElectronicPurseCreationWithUserDTO electronicPurseCreationDTO);

    /**
     * Refresh the balance of the electronic purse.
     * @param id the electronic purse ID.
     * @return the electronic purse updated.
     */
    ElectronicPurseDTO refreshBalance(@NotNull @Identifier String id);

    /**
     * Updates a electronic purse.
     * @param id the electronic purse ID.
     * @param electronicPurseUpdateDTO the DTO to update a electronic purse.
     * @return the updated electronic purse.
     */
    ElectronicPurseDTO update(@NotNull @Identifier String id, @NotNull @Valid ElectronicPurseUpdateDTO electronicPurseUpdateDTO);

    /**
     * Patches a electronic purse.
     * @param id the electronic purse ID.
     * @param electronicPursePatchDTO the DTO to update a electronic purse.
     * @return the updated electronic purse.
     */
    ElectronicPurseDTO patch(@NotNull @Identifier String id, @NotNull @Valid ElectronicPursePatchDTO electronicPursePatchDTO);

    /**
     * Deletes all electronic purses of a user.
     * @param id the electronic purse ID.
     */
    void delete(@NotNull @Identifier String id);
}
