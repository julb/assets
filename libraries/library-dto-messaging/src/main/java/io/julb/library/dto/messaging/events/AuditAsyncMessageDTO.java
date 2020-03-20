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

import lombok.Getter;
import lombok.Setter;

/**
 * Message to be collected by the audit logs collector.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class AuditAsyncMessageDTO<T> extends EventCollectorAsyncMessageDTO<T> {

    //@formatter:off
     /**
     * The product attribute.
     * -- GETTER --
     * Getter for {@link #product} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #product} property.
     * @param product the value to set.
     */
     //@formatter:on
    private String product;

    //@formatter:off
     /**
     * The objectType attribute.
     * -- GETTER --
     * Getter for {@link #objectType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #objectType} property.
     * @param objectType the value to set.
     */
     //@formatter:on
    private String objectType;

    //@formatter:off
     /**
     * The objectReference attribute.
     * -- GETTER --
     * Getter for {@link #objectReference} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #objectReference} property.
     * @param objectReference the value to set.
     */
     //@formatter:on
    private String objectReference;

    //@formatter:off
     /**
     * The objectName attribute.
     * -- GETTER --
     * Getter for {@link #objectName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #objectName} property.
     * @param objectName the value to set.
     */
     //@formatter:on
    private String objectName;

    //@formatter:off
     /**
     * The action attribute.
     * -- GETTER --
     * Getter for {@link #action} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #action} property.
     * @param action the value to set.
     */
     //@formatter:on
    private String action;

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
