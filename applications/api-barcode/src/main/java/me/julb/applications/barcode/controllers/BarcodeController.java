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

package me.julb.applications.barcode.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
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
import me.julb.library.utility.exceptions.InternalServerErrorException;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

/**
 * The REST controller to handle barcodes generation.
 * <br>
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
    public Mono<ByteBuffer> generateEAN13Code(@RequestParam("value") String value, @RequestParam("width") @Min(1) int width, @RequestParam("height") @Min(1) int height)
        throws IOException {
        LOGGER.info("Generating EAN13 barcode for value={sha256}{}, w={}, h={}.", DigestUtils.sha256Hex(value), width, height);
        BufferedImage image = EAN13Utility.generate(value, width, height);
        return writeInternal(image, MediaType.IMAGE_PNG);
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
    public Mono<ByteBuffer> generateQRCode(@RequestParam("value") String value, @RequestParam("width") @Min(1) int width, @RequestParam("height") @Min(1) int height)
        throws IOException {
        LOGGER.info("Generating QR code for value={sha256}{}, w={}, h={}", DigestUtils.sha256Hex(value), width, height);
        BufferedImage image = QRCodeUtility.generate(value, width, height);
        return writeInternal(image, MediaType.IMAGE_PNG);
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
    public Mono<ByteBuffer> generatePDF417Code(@RequestParam("value") String value, @RequestParam("width") @Min(1) int width, @RequestParam("height") @Min(1) int height)
        throws IOException {
        LOGGER.info("Generating PDF417 code for value={sha256}{}, w={}, h={}", DigestUtils.sha256Hex(value), width, height);
        BufferedImage image = PDF417Utility.generate(value, width, height);
        return writeInternal(image, MediaType.IMAGE_PNG);
    }

    /**
     * Writes a buffered image as a Mono.
     * @param image the image.
     * @param mediaType the media type.
     * @return the byte buffer.
     */
    private Mono<ByteBuffer> writeInternal(BufferedImage image, MediaType mediaType) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image,mediaType.getSubtype(), os);
            return Mono.just(ByteBuffer.wrap(os.toByteArray()));
        } catch(IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
