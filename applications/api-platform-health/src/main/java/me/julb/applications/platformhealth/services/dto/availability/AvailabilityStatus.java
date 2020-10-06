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

package me.julb.applications.platformhealth.services.dto.availability;

import org.apache.commons.lang3.ArrayUtils;

/**
 * The component status.
 * <P>
 * @author Julb.
 */
public enum AvailabilityStatus {

    /**
     * Status indicating that the availability is operational.
     */
    UP(1),

    /**
     * Status indicating that the availability is partially OK.
     */
    PARTIAL(2),

    /**
     * Status indicating that the availability is down.
     */
    DOWN(3);

    /**
     * The severity.
     */
    private int severity;

    /**
     * Constructor.
     * @param severity the severity.
     */
    private AvailabilityStatus(int severity) {
        this.severity = severity;
    }

    /**
     * Gets the severity.
     * @return the severity.
     */
    public int severity() {
        return this.severity;
    }

    /**
     * Gets the worsest status among the given ones.
     * @param availabilityStatuses the statuses.
     * @return the one with the greatest severity.
     */
    public static AvailabilityStatus worsest(AvailabilityStatus... availabilityStatuses) {
        AvailabilityStatus worsestStatus = AvailabilityStatus.UP;
        if (ArrayUtils.isNotEmpty(availabilityStatuses)) {
            for (AvailabilityStatus availableStatus : availabilityStatuses) {
                if (availableStatus.severity() > worsestStatus.severity()) {
                    worsestStatus = availableStatus;
                }
            }
        }
        return worsestStatus;
    }
}
