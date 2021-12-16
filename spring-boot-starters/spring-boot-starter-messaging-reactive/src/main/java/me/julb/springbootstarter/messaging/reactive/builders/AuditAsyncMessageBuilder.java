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

package me.julb.springbootstarter.messaging.reactive.builders;

import java.util.HashMap;

import me.julb.library.dto.messaging.events.AuditAsyncMessageDTO;
import me.julb.library.dto.messaging.events.EventCollectorAsyncMessageDTO;
import me.julb.library.dto.messaging.events.EventCollectorAsyncMessageLevel;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;

/**
 * A builder for {@link EventCollectorAsyncMessageDTO} instances.
 * <br>
 * @author Julb.
 */
public class AuditAsyncMessageBuilder<T> {

    /**
     * The message.
     */
    private AuditAsyncMessageDTO<T> message;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public AuditAsyncMessageBuilder() {
        this.message = new AuditAsyncMessageDTO<>();
        this.message.setTimestamp(DateUtility.dateTimeNow());
        this.message.setId(IdentifierUtility.generateId());
        this.message.setVersion(Integers.ONE);
        this.message.setAttributes(new HashMap<>());

        this.message.setLevel(EventCollectorAsyncMessageLevel.INFO);
    }

    // ------------------------------------------ Class methods.

    /**
     * Setter for property identifier.
     * @param identifier New value of property identifier.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> identifier(String identifier) {
        this.message.setId(identifier);
        return this;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> version(Integer version) {
        this.message.setVersion(version);
        return this;
    }

    /**
     * Add an attribute to the message.
     * @param name the attribute name to set.
     * @param value the attribute value to set.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> attribute(String name, String value) {
        this.message.getAttributes().put(name, value);
        return this;
    }

    /**
     * Setter for property level.
     * @param level New value of property level.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> level(EventCollectorAsyncMessageLevel level) {
        this.message.setLevel(level);
        return this;
    }

    /**
     * Setter for property action.
     * @param action New value of property action.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> action(String action) {
        this.message.setAction(action);
        return this;
    }

    /**
     * Setter for property objectReference.
     * @param objectReference New value of property objectReference.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> objectReference(String objectReference) {
        this.message.setObjectReference(objectReference);
        return this;
    }

    /**
     * Setter for property objectType.
     * @param objectType New value of property objectType.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> objectType(String objectType) {
        this.message.setObjectType(objectType);
        return this;
    }

    /**
     * Setter for property objectName.
     * @param objectName New value of property objectName.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> objectName(String objectName) {
        this.message.setObjectName(objectName);
        return this;
    }

    /**
     * Setter for property product.
     * @param product New value of property product.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> product(String product) {
        this.message.setProduct(product);
        return this;
    }

    /**
     * Setter for property user.
     * @param user New value of property user.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> user(String user) {
        this.message.setUser(user);
        return this;
    }

    /**
     * Setter for property body.
     * @param body New value of property body.
     * @return the current builder instance.
     */
    public AuditAsyncMessageBuilder<T> body(T body) {
        this.message.setBody(body);
        return this;
    }

    /**
     * Builds the message instance.
     * @return the message instance.
     */
    public AuditAsyncMessageDTO<T> build() {
        return this.message;
    }

    // ------------------------------------------ Overridden methods.
}
