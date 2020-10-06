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
package me.julb.springbootstarter.consumer.decoders;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import feign.Request;
import feign.Request.Body;
import feign.Request.HttpMethod;
import me.julb.library.utility.exceptions.AbstractRemoteSystemException;
import me.julb.library.utility.exceptions.RemoteSystemClientErrorException;
import me.julb.library.utility.exceptions.RemoteSystemGatewayTimeoutException;
import me.julb.library.utility.exceptions.RemoteSystemNotFoundException;
import me.julb.library.utility.exceptions.RemoteSystemServerErrorException;
import me.julb.library.utility.exceptions.RemoteSystemServiceUnavailableException;
import me.julb.springbootstarter.consumer.decoders.DefaultClientErrorDecoder;
import feign.RequestTemplate;
import feign.Response;

/**
 * An Error Decoder for Feign clients.
 * <P>
 * @author Julb.
 */
public class DefaultClientErrorDecoderTest {

    /**
     * The sample request.
     */
    private Request sampleRequest;

    /**
     * The object under test.
     */
    private DefaultClientErrorDecoder errorDecoder;

    /**
     * The set-up.
     */
    @Before
    public void setUp() {
        this.errorDecoder = new DefaultClientErrorDecoder();
        this.sampleRequest = Request.create(HttpMethod.GET, "http://nowhere", new HashMap<String, Collection<String>>(), Body.empty(), new RequestTemplate());
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode1xxCall_thenReturnNull() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.CONTINUE.value())
            .build();
        //@formatter:on

        Assert.assertNull(this.errorDecoder.decode("methodKey", response));
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode2xxCall_thenReturnNull() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.OK.value())
            .build();
        //@formatter:on

        Assert.assertNull(this.errorDecoder.decode("methodKey", response));
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode3xxCall_thenReturnNull() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.PERMANENT_REDIRECT.value())
            .build();
        //@formatter:on

        Assert.assertNull(this.errorDecoder.decode("methodKey", response));
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode400Call_thenThrowRemoteSystemClientErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.BAD_REQUEST.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemClientErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode401Call_thenThrowRemoteSystemClientErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.UNAUTHORIZED.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemClientErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode403Call_thenThrowRemoteSystemClientErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.FORBIDDEN.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemClientErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode404Call_thenThrowRemoteSystemNotFoundException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.NOT_FOUND.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemNotFoundException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode405Call_thenThrowRemoteSystemClientErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.METHOD_NOT_ALLOWED.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemClientErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode406Call_thenThrowRemoteSystemClientErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.NOT_ACCEPTABLE.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemClientErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode407Call_thenThrowRemoteSystemClientErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemClientErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode500Call_thenThrowRemoteSystemServerErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemServerErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode501Call_thenThrowRemoteSystemServerErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.NOT_IMPLEMENTED.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemServerErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode502Call_thenThrowRemoteSystemServerErrorExceptionException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.BAD_GATEWAY.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemServerErrorException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode503Call_thenThrowRemoteSystemServiceUnavailableException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemServiceUnavailableException.class);
    }

    /**
     * Test the method.
     */
    @Test
    public void whenDecode504Call_thenThrowRemoteSystemTimeoutException() {
        //@formatter:off
        Response response = Response.builder()
            .request(sampleRequest)
            .status(HttpStatus.GATEWAY_TIMEOUT.value())
            .build();
        //@formatter:on

        Exception e = this.errorDecoder.decode("methodKey", response);
        assertDecoderReturnRemoteSystemException(response, e, RemoteSystemGatewayTimeoutException.class);
    }

    // -------------------------- Private methods.

    /**
     * Asserts that the returned exception instance matches the given type.
     * @param response the response.
     * @param exception the returned exception.
     * @param expectedExceptionClazz the expected exception clazz.
     */
    protected void assertDecoderReturnRemoteSystemException(Response response, Exception exception, Class<? extends Exception> expectedExceptionClazz) {
        Assert.assertNotNull(exception);

        Assert.assertTrue("Method should have thrown a " + expectedExceptionClazz.getName(), exception.getClass().equals(expectedExceptionClazz));

        AbstractRemoteSystemException castedException = (AbstractRemoteSystemException) exception;
        Assert.assertEquals(sampleRequest.httpMethod().name(), castedException.getHttpRequestMethod());
        Assert.assertEquals(sampleRequest.url(), castedException.getHttpRequestUrl());
        Assert.assertEquals(response.reason(), castedException.getHttpResponseReason());
        Assert.assertEquals(response.status(), castedException.getHttpResponseStatusCode().intValue());
    }

}
