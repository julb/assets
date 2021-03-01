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

package me.julb.applications.ping.configurations.properties;

import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;

/**
 * The remote properties.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class TargetProperties {

    //@formatter:off
     /**
     * The id attribute.
     * -- GETTER --
     * Getter for {@link #id} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #id} property.
     * @param id the value to set.
     */
     //@formatter:on
    private String id = IdentifierUtility.generateId();

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
     * The endpoint attribute.
     * -- GETTER --
     * Getter for {@link #endpoint} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #endpoint} property.
     * @param endpoint the value to set.
     */
     //@formatter:on
    private ConsumerEndpointProperties endpoint;
}
