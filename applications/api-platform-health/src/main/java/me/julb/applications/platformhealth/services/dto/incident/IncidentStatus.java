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

package me.julb.applications.platformhealth.services.dto.incident;

import java.util.Arrays;
import java.util.Collection;

/**
 * The incident status.
 * <P>
 * @author Julb.
 */
public enum IncidentStatus {

    /**
     * Status indicating that incident is created but not reported to users.
     */
    DRAFT,

    /**
     * Status indicating that incident is identified.
     */
    REPORTED,

    /**
     * Status indicating that incident has been updated
     */
    UPDATE,

    /**
     * Status indicating that solution has been implemented but is currently monitored.
     */
    MONITORING,

    /**
     * Status indicating that incident is resolved.
     */
    RESOLVED;

    /**
     * Returns the status indicating in progress maintenances.
     * @return indicating in progress maintenances.
     */
    public static Collection<IncidentStatus> inProgressStatuses() {
        return Arrays.asList(IncidentStatus.REPORTED, IncidentStatus.UPDATE, IncidentStatus.MONITORING);
    }
}
