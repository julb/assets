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

import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The user mail repository.
 * <br>
 * @author Julb.
 */
public interface UserMailRepository extends MongoRepository<UserMailEntity, String>, MongoSpecificationExecutor<UserMailEntity> {

    /**
     * Finds an user primary mail by trademark and user ID.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the user mail, or <code>null</code> if not exists.
     */
    UserMailEntity findByTmAndUser_IdAndPrimaryIsTrue(String tm, String userId);

    /**
     * Finds an user mail by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the user mail, or <code>null</code> if not exists.
     */
    UserMailEntity findByTmAndUser_IdAndId(String tm, String userId, String id);

    /**
     * Finds an user mail by trademark and mail address.
     * @param tm the trademark.
     * @param mail the mail address.
     * @return the user mail.
     */
    UserMailEntity findByTmAndMailIgnoreCase(String tm, String mail);

    /**
     * Check existence of an user mail by trademark and mail address.
     * @param tm the trademark.
     * @param mail the mail address.
     * @return <code>true</code> if the mail exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndMailIgnoreCase(String tm, String mail);

    /**
     * Finds an user mail by trademark and mail address.
     * @param tm the trademark.
     * @param mail the mail address.
     * @return the user mail.
     */
    UserMailEntity findByTmAndMailIgnoreCaseAndVerifiedIsTrue(String tm, String mail);

    /**
     * Finds verified user mails by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the verified user mails, or <code>null</code> if not exists.
     */
    List<UserMailEntity> findByTmAndUser_IdAndVerifiedIsTrue(String tm, String userId);

    /**
     * Finds an user mail by trademark and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the user mail, or <code>null</code> if not exists.
     */
    UserMailEntity findByTmAndUser_IdAndIdAndVerifiedIsTrue(String tm, String userId, String id);
}
