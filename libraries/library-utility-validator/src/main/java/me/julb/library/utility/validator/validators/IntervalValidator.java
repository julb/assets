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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import me.julb.library.utility.interfaces.Intervallable;
import me.julb.library.utility.validator.constraints.Interval;

/**
 * Validator to check that the given object is a valid interval.
 * <br>
 * @author Julb.
 */
public class IntervalValidator implements ConstraintValidator<Interval, Object> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Interval constraintAnnotation) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // Case null
        if (value == null) {
            return true;
        }

        // Get target value.
        if (value instanceof Intervallable) {
            Intervallable intervallable = (Intervallable) value;

            // Skipping the check if one is null.
            if (intervallable.getFrom() == null || intervallable.getTo() == null) {
                return true;
            }

            // Compare from and to.
            return intervallable.getFrom().compareTo(intervallable.getTo()) <= 0;
        } else {
            return false;
        }
    }

}
