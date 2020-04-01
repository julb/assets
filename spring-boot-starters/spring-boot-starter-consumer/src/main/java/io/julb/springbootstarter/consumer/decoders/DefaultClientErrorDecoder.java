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
package io.julb.springbootstarter.consumer.decoders;

import io.julb.library.utility.exceptions.RemoteSystemClientErrorException;
import io.julb.library.utility.exceptions.RemoteSystemGatewayTimeoutException;
import io.julb.library.utility.exceptions.RemoteSystemNotFoundException;
import io.julb.library.utility.exceptions.RemoteSystemServerErrorException;
import io.julb.library.utility.exceptions.RemoteSystemServiceUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * An Error Decoder for Feign clients.
 * <P>
 * @author Julb.
 */
public class DefaultClientErrorDecoder implements ErrorDecoder {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClientErrorDecoder.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        String requestMethod = response.request().httpMethod().name();
        String requestUrl = response.request().url();

        HttpStatus responseStatus = HttpStatus.valueOf(response.status());
        String responseReason = response.reason();

        // Trace message.
        if (HttpStatus.NOT_FOUND.equals(responseStatus)) {
            LOGGER.warn("Call to remote system has failed: <{} {}> returns a <{}> response.", requestMethod, requestUrl, response.status());
        } else {
            LOGGER.error("Call to remote system has failed: <{} {}> returns a <{}> response.", requestMethod, requestUrl, response.status());
        }

        // Data not found
        if (HttpStatus.NOT_FOUND.equals(responseStatus)) {
            return new RemoteSystemNotFoundException(requestMethod, requestUrl, responseStatus.value(), responseReason);
        }

        // Remote Service unavailable.
        if (HttpStatus.SERVICE_UNAVAILABLE.equals(responseStatus)) {
            return new RemoteSystemServiceUnavailableException(requestMethod, requestUrl, responseStatus.value(), responseReason);
        }

        // Remote Service timeout.
        if (HttpStatus.GATEWAY_TIMEOUT.equals(responseStatus)) {
            return new RemoteSystemGatewayTimeoutException(requestMethod, requestUrl, responseStatus.value(), responseReason);
        }

        // Generic handling.

        // Successful request.
        if (responseStatus.is1xxInformational() || responseStatus.is2xxSuccessful() | responseStatus.is3xxRedirection()) {
            return null;
        }

        // Remote system client error.
        if (responseStatus.is4xxClientError()) {
            return new RemoteSystemClientErrorException(requestMethod, requestUrl, responseStatus.value(), responseReason);
        }

        // Remote system failure - Fallback
        return new RemoteSystemServerErrorException(requestMethod, requestUrl, responseStatus.value(), responseReason);

    }

}