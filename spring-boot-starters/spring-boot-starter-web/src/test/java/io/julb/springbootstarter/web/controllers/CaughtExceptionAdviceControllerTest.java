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
package io.julb.springbootstarter.web.controllers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.julb.library.utility.exceptions.BadRequestException;
import io.julb.library.utility.exceptions.ConflictException;
import io.julb.library.utility.exceptions.ForbiddenException;
import io.julb.library.utility.exceptions.InternalServerErrorException;
import io.julb.library.utility.exceptions.NotFoundException;
import io.julb.library.utility.exceptions.NotImplementedException;
import io.julb.library.utility.exceptions.RemoteSystemClientErrorException;
import io.julb.library.utility.exceptions.RemoteSystemGatewayTimeoutException;
import io.julb.library.utility.exceptions.RemoteSystemServerErrorException;
import io.julb.library.utility.exceptions.RemoteSystemServiceUnavailableException;
import io.julb.library.utility.exceptions.UnauthorizedException;
import io.julb.springbootstarter.web.helpers.UnitTestService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * A controller advice defined to catch all global exceptions.
 * <P>
 * @author Julb.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class CaughtExceptionAdviceControllerTest {

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
    public void whenSimpleCall_thenReturn200()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenUnauthorizedException_thenReturn400()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new BadRequestException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.BAD_REQUEST.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenUnauthorizedException_thenReturn401()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new UnauthorizedException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.UNAUTHORIZED.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenForbiddenException_thenReturn403()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new ForbiddenException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.FORBIDDEN.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenNotFoundException_thenReturn404()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new NotFoundException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.NOT_FOUND.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenHttpRequestMethodNotSupportedException_thenReturn405()
        throws Exception {
        //@formatter:off
        Integer expectedStatusCode = HttpStatus.METHOD_NOT_ALLOWED.value();
        mockMvc
            .perform(post("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenHttpMediaTypeNotSupportedException_thenReturn405()
        throws Exception {
        //@formatter:off
        Integer expectedStatusCode = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenConflictException_thenReturn409()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new ConflictException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.CONFLICT.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenNotImplementedException_thenReturn501()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new NotImplementedException());

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.NOT_IMPLEMENTED.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRemoteSystemClientErrorException_thenReturn500()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new RemoteSystemClientErrorException(HttpMethod.GET.name(), "https://url", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()));

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

    /**
     * Unit test method.
     */
    @Test
    public void whenInternalServerErrorException_thenReturn500()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new InternalServerErrorException());

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

    /**
     * Unit test method.
     */
    @Test
    public void whenRemoteSystemServerErrorException_thenReturn502()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new RemoteSystemServerErrorException(HttpMethod.GET.name(), "https://url", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.BAD_GATEWAY.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRemoteSystemServiceUnavailableException_thenReturn502()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new RemoteSystemServiceUnavailableException(HttpMethod.GET.name(), "https://url", HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase()));

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.BAD_GATEWAY.value();
        mockMvc
            .perform(get("/unit-test").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().is(expectedStatusCode))
            .andExpect(jsonPath("$.httpStatus", is(expectedStatusCode)))
            .andExpect(jsonPath("$.dateTime", notNullValue()))
            .andExpect(jsonPath("$.message", notNullValue()))
            .andExpect(jsonPath("$.trace", notNullValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRemoteSystemGatewayTimeoutException_thenReturn504()
        throws Exception {
        Mockito.when(unitTestServiceMock.doHello()).thenThrow(new RemoteSystemGatewayTimeoutException(HttpMethod.GET.name(), "https://url", HttpStatus.GATEWAY_TIMEOUT.value(), HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase()));

        //@formatter:off
        Integer expectedStatusCode = HttpStatus.GATEWAY_TIMEOUT.value();
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
