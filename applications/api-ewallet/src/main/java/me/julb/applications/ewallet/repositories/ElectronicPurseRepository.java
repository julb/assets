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

package me.julb.applications.ewallet.repositories;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.ewallet.entities.ElectronicPurseEntity;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Mono;

/**
 * The electronic purse repository.
 * <br>
 * @author Julb.
 */
public interface ElectronicPurseRepository extends ReactiveMongoRepository<ElectronicPurseEntity, String>, MongoSpecificationExecutor<ElectronicPurseEntity> {

    /**
     * Checks if the given user has electronic purses or not.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return <code>true</code> if the user has, at least, one electronic purse, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByTmAndUser_Id(String tm, String userId);

    /**
     * Finds an electronic purses by trademark and user id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the electronic purse, or <code>null</code> if not exists.
     */
    Mono<ElectronicPurseEntity> findByTmAndUser_Id(String tm, String userId);

    /**
     * Finds an electronic purses by trademark and id.
     * @param tm the trademark.
     * @param id the ID.
     * @return the electronic purse, or <code>null</code> if not exists.
     */
    Mono<ElectronicPurseEntity> findByTmAndId(String tm, String id);

}
