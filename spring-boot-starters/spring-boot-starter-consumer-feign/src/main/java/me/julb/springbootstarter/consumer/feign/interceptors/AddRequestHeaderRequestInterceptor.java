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
package me.julb.springbootstarter.consumer.feign.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Interceptor to add an header name with its value to the request.
 * <br>
 * @author Julb.
 */
public class AddRequestHeaderRequestInterceptor implements RequestInterceptor {

    /**
     * The header name.
     */
    private final String headerName;

    /**
     * The header value.
     */
    private final String[] headerValues;

    /**
     * Constructor.
     * @param headerName the header name.
     * @param headerValue the header value.
     */
    public AddRequestHeaderRequestInterceptor(String headerName, String... headerValue) {
        super();
        this.headerName = headerName;
        this.headerValues = headerValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(RequestTemplate template) {
        template.header(this.headerName, this.headerValues);
    }

}
