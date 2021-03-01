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

package me.julb.applications.ping.services.dto;

import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.enums.HealthStatus;

/**
 * The DTO to describe a remote ping result.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class PingTargetDTO {

    //@formatter:off
     /**
     * The metadata attribute.
     * -- GETTER --
     * Getter for {@link #metadata} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #metadata} property.
     * @param metadata the value to set.
     */
     //@formatter:on
    private Map<String, String> metadata = new TreeMap<>();

    //@formatter:off
     /**
     * The status attribute.
     * -- GETTER --
     * Getter for {@link #status} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #status} property.
     * @param status the value to set.
     */
     //@formatter:on
    private HealthStatus status = HealthStatus.UNKNOWN;

    //@formatter:off
     /**
     * The responseStatusCode attribute.
     * -- GETTER --
     * Getter for {@link #responseStatusCode} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #responseStatusCode} property.
     * @param responseStatusCode the value to set.
     */
     //@formatter:on
    private Integer responseStatusCode;

    //@formatter:off
     /**
     * The responseTimeMilliseconds attribute.
     * -- GETTER --
     * Getter for {@link #responseTimeMilliseconds} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #responseTimeMilliseconds} property.
     * @param responseTimeMilliseconds the value to set.
     */
     //@formatter:on
    private Long responseTimeMilliseconds;

}
