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

package me.julb.applications.announcement.controllers;

import io.swagger.v3.oas.annotations.Operation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.announcement.services.AnnouncementService;
import me.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import me.julb.applications.announcement.services.dto.AnnouncementDTO;
import me.julb.applications.announcement.services.dto.AnnouncementPatchDTO;
import me.julb.applications.announcement.services.dto.AnnouncementUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage announcements.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/announcements", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnnouncementController {

    /**
     * The announcement service.
     */
    @Autowired
    private AnnouncementService announcementService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the announcements.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the announcement paged list.
     */
    @Operation(summary = "list announcements")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('announcement', 'read')")
    public Page<AnnouncementDTO> findAll(Searchable searchable, Pageable pageable) {
        return announcementService.findAll(searchable, pageable);
    }

    /**
     * Finds a announcement by its ID.
     * @param id the ID of the announcement to fetch.
     * @return the announcement fetched.
     */
    @Operation(summary = "gets a announcement")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'announcement', 'read')")
    public AnnouncementDTO get(@PathVariable @Identifier String id) {
        return announcementService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an announcement.
     * @param creationDTO the DTO to create the announcement.
     * @return the created announcement.
     */
    @Operation(summary = "creates an announcement")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('announcement', 'create')")
    public AnnouncementDTO create(@RequestBody @NotNull @Valid AnnouncementCreationDTO creationDTO) {
        return announcementService.create(creationDTO);
    }

    /**
     * Updates a announcement.
     * @param id the ID of the announcement to update.
     * @param updateDTO the DTO to update the announcement.
     * @return the response.
     */
    @Operation(summary = "updates a announcement")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'announcement', 'update')")
    public AnnouncementDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid AnnouncementUpdateDTO updateDTO) {
        return announcementService.update(id, updateDTO);
    }

    /**
     * Patches a announcement.
     * @param id the ID of the announcement to patch.
     * @param patchDTO the DTO to patch the announcement.
     * @return the response.
     */
    @Operation(summary = "patches a announcement")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'announcement', 'update')")
    public AnnouncementDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid AnnouncementPatchDTO patchDTO) {
        return announcementService.patch(id, patchDTO);
    }

    /**
     * Deletes a announcement.
     * @param id the id of the announcement to delete.
     */
    @Operation(summary = "deletes an announcement")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'announcement', 'delete')")
    public void delete(@PathVariable String id) {
        announcementService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
