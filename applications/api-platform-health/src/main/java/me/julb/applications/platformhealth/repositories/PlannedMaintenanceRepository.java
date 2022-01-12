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

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.platformhealth.entities.PlannedMaintenanceEntity;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The planned maintenance repository.
 * <br>
 * @author Julb.
 */
public interface PlannedMaintenanceRepository extends ReactiveMongoRepository<PlannedMaintenanceEntity, String>, MongoSpecificationExecutor<PlannedMaintenanceEntity> {

    /**
     * Finds an planned maintenance by trademark and id.
     * @param tm the trademark.
     * @param id the id.
     * @return the planned maintenance, or <code>null</code> if not exists.
     */
    Mono<PlannedMaintenanceEntity> findByTmAndId(String tm, String id);

    /**
     * Finds the planned maintenances having given status.
     * @param tm the trademark.
     * @param status the status to filter on.
     * @return the planned maintenances matching the given status.
     */
    Flux<PlannedMaintenanceEntity> findByTmAndStatusIn(String tm, Collection<PlannedMaintenanceStatus> status);

    /**
     * Finds the planned maintenances created after given date and not having given status.
     * @param tm the trademark.
     * @param dateTimeThreshold the created-at date time threshold.
     * @param status the status to exclude.
     * @return the planned maintenances.
     */
    Flux<PlannedMaintenanceEntity> findByTmAndLastUpdatedAtGreaterThanEqualAndStatusNotInOrderByLastUpdatedAtDesc(String tm, String dateTimeThreshold, Collection<PlannedMaintenanceStatus> status);
}
