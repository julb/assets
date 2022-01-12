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

package me.julb.applications.bookmark.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.bookmark.entities.AbstractItemEntity;
import me.julb.applications.bookmark.entities.FolderEntity;
import me.julb.applications.bookmark.services.dto.ItemType;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The item repository.
 * <br>
 * @author Julb.
 */
public interface ItemRepository extends ReactiveMongoRepository<AbstractItemEntity, String>, MongoSpecificationExecutor<AbstractItemEntity> {

    /**
     * Gets the item by trademark aand id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the item.
     */
    Mono<AbstractItemEntity> findByTmAndUser_IdAndId(String tm, String userId, String id);

    /**
     * Gets the item by trademark aand id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param itemType the item type.
     * @param id the id.
     * @return the item.
     */
    Mono<AbstractItemEntity> findByTmAndUser_IdAndTypeAndId(String tm, String userId, ItemType itemType, String id);

    /**
     * Finds the link without parent with the greatest position.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the link without parent with the greatest position.
     */
    Mono<AbstractItemEntity> findTopByTmAndUser_IdAndParentIsNullOrderByPositionDesc(String tm, String userId);

    /**
     * Finds the item with given parent with the greatest position.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param parent the parent folder.
     * @return the link with given parent with the greatest position.
     */
    Mono<AbstractItemEntity> findTopByTmAndUser_IdAndParentOrderByPositionDesc(String tm, String userId, FolderEntity parent);

    /**
     * Finds all items excluding given id with path starting with given path.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the ID to exclude.
     * @param path the path.
     * @return the items maatching this one.
     */
    Flux<AbstractItemEntity> findByTmAndUser_IdAndIdNotAndPathStartsWith(String tm, String userId, String id, String path);

    /**
     * Finds all items at root order by position asc.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the items.
     */
    Flux<AbstractItemEntity> findByTmAndUser_IdAndParentIsNullOrderByPositionAsc(String tm, String userId);

    /**
     * Finds all items under given parent order by position asc.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param parent the parent.
     * @return the items.
     */
    Flux<AbstractItemEntity> findByTmAndUser_IdAndParentOrderByPositionAsc(String tm, String userId, FolderEntity parent);

    /**
     * Finds all items at root order by position asc.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the ID to exclude.
     * @return the items.
     */
    Flux<AbstractItemEntity> findByTmAndUser_IdAndIdNotAndParentIsNullOrderByPositionAsc(String tm, String userId, String id);

    /**
     * Finds all items under given parent order by position asc.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the ID to exclude.
     * @param parent the parent.
     * @return the items.
     */
    Flux<AbstractItemEntity> findByTmAndUser_IdAndIdNotAndParentOrderByPositionAsc(String tm, String userId, String id, FolderEntity parent);

}
