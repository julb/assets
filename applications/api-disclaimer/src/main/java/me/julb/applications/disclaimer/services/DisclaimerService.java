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

package me.julb.applications.disclaimer.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerCreationDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerPatchDTO;
import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The disclaimer service.
 * <br>
 * @author Julb.
 */
public interface DisclaimerService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available disclaimers (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of disclaimers.
     */
    Flux<DisclaimerDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a disclaimer through its ID.
     * @param id the disclaimer identifier.
     * @return the disclaimer.
     */
    Mono<DisclaimerDTO> findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a disclaimer.
     * @param disclaimerCreationDTO the DTO to create a disclaimer.
     * @return the created disclaimer.
     */
    Mono<DisclaimerDTO> create(@NotNull @Valid DisclaimerCreationDTO disclaimerCreationDTO);

    /**
     * Updates a disclaimer.
     * @param id the disclaimer identifier.
     * @param disclaimerUpdateDTO the DTO to update a disclaimer.
     * @return the updated disclaimer.
     */
    Mono<DisclaimerDTO> update(@NotNull @Identifier String id, @NotNull @Valid DisclaimerUpdateDTO disclaimerUpdateDTO);

    /**
     * Publishes a disclaimer.
     * @param id the disclaimer identifier.
     * @return the published disclaimer.
     */
    Mono<DisclaimerDTO> publish(@NotNull @Identifier String id);

    /**
     * Unpublishes a disclaimer.
     * @param id the disclaimer identifier.
     * @return the unpublished disclaimer.
     */
    Mono<DisclaimerDTO> unpublish(@NotNull @Identifier String id);

    /**
     * Patches a disclaimer.
     * @param id the disclaimer identifier.
     * @param disclaimerPatchDTO the DTO to update a disclaimer.
     * @return the updated disclaimer.
     */
    Mono<DisclaimerDTO> patch(@NotNull @Identifier String id, @NotNull @Valid DisclaimerPatchDTO disclaimerPatchDTO);

    /**
     * Deletes a disclaimer.
     * @param id the id of the disclaimer to delete.
     * @return the void result.
     */
    Mono<Void> delete(@NotNull @Identifier String id);

}
