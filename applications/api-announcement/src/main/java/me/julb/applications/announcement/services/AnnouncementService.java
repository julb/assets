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

package me.julb.applications.announcement.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import me.julb.applications.announcement.services.dto.AnnouncementDTO;
import me.julb.applications.announcement.services.dto.AnnouncementPatchDTO;
import me.julb.applications.announcement.services.dto.AnnouncementUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The announcement service.
 * <br>
 * @author Julb.
 */
public interface AnnouncementService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available announcements (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of announcements.
     */
    Flux<AnnouncementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a announcement through its ID.
     * @param id the announcement identifier.
     * @return the announcement.
     */
    Mono<AnnouncementDTO> findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a announcement.
     * @param announcementCreationDTO the DTO to create a announcement.
     * @return the created announcement.
     */
    Mono<AnnouncementDTO> create(@NotNull @Valid AnnouncementCreationDTO announcementCreationDTO);

    /**
     * Updates a announcement.
     * @param id the announcement identifier.
     * @param announcementUpdateDTO the DTO to update a announcement.
     * @return the updated announcement.
     */
    Mono<AnnouncementDTO> update(@NotNull @Identifier String id, @NotNull @Valid AnnouncementUpdateDTO announcementUpdateDTO);

    /**
     * Patches a announcement.
     * @param id the announcement identifier.
     * @param announcementPatchDTO the DTO to update a announcement.
     * @return the updated announcement.
     */
    Mono<AnnouncementDTO> patch(@NotNull @Identifier String id, @NotNull @Valid AnnouncementPatchDTO announcementPatchDTO);

    /**
     * Deletes a announcement.
     * @param id the id of the announcement to delete.
     */
    Mono<Void> delete(@NotNull @Identifier String id);

}
