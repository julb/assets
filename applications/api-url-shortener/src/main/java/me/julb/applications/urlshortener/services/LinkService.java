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

package me.julb.applications.urlshortener.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.urlshortener.services.dto.LinkCreationDTO;
import me.julb.applications.urlshortener.services.dto.LinkDTO;
import me.julb.applications.urlshortener.services.dto.LinkPatchDTO;
import me.julb.applications.urlshortener.services.dto.LinkUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The host service.
 * <br>
 * @author Julb.
 */
public interface LinkService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available links (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of links.
     */
    Flux<LinkDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a link through its ID.
     * @param id the link identifier.
     * @return the link.
     */
    Mono<LinkDTO> findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a link.
     * @param linkCreationDTO the DTO to create a link.
     * @return the created link.
     */
    Mono<LinkDTO> create(@NotNull @Valid LinkCreationDTO linkCreationDTO);

    /**
     * Increments the number of hits of a link.
     * @param id the link identifier.
     * @return the updated link.
     */
    Mono<LinkDTO> incrementNumberOfHits(@NotNull @Identifier String id);

    /**
     * Resets the number of hits of a link.
     * @param id the link identifier.
     * @return the updated link.
     */
    Mono<LinkDTO> resetNumberOfHits(@NotNull @Identifier String id);

    /**
     * Updates a link.
     * @param id the link identifier.
     * @param linkUpdateDTO the DTO to update a link.
     * @return the updated link.
     */
    Mono<LinkDTO> update(@NotNull @Identifier String id, @NotNull @Valid LinkUpdateDTO linkUpdateDTO);

    /**
     * Patches a link.
     * @param id the link identifier.
     * @param linkPatchDTO the DTO to update a link.
     * @return the updated link.
     */
    Mono<LinkDTO> patch(@NotNull @Identifier String id, @NotNull @Valid LinkPatchDTO linkPatchDTO);

    /**
     * Deletes a link.
     * @param id the id of the link to delete.
     */
    Mono<Void> delete(@NotNull @Identifier String id);

}
