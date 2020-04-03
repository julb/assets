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

package io.julb.applications.announcement.services;

import io.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import io.julb.applications.announcement.services.dto.AnnouncementDTO;
import io.julb.applications.announcement.services.dto.AnnouncementPatchDTO;
import io.julb.applications.announcement.services.dto.AnnouncementUpdateDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The announcement service.
 * <P>
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
    Page<AnnouncementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a announcement through its ID.
     * @param id the announcement identifier.
     * @return the announcement.
     */
    AnnouncementDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a announcement.
     * @param announcementCreationDTO the DTO to create a announcement.
     * @return the created announcement.
     */
    AnnouncementDTO create(@NotNull @Valid AnnouncementCreationDTO announcementCreationDTO);

    /**
     * Updates a announcement.
     * @param id the announcement identifier.
     * @param announcementUpdateDTO the DTO to update a announcement.
     * @return the updated announcement.
     */
    AnnouncementDTO update(@NotNull @Identifier String id, @NotNull @Valid AnnouncementUpdateDTO announcementUpdateDTO);

    /**
     * Patches a announcement.
     * @param id the announcement identifier.
     * @param announcementPatchDTO the DTO to update a announcement.
     * @return the updated announcement.
     */
    AnnouncementDTO patch(@NotNull @Identifier String id, @NotNull @Valid AnnouncementPatchDTO announcementPatchDTO);

    /**
     * Deletes a announcement.
     * @param id the id of the announcement to delete.
     */
    void delete(@NotNull @Identifier String id);

}
