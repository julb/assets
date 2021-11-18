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
package me.julb.library.utility.identifier;

import java.security.SecureRandom;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.TextRandomProvider;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;

/**
 * An utility to manipulate identifiers.
 * <br>
 * @author Julb.
 */
public final class IdentifierUtility {

    /**
     * A random provider secure to generate the string.
     * <br>
     * @author Julb.
     */
    private static final class SecureTextRandomProvider implements TextRandomProvider {

        /**
         * A secure random to generate ID.
         */
        private static final SecureRandom SECURE_RANDOM = new SecureRandom();

        /**
         * {@inheritDoc}
         */
        @Override
        public int nextInt(int max) {
            return SECURE_RANDOM.nextInt(max);
        }
    }

    /**
     * Constructor.
     */
    private IdentifierUtility() {
        // Do nothing
    }

    /**
     * Generates a unique universal identifier.
     * <P>
     * The generated identifier should be an alphanumeric string of 32 characters.
     * <P>
     * @return a warranted new identifier for the object.
     */
    public static String generateId() {
        //@formatter:off
        return new RandomStringGenerator.Builder()
            .withinRange(Chars.ZERO, Chars.F_LOWERCASE)
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LOWERCASE_LETTERS)
            .usingRandom(new SecureTextRandomProvider())
            .build()
            .generate(Integers.THIRTY_TWO);
        //@formatter:on
    }
}
