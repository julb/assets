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

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import me.julb.applications.platformhealth.entities.PlannedMaintenanceComponentEntity;
import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The planned maintenance component repository.
 * <br>
 * @author Julb.
 */
public interface PlannedMaintenanceComponentRepository extends ReactiveMongoRepository<PlannedMaintenanceComponentEntity, String>, MongoSpecificationExecutor<PlannedMaintenanceComponentEntity> {
    /**
     * Finds the components linked to planned maintenance by its trademark and planned maintenance id.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @return the planned maintenance component items.
     */
    Flux<PlannedMaintenanceComponentEntity> findByTmAndPlannedMaintenanceId(String tm, String plannedMaintenanceId);

    /**
     * Checks if the component is linked to planned maintenance by its trademark, planned maintenance id and id.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param componentId the component ID.
     * @return <code>true</code> if a link exists, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByTmAndPlannedMaintenanceIdAndComponentId(String tm, String plannedMaintenanceId, String componentId);

    /**
     * Finds the component linked to planned maintenance by its trademark, planned maintenance id and id.
     * @param tm the trademark.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param componentId the component ID.
     * @return the planned maintenance component entity, <code>null</code> otherwise.
     */
    Mono<PlannedMaintenanceComponentEntity> findByTmAndPlannedMaintenanceIdAndComponentId(String tm, String plannedMaintenanceId, String componentId);

    /**
     * Finds the components linked to planned maintenance by its trademark and component id.
     * @param tm the trademark.
     * @param componentId the component ID.
     * @return the planned maintenance component items.
     */
    Flux<PlannedMaintenanceComponentEntity> findByTmAndComponentId(String tm, String componentId);

    /**
     * Checks if any planned maintenance is linked to the given component.
     * @param componentId the component ID.
     * @return <code>true</code> if it is linked to this component, <code>false</code> otherwise.
     */
    Mono<Boolean> existsByComponentId(String componentId);

}
