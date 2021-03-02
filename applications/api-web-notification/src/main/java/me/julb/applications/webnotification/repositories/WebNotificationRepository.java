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

package me.julb.applications.webnotification.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.webnotification.entities.WebNotificationEntity;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The web notification repository.
 * <P>
 * @author Julb.
 */
public interface WebNotificationRepository extends MongoRepository<WebNotificationEntity, String>, MongoSpecificationExecutor<WebNotificationEntity> {

    /**
     * Finds a web notification by trademark and user id
     * @param tm the trademark.
     * @param userId the user ID.
     * @return the web notifications, or <code>null</code> if not exists.
     */
    List<WebNotificationEntity> findByTmAndUserId(String tm, String userId);

    /**
     * Finds a web notification by trademark, user id and id.
     * @param tm the trademark.
     * @param userId the user ID.
     * @param id the id.
     * @return the web notification, or <code>null</code> if not exists.
     */
    WebNotificationEntity findByTmAndUserIdAndId(String tm, String userId, String id);

    /**
     * Finds the web notification which expiry date time is less or equal than the given date.
     * @param dateTime the given date time.
     * @return the web notifications.
     */
    List<WebNotificationEntity> findByExpiryDateTimeLessThanEqualOrderByTmAsc(String dateTime);

}
