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

package me.julb.springbootstarter.messaging.builders;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;

/**
 * A builder for {@link AsyncMessageDTO} instances.
 * <br>
 * @author Julb.
 */
public class AsyncMessageBuilder<T> {

    /**
     * The message.
     */
    private AsyncMessageDTO<T> message;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public AsyncMessageBuilder() {
        this.message = new AsyncMessageDTO<>();
        this.message.setTimestamp(DateUtility.dateTimeNow());
        this.message.setId(IdentifierUtility.generateId());
        this.message.setVersion(Integers.ONE);
        this.message.setAttributes(new HashMap<>());
    }

    // ------------------------------------------ Class methods.

    /**
     * Setter for property identifier.
     * @param identifier New value of property identifier.
     * @return the current builder instance.
     */
    public AsyncMessageBuilder<T> identifier(String identifier) {
        this.message.setId(identifier);
        return this;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     * @return the current builder instance.
     */
    public AsyncMessageBuilder<T> version(Integer version) {
        this.message.setVersion(version);
        return this;
    }

    /**
     * Add an attribute to the message.
     * @param name the attribute name to set.
     * @param value the attribute value to set.
     * @return the current builder instance.
     */
    public AsyncMessageBuilder<T> attribute(String name, String value) {
        this.message.getAttributes().put(name, value);
        return this;
    }

    /**
     * Add an attribute to the message.
     * @param name the attribute name to set.
     * @param value the attribute value to set.
     * @return the current builder instance.
     */
    public AsyncMessageBuilder<T> attributeIfNotBlank(String name, String value) {
        if (StringUtils.isNotBlank(value)) {
            this.message.getAttributes().put(name, value);
        }
        return this;
    }

    /**
     * Setter for property body.
     * @param body New value of property body.
     * @return the current builder instance.
     */
    public AsyncMessageBuilder<T> body(T body) {
        this.message.setBody(body);
        return this;
    }

    /**
     * Builds the message instance.
     * @return the message instance.
     */
    public AsyncMessageDTO<T> build() {
        return this.message;
    }

    // ------------------------------------------ Overridden methods.
}
