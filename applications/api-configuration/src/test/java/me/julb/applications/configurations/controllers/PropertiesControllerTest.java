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

package me.julb.applications.configurations.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import me.julb.applications.configuration.controllers.PropertiesController;
import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.springbootstarter.test.base.AbstractBaseTest;
import me.julb.springbootstarter.test.security.annotations.WithMockUser;

/**
 * Unit test for the {@link PropertiesController} class.
 * <br>
 * @author Julb.
 */
@AutoConfigureWebTestClient
public class PropertiesControllerTest extends AbstractBaseTest {

    /**
     * The web test client.
     */
    @Autowired
    private WebTestClient webTestClient;

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenGetProperties_thenReturn200()
        throws Exception {

        //@formatter:off
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/properties").build())
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.['a.property']").isEqualTo("Testio-Value-a")
                .jsonPath("$.['b.property']").isEqualTo("Testio-Value-b")
                .jsonPath("$.['c.property']").isEqualTo("$$REDACTED$$")
                .jsonPath("$.length()").isEqualTo(3);
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenGetPropertiesWithPrefix_thenReturn200()
        throws Exception {

        //@formatter:off
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/properties").queryParam("prefix", "a").build())
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.['a.property']").isEqualTo("Testio-Value-a")
                .jsonPath("$.length()").isEqualTo(1);
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenGetPropertiesWithUnexistingPrefix_thenReturn200()
        throws Exception {

        //@formatter:off
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/properties").queryParam("prefix", "zzz").build())
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$").isEmpty();
        //@formatter:on
    }
}
