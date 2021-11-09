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
package me.julb.library.utility.barcode.ean13;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The EAN13 utility.
 * <br>
 * @author Julb.
 */
public final class EAN13Utility {

    /**
     * Generates a EAN13 image and write it to the given output stream.
     * @param value the EAN13 value.
     * @param imageFormat the image format ("png", "jpg").
     * @param width the width of the generated image.
     * @param height the height of the generated image.
     * @param outputStream the stream to write the result to.
     * @throws IOException if an error occurs.
     */
    public static void write(String value, String imageFormat, int width, int height, OutputStream outputStream)
        throws IOException {
        EAN13Writer writer = new EAN13Writer();
        BitMatrix bitMatrix = writer.encode(value, BarcodeFormat.EAN_13, width, height);
        MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, outputStream);
    }

    /**
     * Generates a EAN13 image and returns it as a buffered image.
     * @param value the EAN13 value.
     * @param width the width of the generated image.
     * @param height the height of the generated image.
     * @return the buffered image.
     */
    public static BufferedImage generate(String value, int width, int height) {
        EAN13Writer writer = new EAN13Writer();
        BitMatrix bitMatrix = writer.encode(value, BarcodeFormat.EAN_13, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
