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

package io.julb.applications.authorizationserver.repositories;

import io.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The user preferences repository.
 * <P>
 * @author Julb.
 */
public interface UserPreferencesRepository extends MongoRepository<UserPreferencesEntity, String>, MongoSpecificationExecutor<UserPreferencesEntity> {

    /**
     * Checks if an user preferences by trademark and user exists.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return <code>true</code> if the user preferences exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndUser_Id(String tm, String userId);

    /**
     * Finds an user preferences by trademark and user.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the user preferences, or <code>null</code> if not exists.
     */
    UserPreferencesEntity findByTmAndUser_Id(String tm, String userId);

}
