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
package me.julb.springbootstarter.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import me.julb.springbootstarter.core.configurations.CoreConfiguration;
import me.julb.springbootstarter.security.reactive.configurations.SecurityReactiveConfiguration;
import me.julb.springbootstarter.web.helpers.UnitTestService;
import me.julb.springbootstarter.web.reactive.configurations.WebReactiveConfiguration;
import me.julb.springbootstarter.web.reactive.controllers.AliveController;

/**
 * A controller advice defined to catch all global exceptions.
 * <br>
 * @author Julb.
 */
@WebFluxTest
@ContextConfiguration(classes = {AliveController.class, UnitTestService.class, CoreConfiguration.class, WebReactiveConfiguration.class, SecurityReactiveConfiguration.class})
public class AliveControllerTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private WebTestClient webTestClient;

    /**
     * The service mocked.
     */
    @MockBean
    private UnitTestService unitTestServiceMock;

    /**
     * Unit test method.
     */
    @Test
    public void whenSimpleCall_thenReturn200()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/alive")
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus()
                .is2xxSuccessful()
            .expectBody()
                .jsonPath("$.alive").isEqualTo(true);
        //@formatter:on
    }
}
