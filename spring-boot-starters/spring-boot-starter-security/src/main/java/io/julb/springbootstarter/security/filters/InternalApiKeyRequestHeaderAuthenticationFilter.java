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
package io.julb.springbootstarter.security.filters;

import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

/**
 * The pre-authenticated header authentication filter for API keys.
 * <P>
 * @author Julb.
 */
public class InternalApiKeyRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {

    /**
     * Constructor.
     * @param apiKeyHeader the API key header.
     */
    public InternalApiKeyRequestHeaderAuthenticationFilter(String apiKeyHeader) {
        super();
        this.setPrincipalRequestHeader(apiKeyHeader);
        this.setExceptionIfHeaderMissing(false);
    }
}
