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
package io.julb.library.utility.size;

import io.julb.library.utility.constants.Strings;

/**
 * A size utility to convert Memory values to Numeric values.
 * <P>
 * @author Julb.
 */
public final class MemoryUtility {

    /**
     * Constructor.
     */
    private MemoryUtility() {
        // Do nothing
    }

    /**
     * Returns the memory value as a numeric value.
     * @param memoryValue the stringified memory value.
     * @return the integer value of the indicated memory.
     */
    public static Long parse(String memoryValue) {
        Double result;
        if (memoryValue.endsWith("K")) {
            result = Long.valueOf(memoryValue.replace("K", Strings.EMPTY)) * Double.valueOf(1000);
        } else if (memoryValue.endsWith("M")) {
            result = Long.valueOf(memoryValue.replace("M", Strings.EMPTY)) * Math.pow(1000, 2);
        } else if (memoryValue.endsWith("G")) {
            result = Long.valueOf(memoryValue.replace("G", Strings.EMPTY)) * Math.pow(1000, 3);
        } else if (memoryValue.endsWith("T")) {
            result = Long.valueOf(memoryValue.replace("T", Strings.EMPTY)) * Math.pow(1000, 4);
        } else if (memoryValue.endsWith("P")) {
            result = Long.valueOf(memoryValue.replace("P", Strings.EMPTY)) * Math.pow(1000, 5);
        } else if (memoryValue.endsWith("E")) {
            result = Long.valueOf(memoryValue.replace("E", Strings.EMPTY)) * Math.pow(1000, 6);
        } else if (memoryValue.endsWith("Ki")) {
            result = Long.valueOf(memoryValue.replace("Ki", Strings.EMPTY)) * Double.valueOf(1024);
        } else if (memoryValue.endsWith("Mi")) {
            result = Long.valueOf(memoryValue.replace("Mi", Strings.EMPTY)) * Math.pow(1024, 2);
        } else if (memoryValue.endsWith("Gi")) {
            result = Long.valueOf(memoryValue.replace("Gi", Strings.EMPTY)) * Math.pow(1024, 3);
        } else if (memoryValue.endsWith("Ti")) {
            result = Long.valueOf(memoryValue.replace("Ti", Strings.EMPTY)) * Math.pow(1024, 4);
        } else if (memoryValue.endsWith("Pi")) {
            result = Long.valueOf(memoryValue.replace("Pi", Strings.EMPTY)) * Math.pow(1024, 5);
        } else if (memoryValue.endsWith("Ei")) {
            result = Long.valueOf(memoryValue.replace("Ei", Strings.EMPTY)) * Math.pow(1024, 6);
        } else {
            result = Double.valueOf(memoryValue);
        }
        return Long.valueOf(result.longValue());
    }

}
