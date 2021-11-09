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
package me.julb.library.utility.size;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CpuUtility} class.
 * <br>
 * @author Julb.
 */
public class CpuUtilityTest {

    /**
     * Test method.
     */
    @Test
    public void whenParseCpuWithm_thenReturnValidFloatValue() {
        Float floatValue = CpuUtility.parse("1250m");
        Assertions.assertEquals(Float.valueOf(1.25f), floatValue);
    }

    /**
     * Test method.
     */
    @Test
    public void whenParse_thenReturnValidFloatValue() {
        Float floatValue = CpuUtility.parse("2.375");
        Assertions.assertEquals(Float.valueOf(2.375f), floatValue);
    }

    /**
     * Test method.
     */
    @Test
    public void whenParseAndRoundCpuWithm_thenReturnValidFloatValue() {
        Float floatValue = CpuUtility.parseAndRound("1250m");
        Assertions.assertEquals(Float.valueOf(1.3f), floatValue);
    }

    /**
     * Test method.
     */
    @Test
    public void whenParseAndRound_thenReturnValidFloatValue() {
        Float floatValue = CpuUtility.parseAndRound("2.375");
        Assertions.assertEquals(Float.valueOf(2.4f), floatValue);
    }

}
