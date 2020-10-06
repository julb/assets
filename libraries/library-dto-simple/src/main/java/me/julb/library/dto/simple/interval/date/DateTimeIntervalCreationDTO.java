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

package me.julb.library.dto.simple.interval.date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import me.julb.library.utility.interfaces.Intervallable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.Interval;

/**
 * The DTO to create a date time interval.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Interval
public class DateTimeIntervalCreationDTO implements Intervallable<String> {

    //@formatter:off
     /**
     * The from attribute.
     * -- GETTER --
     * Getter for {@link #from} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #from} property.
     * @param from the value to set.
     */
     //@formatter:on
    @NotNull
    @DateTimeISO8601
    private String from;

    //@formatter:off
     /**
     * The to attribute.
     * -- GETTER --
     * Getter for {@link #to} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #to} property.
     * @param to the value to set.
     */
     //@formatter:on
    @NotNull
    @DateTimeISO8601
    private String to;
}
