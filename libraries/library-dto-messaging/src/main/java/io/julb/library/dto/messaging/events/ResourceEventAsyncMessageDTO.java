/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package io.julb.library.dto.messaging.events;

import io.julb.library.dto.messaging.message.AsyncMessageDTO;

import lombok.Getter;
import lombok.Setter;

/**
 * A message linked to a resource event.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ResourceEventAsyncMessageDTO extends AsyncMessageDTO<Void> {

    //@formatter:off
     /**
     * The resourceClassName attribute.
     * -- GETTER --
     * Getter for {@link #resourceClassName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #resourceClassName} property.
     * @param resourceClassName the value to set.
     */
     //@formatter:on
    private String resourceClassName;

    //@formatter:off
     /**
     * The resourceClassSimpleName attribute.
     * -- GETTER --
     * Getter for {@link #resourceClassSimpleName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #resourceClassSimpleName} property.
     * @param resourceClassSimpleName the value to set.
     */
     //@formatter:on
    private String resourceClassSimpleName;

    //@formatter:off
     /**
     * The resourceId attribute.
     * -- GETTER --
     * Getter for {@link #resourceId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #resourceId} property.
     * @param resourceId the value to set.
     */
     //@formatter:on
    private String resourceId;

    //@formatter:off
     /**
     * The resourceName attribute.
     * -- GETTER --
     * Getter for {@link #resourceName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #resourceName} property.
     * @param resourceName the value to set.
     */
     //@formatter:on
    private String resourceName;

    //@formatter:off
     /**
     * The resourceType attribute.
     * -- GETTER --
     * Getter for {@link #resourceType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #resourceType} property.
     * @param resourceType the value to set.
     */
     //@formatter:on
    private String resourceType;

    //@formatter:off
     /**
     * The resourceTrademark attribute.
     * -- GETTER --
     * Getter for {@link #resourceTrademark} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #resourceTrademark} property.
     * @param resourceTrademark the value to set.
     */
     //@formatter:on
    private String resourceTrademark;

    //@formatter:off
     /**
     * The eventType attribute.
     * -- GETTER --
     * Getter for {@link #eventType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #eventType} property.
     * @param eventType the value to set.
     */
     //@formatter:on
    private ResourceEventType eventType;

    //@formatter:off
     /**
     * The user attribute.
     * -- GETTER --
     * Getter for {@link #user} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #user} property.
     * @param user the value to set.
     */
     //@formatter:on
    private String user;

}
