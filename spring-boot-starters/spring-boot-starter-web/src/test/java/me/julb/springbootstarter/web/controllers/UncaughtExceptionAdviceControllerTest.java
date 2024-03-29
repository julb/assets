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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import me.julb.springbootstarter.core.configurations.CoreConfiguration;
import me.julb.springbootstarter.security.configurations.SecurityConfiguration;
import me.julb.springbootstarter.web.configurations.WebMvcConfiguration;
import me.julb.springbootstarter.web.helpers.UnitTestController;
import me.julb.springbootstarter.web.helpers.UnitTestService;

/**
 * A controller advice defined to catch all global exceptions.
 * <br>
 * @author Julb.
 */
@WebMvcTest
@ContextConfiguration(classes = {CaughtExceptionAdviceController.class, UncaughtExceptionAdviceController.class, UncaughtErrorController.class, UnitTestController.class, UnitTestService.class, CoreConfiguration.class, WebMvcConfiguration.class,
    SecurityConfiguration.class})
public class UncaughtExceptionAdviceControllerTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * The service mocked.
     */
    @MockBean
    private UnitTestService unitTestServiceMock;

    /**
     * Unit test method.
     */
    @Test
    public void whenUncaughtException_thenReturn500()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new RuntimeException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }
}
