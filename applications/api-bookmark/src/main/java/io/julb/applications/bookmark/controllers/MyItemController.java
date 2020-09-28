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

package io.julb.applications.bookmark.controllers;

import io.julb.applications.bookmark.services.MyItemService;
import io.julb.applications.bookmark.services.dto.ItemType;
import io.julb.applications.bookmark.services.dto.externallink.ExternalLinkCreationDTO;
import io.julb.applications.bookmark.services.dto.externallink.ExternalLinkDTO;
import io.julb.applications.bookmark.services.dto.externallink.ExternalLinkPatchDTO;
import io.julb.applications.bookmark.services.dto.externallink.ExternalLinkUpdateDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderCreationDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderPatchDTO;
import io.julb.applications.bookmark.services.dto.folder.FolderUpdateDTO;
import io.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import io.julb.applications.bookmark.services.dto.object.ObjectLinkCreationDTO;
import io.julb.applications.bookmark.services.dto.object.ObjectLinkDTO;
import io.julb.applications.bookmark.services.dto.object.ObjectLinkPatchDTO;
import io.julb.applications.bookmark.services.dto.object.ObjectLinkUpdateDTO;
import io.julb.library.dto.simple.identifier.IdentifierDTO;
import io.julb.library.dto.simple.value.PositiveIntegerValueDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;
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

/**
 * The rest controller to manage items.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/items", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyItemController {

    /**
     * The item service.
     */
    @Autowired
    private MyItemService myItemService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the items.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list my items")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<? extends AbstractItemDTO> findAll(Searchable searchable, Pageable pageable) {
        return myItemService.findAll(searchable, pageable);
    }

    /**
     * Lists the root items.
     * @return the item list.
     */
    @Operation(summary = "list my root items")
    @GetMapping("/root/children")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public List<? extends AbstractItemDTO> findAll() {
        return myItemService.findAllByParent(null);
    }

    /**
     * Lists the child items of a folder.
     * @param itemId the parent item ID.
     * @return the children item list.
     */
    @Operation(summary = "list the children of an item")
    @GetMapping("/{itemId}/children")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public List<? extends AbstractItemDTO> findChildren(@PathVariable("itemId") @Identifier String itemId) {
        return myItemService.findAllByParent(itemId);
    }

    /**
     * Lists the folders.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list my folders")
    @GetMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<? extends AbstractItemDTO> findAllFolders(Searchable searchable, Pageable pageable) {
        return myItemService.findAllByType(ItemType.FOLDER, searchable, pageable);
    }

    /**
     * Lists the external links.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list my external links")
    @GetMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<? extends AbstractItemDTO> findAllExternalLinks(Searchable searchable, Pageable pageable) {
        return myItemService.findAllByType(ItemType.EXTERNAL_LINK, searchable, pageable);
    }

    /**
     * Lists the object links.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the item paged list.
     */
    @Operation(summary = "list my object links")
    @GetMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<? extends AbstractItemDTO> findAllObjectLinks(Searchable searchable, Pageable pageable) {
        return myItemService.findAllByType(ItemType.OBJECT_LINK, searchable, pageable);
    }

    /**
     * Finds a item by its ID.
     * @param id the ID of the item to fetch.
     * @return the item fetched.
     */
    @Operation(summary = "gets a item")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public AbstractItemDTO get(@PathVariable @Identifier String id) {
        return myItemService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a folder.
     * @param creationDTO the DTO to create the folder.
     * @return the created folder.
     */
    @Operation(summary = "creates a folder")
    @PostMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public FolderDTO create(@RequestBody @NotNull @Valid FolderCreationDTO creationDTO) {
        return (FolderDTO) myItemService.create(creationDTO);
    }

    /**
     * Creates an external link.
     * @param creationDTO the DTO to create the external link.
     * @return the created external link.
     */
    @Operation(summary = "creates an external link")
    @PostMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public ExternalLinkDTO create(@RequestBody @NotNull @Valid ExternalLinkCreationDTO creationDTO) {
        return (ExternalLinkDTO) myItemService.create(creationDTO);
    }

    /**
     * Creates an object link.
     * @param creationDTO the DTO to create the object link.
     * @return the created object link.
     */
    @Operation(summary = "creates an object link")
    @PostMapping(consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public ObjectLinkDTO create(@RequestBody @NotNull @Valid ObjectLinkCreationDTO creationDTO) {
        return (ObjectLinkDTO) myItemService.create(creationDTO);
    }

    /**
     * Updates a folder.
     * @param id the ID of the folder to update.
     * @param updateDTO the DTO to update the folder.
     * @return the response.
     */
    @Operation(summary = "updates a folder")
    @PutMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public FolderDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid FolderUpdateDTO updateDTO) {
        return (FolderDTO) myItemService.update(id, updateDTO);
    }

    /**
     * Updates an external link.
     * @param id the ID of the external link to update.
     * @param updateDTO the DTO to update the external link.
     * @return the response.
     */
    @Operation(summary = "updates an external link")
    @PutMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public ExternalLinkDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ExternalLinkUpdateDTO updateDTO) {
        return (ExternalLinkDTO) myItemService.update(id, updateDTO);
    }

    /**
     * Updates an object link.
     * @param id the ID of the object link to update.
     * @param updateDTO the DTO to update the object link.
     * @return the response.
     */
    @Operation(summary = "updates an object link")
    @PutMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public ObjectLinkDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ObjectLinkUpdateDTO updateDTO) {
        return (ObjectLinkDTO) myItemService.update(id, updateDTO);
    }

    /**
     * Patches a folder.
     * @param id the ID of the folder to patch.
     * @param patchDTO the DTO to patch the folder.
     * @return the response.
     */
    @Operation(summary = "patches a folder")
    @PatchMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_FOLDER_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public FolderDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid FolderPatchDTO patchDTO) {
        return (FolderDTO) myItemService.patch(id, patchDTO);
    }

    /**
     * Patches an external link.
     * @param id the ID of the external link to patch.
     * @param patchDTO the DTO to patch the external link.
     * @return the response.
     */
    @Operation(summary = "patches an external link")
    @PatchMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_EXTERNAL_LINK_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public ExternalLinkDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ExternalLinkPatchDTO patchDTO) {
        return (ExternalLinkDTO) myItemService.patch(id, patchDTO);
    }

    /**
     * Patches an object link.
     * @param id the ID of the object link to patch.
     * @param patchDTO the DTO to patch the object link.
     * @return the response.
     */
    @Operation(summary = "patches an object link")
    @PatchMapping(path = "/{id}", consumes = CustomMediaType.APPLICATION_VND_BOOKMARK_OBJECT_LINK_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public ObjectLinkDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid ObjectLinkPatchDTO patchDTO) {
        return (ObjectLinkDTO) myItemService.patch(id, patchDTO);
    }

    /**
     * Updates the position of the item.
     * @param id the ID of the object link to update.
     * @param updateDTO the DTO to update the position.
     * @return the response.
     */
    @Operation(summary = "updates the position of the item")
    @PutMapping(path = "/{id}/position", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public AbstractItemDTO updatePosition(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid PositiveIntegerValueDTO updateDTO) {
        return myItemService.updatePosition(id, updateDTO);
    }

    /**
     * Updates the parent of the item.
     * @param id the ID of the object link to update.
     * @param updateDTO the DTO to update the parent.
     * @return the response.
     */
    @Operation(summary = "updates the paarent of the item")
    @PutMapping(path = "/{id}/parent", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public AbstractItemDTO updateParent(@PathVariable @Identifier String id, @RequestBody @Valid IdentifierDTO updateDTO) {
        return myItemService.updateParent(id, updateDTO);
    }

    /**
     * Deletes a item.
     * @param id the id of the item to delete.
     */
    @Operation(summary = "deletes an item")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete(@PathVariable String id) {
        myItemService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
