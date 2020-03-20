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

package me.julb.library.utility.validator.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import me.julb.library.utility.validator.constraints.SemverVersion;

/**
 * Validator to check that a String respects a versioning pattern.
 * <P>
 * @author Julb.
 */
public class SemverVersionValidator implements ConstraintValidator<SemverVersion, String> {

    /**
     * Pattern to check a version without snapshots.
     */
    private static final Pattern VERSION_WITH_SNAPSHOTS_FORBIDDEN_PATTERN = Pattern.compile("^[0-9]+.[0-9]+.[0-9]+$");

    /**
     * Pattern to check a version with snapshots allowed.
     */
    private static final Pattern VERSION_WITH_SNAPSHOTS_ALLOWED_PATTERN = Pattern.compile("^[0-9]+.[0-9]+.[0-9]+(-SNAPSHOT)?$");

    /**
     * The flag to allow snapshots
     */
    private boolean allowSnapshots;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(SemverVersion constraintAnnotation) {
        this.allowSnapshots = constraintAnnotation.allowSnapshots();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // Case null
        if (value == null) {
            return true;
        }

        // Min
        if (allowSnapshots) {
            return VERSION_WITH_SNAPSHOTS_ALLOWED_PATTERN.matcher(value).matches();
        } else {
            return VERSION_WITH_SNAPSHOTS_FORBIDDEN_PATTERN.matcher(value).matches();
        }
    }

}
