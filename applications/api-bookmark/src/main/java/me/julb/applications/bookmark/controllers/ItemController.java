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

package me.julb.applications.bookmark.controllers;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

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

import me.julb.applications.bookmark.services.ItemService;
import me.julb.applications.bookmark.services.dto.ItemType;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkCreationDTO;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkDTO;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkPatchDTO;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkUpdateDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderCreationDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderPatchDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderUpdateDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkCreationDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkPatchDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkUpdateDTO;
import me.julb.library.dto.simple.identifier.IdentifierDTO;
import me.julb.library.dto.simple.value.PositiveIntegerValueDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage items of a user.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {

    /**
     * The item service.
     */
    @Autowired
    private ItemService itemService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the items of the user.
     * @param userId the user ID.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list the items of the user")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('bookmark-item', 'read')")
    public Page<? extends AbstractItemDTO> findAll(@PathVariable("userId") @Identifier String userId, Searchable searchable, Pageable pageable) {
        return itemService.findAll(userId, searchable, pageable);
    }

    /**
     * Lists the root items of the user.
     * @param userId the user ID.
     * @return the item list.
     */
    @Operation(summary = "list the root items of the user")
    @GetMapping("/root/children")
    @PreAuthorize("hasPermission('bookmark-item', 'read')")
    public List<? extends AbstractItemDTO> findAll(@PathVariable("userId") @Identifier String userId) {
        return itemService.findAllByParent(userId, null);
    }

    /**
     * Lists the child items of a folder of the user.
     * @param userId the user ID.
     * @param itemId the parent item ID.
     * @return the children item list.
     */
    @Operation(summary = "list the children of an item of the user")
    @GetMapping("/{itemId}/children")
    @PreAuthorize("hasPermission('bookmark-item', 'read')")
    public List<? extends AbstractItemDTO> findChildren(@PathVariable("userId") @Identifier String userId, @PathVariable("itemId") @Identifier String itemId) {
        return itemService.findAllByParent(userId, itemId);
    }

    /**
     * Lists the folders of the user.
     * @param userId the user ID.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list the folders of the user")
    @GetMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('bookmark-item', 'read')")
    public Page<? extends AbstractItemDTO> findAllFolders(@PathVariable("userId") @Identifier String userId, Searchable searchable, Pageable pageable) {
        return itemService.findAllByType(userId, ItemType.FOLDER, searchable, pageable);
    }

    /**
     * Lists the external links of the user.
     * @param userId the user ID.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list the external links of the user")
    @GetMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('bookmark-item', 'read')")
    public Page<? extends AbstractItemDTO> findAllExternalLinks(@PathVariable("userId") @Identifier String userId, Searchable searchable, Pageable pageable) {
        return itemService.findAllByType(userId, ItemType.EXTERNAL_LINK, searchable, pageable);
    }

    /**
     * Lists the object links of the user.
     * @param userId the user ID.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list the object links of the user")
    @GetMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('bookmark-item', 'read')")
    public Page<? extends AbstractItemDTO> findAllObjectLinks(@PathVariable("userId") @Identifier String userId, Searchable searchable, Pageable pageable) {
        return itemService.findAllByType(userId, ItemType.OBJECT_LINK, searchable, pageable);
    }

    /**
     * Finds the item by the ID of the user.
     * @param userId the user ID.
     * @param id the ID of the item to fetch.
     * @return the item fetched.
     */
    @Operation(summary = "gets the item of the user")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'read')")
    public AbstractItemDTO get(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id) {
        return itemService.findOne(userId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a folder for the user.
     * @param userId the user ID.
     * @param creationDTO the DTO to create the folder.
     * @return the created folder.
     */
    @Operation(summary = "creates a folder for the user")
    @PostMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('bookmark-item', 'create')")
    public FolderDTO create(@PathVariable("userId") @Identifier String userId, @RequestBody @NotNull @Valid FolderCreationDTO creationDTO) {
        return (FolderDTO) itemService.create(userId, creationDTO);
    }

    /**
     * Creates an external link for the user.
     * @param userId the user ID.
     * @param creationDTO the DTO to create the external link.
     * @return the created external link.
     */
    @Operation(summary = "creates an external link for the user")
    @PostMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('bookmark-item', 'create')")
    public ExternalLinkDTO create(@PathVariable("userId") @Identifier String userId, @RequestBody @NotNull @Valid ExternalLinkCreationDTO creationDTO) {
        return (ExternalLinkDTO) itemService.create(userId, creationDTO);
    }

    /**
     * Creates an object link for the user.
     * @param userId the user ID.
     * @param creationDTO the DTO to create the object link.
     * @return the created object link.
     */
    @Operation(summary = "creates an object link for the user")
    @PostMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('bookmark-item', 'create')")
    public ObjectLinkDTO create(@PathVariable("userId") @Identifier String userId, @RequestBody @NotNull @Valid ObjectLinkCreationDTO creationDTO) {
        return (ObjectLinkDTO) itemService.create(userId, creationDTO);
    }

    /**
     * Updates a folder for the user.
     * @param userId the user ID.
     * @param id the ID of the folder to update.
     * @param updateDTO the DTO to update the folder.
     * @return the response.
     */
    @Operation(summary = "updates a folder for the user")
    @PutMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public FolderDTO update(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid FolderUpdateDTO updateDTO) {
        return (FolderDTO) itemService.update(userId, id, updateDTO);
    }

    /**
     * Updates an external link for the user.
     * @param userId the user ID.
     * @param id the ID of the external link to update.
     * @param updateDTO the DTO to update the external link.
     * @return the response.
     */
    @Operation(summary = "updates an external link for the user")
    @PutMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public ExternalLinkDTO update(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ExternalLinkUpdateDTO updateDTO) {
        return (ExternalLinkDTO) itemService.update(userId, id, updateDTO);
    }

    /**
     * Updates an object link for the user.
     * @param userId the user ID.
     * @param id the ID of the object link to update.
     * @param updateDTO the DTO to update the object link.
     * @return the response.
     */
    @Operation(summary = "updates an object link for the user")
    @PutMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public ObjectLinkDTO update(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ObjectLinkUpdateDTO updateDTO) {
        return (ObjectLinkDTO) itemService.update(userId, id, updateDTO);
    }

    /**
     * Patches a folder for the user.
     * @param userId the user ID.
     * @param id the ID of the folder to patch.
     * @param patchDTO the DTO to patch the folder.
     * @return the response.
     */
    @Operation(summary = "patches a folder for the user")
    @PatchMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public FolderDTO patch(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid FolderPatchDTO patchDTO) {
        return (FolderDTO) itemService.patch(userId, id, patchDTO);
    }

    /**
     * Patches an external link for the user.
     * @param userId the user ID.
     * @param id the ID of the external link to patch.
     * @param patchDTO the DTO to patch the external link.
     * @return the response.
     */
    @Operation(summary = "patches an external link for the user")
    @PatchMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public ExternalLinkDTO patch(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ExternalLinkPatchDTO patchDTO) {
        return (ExternalLinkDTO) itemService.patch(userId, id, patchDTO);
    }

    /**
     * Patches an object link for the user.
     * @param userId the user ID.
     * @param id the ID of the object link to patch.
     * @param patchDTO the DTO to patch the object link.
     * @return the response.
     */
    @Operation(summary = "patches an object link for the user")
    @PatchMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public ObjectLinkDTO patch(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid ObjectLinkPatchDTO patchDTO) {
        return (ObjectLinkDTO) itemService.patch(userId, id, patchDTO);
    }

    /**
     * Updates the position of the item for the user.
     * @param userId the user ID.
     * @param id the ID of the object link to update.
     * @param updateDTO the DTO to update the position.
     * @return the response.
     */
    @Operation(summary = "updates the position of the item for the user")
    @PutMapping(path = "/{id}/position", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public AbstractItemDTO updatePosition(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @NotNull @Valid PositiveIntegerValueDTO updateDTO) {
        return itemService.updatePosition(userId, id, updateDTO);
    }

    /**
     * Updates the parent of the item for the user.
     * @param userId the user ID.
     * @param id the ID of the object link to update.
     * @param updateDTO the DTO to update the parent.
     * @return the response.
     */
    @Operation(summary = "updates the paarent of the item for the user")
    @PutMapping(path = "/{id}/parent", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'update')")
    public AbstractItemDTO updateParent(@PathVariable("userId") @Identifier String userId, @PathVariable @Identifier String id, @RequestBody @Valid IdentifierDTO updateDTO) {
        return itemService.updateParent(userId, id, updateDTO);
    }

    /**
     * Deletes a item for the user.
     * @param userId the user ID.
     * @param id the id of the item to delete.
     */
    @Operation(summary = "deletes an item for the user")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(#id, 'bookmark-item', 'delete')")
    public void delete(@PathVariable("userId") @Identifier String userId, @PathVariable String id) {
        itemService.delete(userId, id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
