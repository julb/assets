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
import lombok.ToString;

/**
 * DTO designed to describe a job result.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class JobResultAsyncMessageDTO<T> extends EventCollectorAsyncMessageDTO<T> {

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    private String name;

    //@formatter:off
     /**
     * The result attribute.
     * -- GETTER --
     * Getter for {@link #result} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #result} property.
     * @param result the value to set.
     */
     //@formatter:on
    private JobResultStatus result;

    /**
     * Constructor.
     */
    public JobResultAsyncMessageDTO() {
        super();
    }
}
