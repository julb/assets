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

import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByApiKeyEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Mono;

/**
 * The user authentication by api key repository.
 * <br>
 * @author Julb.
 */
public interface UserAuthenticationByApiKeyRepository extends ReactiveMongoRepository<UserAuthenticationByApiKeyEntity, String>, MongoSpecificationExecutor<UserAuthenticationByApiKeyEntity> {

    /**
     * Checks if an user authentication by trademark and user and type exists.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param type the type.
     * @param name the name.
     * @return <code>true</code> if the user authentication exists, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByTmAndUser_IdAndTypeAndNameIgnoreCase(String tm, String userId, UserAuthenticationType type, String name);

    /**
     * Checks if an user authentication by trademark and user and type exists.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param type the type.
     * @param id the id to exclude.
     * @param name the name.
     * @return <code>true</code> if the user authentication exists, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByTmAndUser_IdAndTypeAndIdNotAndNameIgnoreCase(String tm, String userId, UserAuthenticationType type, String id, String name);

    /**
     * Finds an user authentication by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param type the type.
     * @param id the ID.
     * @return the user, or <code>null</code> if not exists.
     */
    Mono<UserAuthenticationByApiKeyEntity> findByTmAndUser_IdAndTypeAndId(String tm, String userId, UserAuthenticationType type, String id);
}
