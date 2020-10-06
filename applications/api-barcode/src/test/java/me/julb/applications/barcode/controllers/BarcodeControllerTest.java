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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import me.julb.applications.barcode.controllers.BarcodeController;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Unit test for the {@link BarcodeController} class.
 * <P>
 * @author Julb.
 */
@AutoConfigureMockMvc
public class BarcodeControllerTest extends AbstractBaseTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingEan13Valid_thenReturn200()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/ean13")
                    .param("value", "012345678901")
                    .param("width", "100")
                    .param("height", "100")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG));
        //@formatter:on

        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/ean13")
                    .param("value", "0123456789012")
                    .param("width", "100")
                    .param("height", "100")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingEan13Invalid_thenReturn400()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/ean13")
                    .param("value", "0123456789013")
                    .param("width", "100")
                    .param("height", "100")
            )
            .andExpect(status().isBadRequest());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingEan13WidthHeightInvalid_thenReturn400()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/ean13")
                    .param("value", "012345678901")
                    .param("width", "-100")
                    .param("height", "100")
            )
            .andExpect(status().isBadRequest());
        //@formatter:on

        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/ean13")
                    .param("value", "012345678901")
                    .param("width", "100")
                    .param("height", "-100")
            )
            .andExpect(status().isBadRequest());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingQrCodeValid_thenReturn200()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/qrcode")
                    .param("value", "julb://some.uri")
                    .param("width", "100")
                    .param("height", "100")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingQrCodeWidthHeightInvalid_thenReturn400()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/qrcode")
                    .param("value", "julb://some.uri")
                    .param("format", "png")
                    .param("width", "-100")
                    .param("height", "100")
            )
            .andExpect(status().isBadRequest());
        //@formatter:on

        //@formatter:off
        mockMvc
            .perform(
                get("/barcodes/qrcode")
                    .param("value", "julb://some.uri")
                    .param("width", "100")
                    .param("height", "-100")
            )
            .andExpect(status().isBadRequest());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingPdf417Valid_thenReturn200()
        throws Exception {

       //@formatter:off
       mockMvc
           .perform(
               get("/barcodes/pdf417")
                   .param("value", "julb://some.uri")
                   .param("width", "100")
                   .param("height", "100")
           )
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.IMAGE_PNG));
       //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGeneratingPdf417WidthHeightInvalid_thenReturn400()
        throws Exception {

       //@formatter:off
       mockMvc
           .perform(
               get("/barcodes/pdf417")
                   .param("value", "julb://some.uri")
                   .param("width", "-100")
                   .param("height", "100")
           )
           .andExpect(status().isBadRequest());
       //@formatter:on

       //@formatter:off
       mockMvc
           .perform(
               get("/barcodes/pdf417")
                   .param("value", "julb://some.uri")
                   .param("width", "100")
                   .param("height", "-100")
           )
           .andExpect(status().isBadRequest());
       //@formatter:on
    }
}
