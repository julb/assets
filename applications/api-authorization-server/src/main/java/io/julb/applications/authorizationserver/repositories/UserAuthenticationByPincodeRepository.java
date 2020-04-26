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

import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The user authentication by pincode repository.
 * <P>
 * @author Julb.
 */
public interface UserAuthenticationByPincodeRepository extends MongoRepository<UserAuthenticationByPincodeEntity, String>, MongoSpecificationExecutor<UserAuthenticationByPincodeEntity> {

    /**
     * Checks if an user authentication by trademark and user and type exists.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param type the type.
     * @return <code>true</code> if the user authentication exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndUser_IdAndType(String tm, String userId, UserAuthenticationType type);

    /**
     * Checks if an user authentication by trademark and user and type exists with MFA enabled.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param type the type.
     * @return <code>true</code> if the user authentication exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndUser_IdAndTypeAndMfaEnabledIsTrue(String tm, String userId, UserAuthenticationType type);

    /**
     * Finds an user authentication by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param type the type.
     * @return the user, or <code>null</code> if not exists.
     */
    UserAuthenticationByPincodeEntity findByTmAndUser_IdAndType(String tm, String userId, UserAuthenticationType type);
}
