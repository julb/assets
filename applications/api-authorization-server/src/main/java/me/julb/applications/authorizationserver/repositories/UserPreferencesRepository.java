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

package me.julb.applications.authorizationserver.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Mono;

/**
 * The user preferences repository.
 * <br>
 * @author Julb.
 */
public interface UserPreferencesRepository extends ReactiveMongoRepository<UserPreferencesEntity, String>, MongoSpecificationExecutor<UserPreferencesEntity> {

    /**
     * Checks if an user preferences by trademark and user exists.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return <code>true</code> if the user preferences exists, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByTmAndUser_Id(String tm, String userId);

    /**
     * Finds an user preferences by trademark and user.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the user preferences, or <code>null</code> if not exists.
     */
    Mono<UserPreferencesEntity> findByTmAndUser_Id(String tm, String userId);

}
