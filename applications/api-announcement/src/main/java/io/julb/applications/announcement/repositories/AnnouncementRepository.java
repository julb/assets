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

package io.julb.applications.announcement.repositories;

import io.julb.applications.announcement.entities.AnnouncementEntity;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The announcement repository.
 * <P>
 * @author Julb.
 */
public interface AnnouncementRepository extends MongoRepository<AnnouncementEntity, String>, MongoSpecificationExecutor<AnnouncementEntity> {

    /**
     * Finds an announcement by trademark and id.
     * @param tm the trademark.
     * @param id the id.
     * @return the announcement, or <code>null</code> if not exists.
     */
    AnnouncementEntity findByTmAndId(String tm, String id);

    /**
     * Checks if an announcement is already visible in the given interval.
     * @param tm the trademark.
     * @param from the from date time.
     * @param to the to date time.
     * @return <code>true</code> if an announcement exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(String tm, String from, String to);

    /**
     * Checks if an announcement not having given id is already visible in the given interval.
     * @param tm the trademark.
     * @param id the ID to exclude.
     * @param from the from date time.
     * @param to the to date time.
     * @return <code>true</code> if an announcement exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndIdNotAndVisibilityDateTime_ToGreaterThanEqualAndVisibilityDateTime_FromLessThanEqual(String tm, String id, String from, String to);

    /**
     * Finds the announcements which to date is less or equal than the given date.
     * @param dateTime the given date time.
     * @return the announcements.
     */
    List<AnnouncementEntity> findByVisibilityDateTime_ToLessThanEqualOrderByTmAsc(String dateTime);

    /**
     * Finds the announcements expired or visible at the given date.
     * @param tm the trademark.
     * @param dateTimeNow the current date time.
     * @return the announcements.
     */
    List<AnnouncementEntity> findByTmAndVisibilityDateTime_FromLessThanEqualOrderByLastUpdatedAtDesc(String tm, String dateTimeNow);

}
