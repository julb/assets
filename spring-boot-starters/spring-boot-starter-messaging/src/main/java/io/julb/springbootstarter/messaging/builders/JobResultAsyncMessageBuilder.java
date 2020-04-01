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

import io.julb.library.dto.messaging.events.EventCollectorAsyncMessageLevel;
import io.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import io.julb.library.dto.messaging.events.JobResultStatus;
import io.julb.library.utility.constants.Integers;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.identifier.IdentifierUtility;

/**
 * A builder for {@link JobResultAsyncMessageDTO} instances.
 * <P>
 * @author Julb.
 */
public class JobResultAsyncMessageBuilder<T> {

    /**
     * The message.
     */
    private JobResultAsyncMessageDTO<T> message;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public JobResultAsyncMessageBuilder() {
        this.message = new JobResultAsyncMessageDTO<>();
        this.message.setTimestamp(DateUtility.dateTimeNow());
        this.message.setId(IdentifierUtility.generateId());
        this.message.setVersion(Integers.ONE);

        this.message.setLevel(EventCollectorAsyncMessageLevel.INFO);
    }

    // ------------------------------------------ Class methods.

    /**
     * Setter for property identifier.
     * @param identifier New value of property identifier.
     * @return the current builder instance.
     */
    public JobResultAsyncMessageBuilder<T> identifier(String identifier) {
        this.message.setId(identifier);
        return this;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     * @return the current builder instance.
     */
    public JobResultAsyncMessageBuilder<T> version(Integer version) {
        this.message.setVersion(version);
        return this;
    }

    /**
     * Setter for property level.
     * @param level New value of property level.
     * @return the current builder instance.
     */
    public JobResultAsyncMessageBuilder<T> level(EventCollectorAsyncMessageLevel level) {
        this.message.setLevel(level);
        return this;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     * @return the current builder instance.
     */
    public JobResultAsyncMessageBuilder<T> name(String name) {
        this.message.setName(name);
        return this;
    }

    /**
     * Setter for property result.
     * @param result New value of property result.
     * @return the current builder instance.
     */
    public JobResultAsyncMessageBuilder<T> result(JobResultStatus result) {
        this.message.setResult(result);
        return this;
    }

    /**
     * Setter for property body.
     * @param body New value of property body.
     * @return the current builder instance.
     */
    public JobResultAsyncMessageBuilder<T> body(T body) {
        this.message.setBody(body);
        return this;
    }

    /**
     * Builds the message instance.
     * @return the message instance.
     */
    public JobResultAsyncMessageDTO<T> build() {
        return this.message;
    }

    // ------------------------------------------ Overridden methods.
}
