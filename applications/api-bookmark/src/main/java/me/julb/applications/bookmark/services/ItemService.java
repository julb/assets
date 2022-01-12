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

package me.julb.applications.bookmark.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.bookmark.services.dto.ItemType;
import me.julb.applications.bookmark.services.dto.item.AbstractItemCreationDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemPatchDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemUpdateDTO;
import me.julb.library.dto.simple.identifier.IdentifierDTO;
import me.julb.library.dto.simple.value.PositiveIntegerValueDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The item service.
 * <br>
 * @author Julb.
 */
public interface ItemService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available items (paged).
     * @param userId current user ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of items.
     */
    Flux<? extends AbstractItemDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets the available folders (paged).
     * @param userId current user ID.
     * @param type the type.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of items.
     */
    Flux<? extends AbstractItemDTO> findAllByType(@NotNull @Identifier String userId, @NotNull ItemType type, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Lists all child items by parent folder.
     * @param userId current user ID.
     * @param parentId the parent ID, or <code>null</code> if we need to list at root.
     * @return the children.
     */
    Flux<? extends AbstractItemDTO> findAllByParent(@NotNull @Identifier String userId, @Identifier String parentId);

    /**
     * Gets a item through its ID.
     * @param userId current user ID.
     * @param id the item identifier.
     * @return the item.
     */
    Mono<AbstractItemDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a item.
     * @param userId current user ID.
     * @param creationDTO the DTO to create a item.
     * @return the created item.
     */
    Mono<AbstractItemDTO> create(@NotNull @Identifier String userId, @NotNull @Valid AbstractItemCreationDTO creationDTO);

    /**
     * Updates a item.
     * @param userId current user ID.
     * @param id the item identifier.
     * @param updateDTO the DTO to update a item.
     * @return the updated item.
     */
    Mono<AbstractItemDTO> update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid AbstractItemUpdateDTO updateDTO);

    /**
     * Patches a item.
     * @param userId current user ID.
     * @param id the item identifier.
     * @param patchDTO the DTO to update a item.
     * @return the updated item.
     */
    Mono<AbstractItemDTO> patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid AbstractItemPatchDTO patchDTO);

    /**
     * Updates the position of an item.
     * @param userId current user ID.
     * @param id the ID.
     * @param updateDTO the position to set.
     * @return the item updated.
     */
    Mono<AbstractItemDTO> updatePosition(@NotNull @Identifier String userId, @Identifier String id, @NotNull @Valid PositiveIntegerValueDTO updateDTO);

    /**
     * Updates the parent of an item.
     * @param userId current user ID.
     * @param id the ID.
     * @param updateDTO the parent folder to set.
     * @return the item updated.
     */
    Mono<AbstractItemDTO> updateParent(@NotNull @Identifier String userId, @Identifier String id, @Valid IdentifierDTO updateDTO);

    /**
     * Deletes a item.
     * @param userId current user ID.
     * @param id the id of the item to delete.
     */
    Mono<Void> delete(@NotNull @Identifier String userId, @NotNull @Identifier String id);
}
