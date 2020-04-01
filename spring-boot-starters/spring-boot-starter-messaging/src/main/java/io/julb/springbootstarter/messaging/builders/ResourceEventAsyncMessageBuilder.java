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

package io.julb.springbootstarter.messaging.builders;

import io.julb.library.dto.messaging.events.EventCollectorAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.utility.constants.Integers;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.identifier.IdentifierUtility;

import org.apache.commons.lang3.StringUtils;

/**
 * A builder for {@link EventCollectorAsyncMessageDTO} instances.
 * <P>
 * @author Julb.
 */
public class ResourceEventAsyncMessageBuilder {

    /**
     * The message.
     */
    private ResourceEventAsyncMessageDTO message;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public ResourceEventAsyncMessageBuilder() {
        this.message = new ResourceEventAsyncMessageDTO();
        this.message.setTimestamp(DateUtility.dateTimeNow());
        this.message.setId(IdentifierUtility.generateId());
        this.message.setVersion(Integers.ONE);
    }

    // ------------------------------------------ Class methods.

    /**
     * Setter for property identifier.
     * @param identifier New value of property identifier.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder identifier(String identifier) {
        this.message.setId(identifier);
        return this;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder version(Integer version) {
        this.message.setVersion(version);
        return this;
    }

    /**
     * Setter for property object.
     * @param clazz the clazz.
     * @param id the id.
     * @param name the name.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder withObject(Class<?> clazz, String id, String name) {
        return withObject(clazz, id, name, clazz.getSimpleName(), StringUtils.EMPTY);
    }

    /**
     * Setter for property object.
     * @param clazz the clazz.
     * @param id the id.
     * @param name the name.
     * @param type the type.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder withObject(Class<?> clazz, String id, String name, String type) {
        return withObject(clazz, id, name, type, StringUtils.EMPTY);
    }

    /**
     * Setter for property object.
     * @param clazz the clazz.
     * @param id the id.
     * @param name the name.
     * @param path the path.
     * @param type the type.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder withObject(Class<?> clazz, String id, String name, String type, String path) {
        this.message.setResourceClassName(clazz.getName());
        this.message.setResourceClassSimpleName(clazz.getSimpleName());
        this.message.setResourceId(id);
        this.message.setResourceName(name);
        this.message.setResourceType(type);
        this.message.setResourcePath(path);
        return this;
    }

    /**
     * Setter for property type.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder created() {
        return eventType(ResourceEventType.CREATED);
    }

    /**
     * Setter for property type.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder updated() {
        return eventType(ResourceEventType.UPDATED);
    }

    /**
     * Setter for property type.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder deleted() {
        return eventType(ResourceEventType.DELETED);
    }

    /**
     * Setter for property type.
     * @param type New value of property type.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder eventType(ResourceEventType type) {
        this.message.setEventType(type);
        return this;
    }

    /**
     * Setter for property user.
     * @param user New value of property user.
     * @return the current builder instance.
     */
    public ResourceEventAsyncMessageBuilder user(String user) {
        this.message.setUser(StringUtils.defaultIfBlank(user, "system"));
        return this;
    }

    /**
     * Builds the message instance.
     * @return the message instance.
     */
    public ResourceEventAsyncMessageDTO build() {
        return this.message;
    }

    // ------------------------------------------ Overridden methods.
}