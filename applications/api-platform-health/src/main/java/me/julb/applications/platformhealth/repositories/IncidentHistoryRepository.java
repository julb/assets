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

package me.julb.applications.platformhealth.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.platformhealth.entities.IncidentHistoryEntity;
import me.julb.applications.platformhealth.services.dto.incident.IncidentStatus;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The incident history repository.
 * <P>
 * @author Julb.
 */
public interface IncidentHistoryRepository extends MongoRepository<IncidentHistoryEntity, String>, MongoSpecificationExecutor<IncidentHistoryEntity> {
    /**
     * Finds an incident history by its trademark and incident id.
     * @param tm the trademark.
     * @param incidentId the incident ID.
     * @return the incident history items.
     */
    List<IncidentHistoryEntity> findByTmAndIncidentId(String tm, String incidentId);

    /**
     * Finds an incident history by its trademark, incident id and id.
     * @param tm the trademark.
     * @param incidentId the incident ID.
     * @param id the id.
     * @return the incident history entiity, <code>null</code> otherwise.
     */
    IncidentHistoryEntity findByTmAndIncidentIdAndId(String tm, String incidentId, String id);

    /**
     * Finds the latest incident history by its trademark and incident id.
     * @param tm the trademark.
     * @param incidentId the incident ID.
     * @return the latest incident history.
     */
    IncidentHistoryEntity findTopByTmAndIncidentIdOrderByCreatedAtDesc(String tm, String incidentId);

    /**
     * Finds the incident history by their trademark, incident and with status not set to given ones.
     * @param tm the trademark.
     * @param incidentId the incident ID.
     * @param statusToExclude the status to exclude.
     * @return the incident history by their trademark, incident and with status not set to given ones.
     */
    List<IncidentHistoryEntity> findByTmAndIncidentIdAndStatusNotInOrderByCreatedAtDesc(String tm, String incidentId, Collection<IncidentStatus> statusToExclude);

}
