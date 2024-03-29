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

package me.julb.applications.platformhealth.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.platformhealth.entities.PlannedMaintenanceHistoryEntity;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The planned maintenance history repository.
 * <br>
 * @author Julb.
 */
public interface PlannedMaintenanceHistoryRepository extends MongoRepository<PlannedMaintenanceHistoryEntity, String>, MongoSpecificationExecutor<PlannedMaintenanceHistoryEntity> {
    /**
     * Finds an planned maintenance history by its trademark and planned maintenance id.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @return the planned maintenance history items.
     */
    List<PlannedMaintenanceHistoryEntity> findByTmAndPlannedMaintenanceId(String tm, String plannedMaintenanceId);

    /**
     * Finds an planned maintenance history by its trademark, planned maintenance id and id.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param id the id.
     * @return the planned maintenance history entity, <code>null</code> otherwise.
     */
    PlannedMaintenanceHistoryEntity findByTmAndPlannedMaintenanceIdAndId(String tm, String plannedMaintenanceId, String id);

    /**
     * Finds the latest planned maintenance history by its trademark and planned maintenance id.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @return the latest planned maintenance history.
     */
    PlannedMaintenanceHistoryEntity findTopByTmAndPlannedMaintenanceIdOrderByCreatedAtDesc(String tm, String plannedMaintenanceId);

    /**
     * Finds the planned maintenance history by their trademark, planned maintenance and with status not set to given ones.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param statusToExclude the status to exclude.
     * @return the planned maintenance history by their trademark, planned maintenance and with status not set to given ones.
     */
    List<PlannedMaintenanceHistoryEntity> findByTmAndPlannedMaintenanceIdAndStatusNotInOrderByCreatedAtDesc(String tm, String plannedMaintenanceId, Collection<PlannedMaintenanceStatus> statusToExclude);

}
