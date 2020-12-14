/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package me.julb.applications.barcode.controllers;

import io.swagger.v3.oas.annotations.Operation;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.julb.library.utility.barcode.ean13.EAN13Utility;
import me.julb.library.utility.barcode.pdf417.PDF417Utility;
import me.julb.library.utility.barcode.qrcode.QRCodeUtility;

/**
 * The REST controller to handle barcodes generation.
 * <P>
 * @author Julb.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/barcodes")
public class BarcodeController {

    /**
     * Generates a EAN13 bar code.
     * @param value the value.
     * @param width the width.
     * @param height the height.
     * @throws IOException if an error occurs.
     * @return the buffered image.
     */
    @Operation(summary = "generates an EAN13 code")
    @GetMapping(path = "/ean13", produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage generateEAN13Code(@RequestParam("value") String value, @RequestParam("width") @Min(1) int width, @RequestParam("height") @Min(1) int height)
        throws IOException {
        LOGGER.info("Generating EAN13 barcode for value={sha256}{}, w={}, h={}.", DigestUtils.sha256Hex(value), width, height);
        return EAN13Utility.generate(value, width, height);
    }

    /**
     * Generates a QRCode.
     * @param value the value.
     * @param width the width.
     * @param height the height.
     * @throws IOException if an error occurs.
     * @return the buffered image.
     */
    @Operation(summary = "generates an QR code")
    @GetMapping(path = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage generateQRCode(@RequestParam("value") String value, @RequestParam("width") @Min(1) int width, @RequestParam("height") @Min(1) int height)
        throws IOException {
        LOGGER.info("Generating QR code for value={sha256}{}, w={}, h={}", DigestUtils.sha256Hex(value), width, height);
        return QRCodeUtility.generate(value, width, height);
    }

    /**
     * Generates a PDF417 code.
     * @param value the value.
     * @param width the width.
     * @param height the height.
     * @throws IOException if an error occurs.
     * @return the buffered image.
     */
    @Operation(summary = "generates an PDF417 code")
    @GetMapping(path = "/pdf417", produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage generatePDF417Code(@RequestParam("value") String value, @RequestParam("width") @Min(1) int width, @RequestParam("height") @Min(1) int height)
        throws IOException {
        LOGGER.info("Generating PDF417 code for value={sha256}{}, w={}, h={}", DigestUtils.sha256Hex(value), width, height);
        return PDF417Utility.generate(value, width, height);
    }
}
