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

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.platformhealth.entities.IncidentComponentEntity;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The incident component repository.
 * <br>
 * @author Julb.
 */
public interface IncidentComponentRepository extends MongoRepository<IncidentComponentEntity, String>, MongoSpecificationExecutor<IncidentComponentEntity> {
    /**
     * Finds the components linked to incident by its trademark and incident id.
     * @param tm the trademark.
     * @param incidentId the incident ID.
     * @return the incident component items.
     */
    List<IncidentComponentEntity> findByTmAndIncidentId(String tm, String incidentId);

    /**
     * Finds the component linked to incident by its trademark, incident id and id.
     * @param tm the trademark.
     * @param incidentId the incident ID.
     * @param componentId the component ID.
     * @return the incident component entity, <code>null</code> otherwise.
     */
    IncidentComponentEntity findByTmAndIncidentIdAndComponentId(String tm, String incidentId, String componentId);

    /**
     * Finds the components linked to incident by its trademark and component id.
     * @param tm the trademark.
     * @param componentId the component ID.
     * @return the incident component items.
     */
    List<IncidentComponentEntity> findByTmAndComponentId(String tm, String componentId);

    /**
     * Checks if any incident is linked to the given component.
     * @param componentId the component ID.
     * @return <code>true</code> if it is linked to this component, <code>false</code> otherwise.
     */
    boolean existsByComponentId(String componentId);

}
