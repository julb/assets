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
package me.julb.library.utility.size;

import me.julb.library.utility.constants.Strings;

/**
 * A size utility to convert CPU values to float.
 * <P>
 * @author Julb.
 */
public final class CpuUtility {

    /**
     * Constructor.
     */
    private CpuUtility() {
        // Do nothing
    }

    /**
     * Returns the CPU value as a numeric value.
     * @param cpuValue the stringified CPU value.
     * @return the float value of the indicated CPU
     */
    public static Float parse(String cpuValue) {
        if (cpuValue.endsWith("m")) {
            return Float.valueOf(cpuValue.replace("m", Strings.EMPTY)) / 1000;
        } else {
            return Float.valueOf(cpuValue);
        }
    }

    /**
     * Returns the CPU value as a numeric value.
     * @param cpuValue the stringified CPU value.
     * @return the float value of the indicated CPU
     */
    public static Float parseAndRound(String cpuValue) {
        Float value = parse(cpuValue);
        return Float.valueOf(Math.round(value * 10)) / 10;
    }

}
