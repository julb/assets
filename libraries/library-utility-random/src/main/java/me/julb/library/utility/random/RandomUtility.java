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
package me.julb.library.utility.random;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;

/**
 * A utility to generate random strings.
 * <P>
 * @author Julb.
 */
public final class RandomUtility {

    /**
     * Constructor.
     */
    private RandomUtility() {
        // Do nothing
    }

    /**
     * Generates a random mobile phone token.
     * @return a token.
     */
    public static String generateRandomTokenForMobilePhonePurpose() {
        // Generate an URI.
        //@formatter:off
        return new RandomStringGenerator.Builder()
            .withinRange(new char[] {Chars.ZERO, Chars.NINE})
            .filteredBy(CharacterPredicates.DIGITS)
            .build()
            .generate(Integers.EIGHT);
        //@formatter:on
    }

    /**
     * Generates a random mail reset token.
     * @return a token.
     */
    public static String generateRandomTokenForMailPurpose() {
        return generateAlphaNumericToken(Integers.ONE_HUNDRED_TWENTY_EIGHT);
    }

    /**
     * Generates a random mail reset token.
     * @param length the length of the generated string.
     * @return a token.
     */
    public static String generateAlphaNumericToken(int length) {
        // Generate an URI.
        //@formatter:off
        return new RandomStringGenerator.Builder()
            .withinRange(new char[] {Chars.ZERO, Chars.NINE}, new char[] {Chars.A_LOWERCASE, Chars.Z_LOWERCASE}, new char[] {Chars.A_UPPERCASE, Chars.Z_UPPERCASE})
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LETTERS)
            .build()
            .generate(length);
        //@formatter:on
    }
}
