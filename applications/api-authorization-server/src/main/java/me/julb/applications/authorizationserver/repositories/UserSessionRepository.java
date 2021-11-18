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

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The user session repository.
 * <br>
 * @author Julb.
 */
public interface UserSessionRepository extends MongoRepository<UserSessionEntity, String>, MongoSpecificationExecutor<UserSessionEntity> {

    /**
     * Finds an user session by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the user session, or <code>null</code> if not exists.
     */
    UserSessionEntity findByTmAndUser_IdAndId(String tm, String userId, String id);

    /**
     * Finds all user sessions by trademark and user id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return all user sessions.
     */
    List<UserSessionEntity> findByTmAndUser_Id(String tm, String userId);

}
