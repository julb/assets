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

package me.julb.library.utility.validator.validators;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import me.julb.library.utility.constants.Temporals;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;

/**
 * Validator to check that a String is a ISO-8601 datetime.
 * <br>
 * @author Julb.
 */
public class DateTimeISO8601Validator implements ConstraintValidator<DateTimeISO8601, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            TemporalAccessor result = DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).parse(value);
            String dateString = DateTimeFormatter.ofPattern(Temporals.ISO_8601_DATE_TIME).format(result);
            if (!value.equals(dateString)) {
                return false;
            }
        }
        return true;
    }
}
