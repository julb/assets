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

package me.julb.library.utility.validator.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

import me.julb.library.utility.validator.validators.GenericNameValidator;

/**
 * Validator to check that a String respects [A-Za-z\\ \\-]* pattern
 * @author Julb.
 */
@Documented
@Pattern(regexp = "[A-Za-z0-9\\_\\ \\-]*")
@Constraint(validatedBy = GenericNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface GenericName {

    /**
     * Gets the message key associated.
     * @return the message key.
     */
    String message() default "{me.julb.library.utility.validator.constraints.GenericName.message}";

    /**
     * Returns the minimum length.
     * @return the minimum length.
     */
    int min() default 2;

    /**
     * Returns the maximum length.
     * @return the maximum length.
     */
    int max() default 128;

    /**
     * Returns the validation groups.
     * @return the validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Returns the payload type.
     * @return the payload type.
     */
    Class<? extends Payload>[] payload() default {};
}
