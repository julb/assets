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

package me.julb.library.dto.http.error;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO designed for all errors returned by APIs.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class HttpErrorResponseDTO {

    //@formatter:off
     /**
     * The httpStatus attribute.
     * -- GETTER --
     * Getter for {@link #httpStatus} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #httpStatus} property.
     * @param httpStatus the value to set.
     */
     //@formatter:on
    private Integer httpStatus;

    //@formatter:off
     /**
     * The message attribute.
     * -- GETTER --
     * Getter for {@link #message} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #message} property.
     * @param message the value to set.
     */
     //@formatter:on
    private String message;

    //@formatter:off
     /**
     * The dateTime attribute.
     * -- GETTER --
     * Getter for {@link #dateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dateTime} property.
     * @param dateTime the value to set.
     */
     //@formatter:on
    private String dateTime;

    //@formatter:off
     /**
      * List of traces giving additional information about the error.
     * The trace attribute.
     * -- GETTER --
     * Getter for {@link #trace} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #trace} property.
     * @param trace the value to set.
     */
     //@formatter:on
    private List<String> trace = new ArrayList<>();
}
