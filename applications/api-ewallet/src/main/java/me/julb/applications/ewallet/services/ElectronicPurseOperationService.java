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

package me.julb.applications.ewallet.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationCreationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The electronic purse operation service.
 * <br>
 * @author Julb.
 */
public interface ElectronicPurseOperationService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the electronic purse operations.
     * @param electronicPurseId the electronic purse ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the electronic purse operations paged list.
     */
    Flux<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String electronicPurseId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets all electronic purse operations.
     * @param electronicPurseId the electronic purse ID.
     * @return the electronic purse operations list.
     */
    Flux<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String electronicPurseId);

    /**
     * Gets a electronic purse operation through its ID.
     * @param electronicPurseId the electronic purse ID.
     * @param id the electronic purse operation ID.
     * @return the electronic purse operation.
     */
    Mono<ElectronicPurseOperationDTO> findOne(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a electronic purse operation.
     * @param electronicPurseId the electronic purse ID.
     * @param electronicPurseOperationCreationDTO the DTO to create a electronic purse operation.
     * @return the created electronic purse operation.
     */
    Mono<ElectronicPurseOperationDTO> create(@NotNull @Identifier String electronicPurseId, @NotNull @Valid ElectronicPurseOperationCreationDTO electronicPurseOperationCreationDTO);

    /**
     * Updates a electronic purse operation.
     * @param electronicPurseId the electronic purse ID.
     * @param id the electronic purse operation ID.
     * @param electronicPurseOperationUpdateDTO the DTO to update a electronic purse operation.
     * @return the updated electronic purse operation.
     */
    Mono<ElectronicPurseOperationDTO> update(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationUpdateDTO electronicPurseOperationUpdateDTO);

    /**
     * Patches a electronic purse operation.
     * @param electronicPurseId the electronic purse ID.
     * @param id the electronic purse operation ID.
     * @param electronicPurseOperationPatchDTO the DTO to update a electronic purse operation.
     * @return the updated electronic purse operation.
     */
    Mono<ElectronicPurseOperationDTO> patch(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationPatchDTO electronicPurseOperationPatchDTO);

    /**
     * Cancels a purse operation.
     * @param electronicPurseId the electronic purse ID.
     * @param id the electronic purse operation ID.
     */
    Mono<Void> cancel(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id);

    /**
     * Deletes all purse operations of an electronic purse.
     * @param electronicPurseId the electronic purse ID.
     */
    Mono<Void> delete(@NotNull @Identifier String electronicPurseId);

    /**
     * Deletes a purse operation.
     * @param electronicPurseId the electronic purse ID.
     * @param id the electronic purse operation ID.
     */
    Mono<Void> delete(@NotNull @Identifier String electronicPurseId, @NotNull @Identifier String id);
}
