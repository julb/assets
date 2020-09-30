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

package io.julb.library.utility.barcode.pdf147;

import io.julb.library.utility.barcode.pdf417.PDF417Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit test class for {@link PDF417Utility}.
 * <P>
 * @author Julb.
 */
public class PDF417UtilityTest {

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingPDF417Png_thenItWorks()
        throws Exception {
        String value = "julb://some.uri";
        PDF417Utility.write(value, "png", 100, 100, new ByteArrayOutputStream());
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingPDF417Jpg_thenItWorks()
        throws Exception {
        String value = "julb://some.uri";
        PDF417Utility.write(value, "jpg", 100, 100, new ByteArrayOutputStream());
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingPDF417Gif_thenItWorks()
        throws Exception {
        String value = "julb://some.uri";
        PDF417Utility.write(value, "gif", 100, 100, new ByteArrayOutputStream());
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingPDF417UnknowFormat_thenItWorks()
        throws Exception {
        Assertions.assertThrows(IOException.class, () -> {
            String value = "julb://some.uri";
            PDF417Utility.write(value, "xyz", 100, 100, new ByteArrayOutputStream());
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingPDF417WithInvalidWidth_thenThrow()
        throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String value = "julb://some.uri";
            PDF417Utility.write(value, "png", -100, 100, new ByteArrayOutputStream());
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingPDF417WithInvalidHeight_thenThrow()
        throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String value = "julb://some.uri";
            PDF417Utility.write(value, "png", 100, -100, new ByteArrayOutputStream());
        });
    }
}
