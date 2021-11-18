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
package me.julb.library.utility.enums;

import me.julb.library.utility.constants.Strings;

/**
 * The health status.
 * <br>
 * @author Julb.
 */
public enum HealthStatus {

    /**
     * {@link HealthStatus} indicating that the component or subsystem is in an unknown state.
     */
    UNKNOWN(-3),

    /**
     * {@link HealthStatus} indicating that the component or subsystem is in an partial state.
     */
    PARTIAL(-2),

    /**
     * {@link HealthStatus} indicating that the component or subsystem is functioning as expected.
     */
    UP(1),

    /**
     * {@link HealthStatus} indicating that the component or subsystem has suffered an unexpected failure.
     */
    DOWN(0),

    /**
     * {@link HealthStatus} indicating that the component or subsystem has been taken out of service and should not be used.
     */
    OUT_OF_SERVICE(-1);

    /**
     * The metrics value.
     */
    private Integer metricsValue;

    /**
     * Constructor.
     * @param metricsValue the metrics value.
     */
    private HealthStatus(Integer metricsValue) {
        this.metricsValue = metricsValue;
    }

    /**
     * The metrics value representation.
     * @return the metrics value representation.
     */
    public Integer metricsValue() {
        return this.metricsValue;
    }

    /**
     * Returns <code>true</code> if the status in healthy, <code>false</code> otherwise.
     * @return <code>true</code> if the status in healthy, <code>false</code> otherwise.
     */
    public boolean healthy() {
        return UP.equals(this);
    }

    /**
     * Returns <code>OK</code> if the status in healthy, <code>KO</code> otherwise.
     * @return <code>OK</code> if the status in healthy, <code>KO</code> otherwise.
     */
    public String okStatus() {
        if (healthy()) {
            return Strings.OK;
        } else {
            return Strings.KO;
        }
    }

}
