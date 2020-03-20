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
package io.julb.library.utility.exceptions;

/**
 * This exception is thrown in case a remote system returns a 404.
 * <P>
 * @author Julb.
 */
public class RemoteSystemNotFoundException extends RemoteSystemClientErrorException {

    /**
     * Constructor.
     * @param httpRequestMethod the HTTP request method.
     * @param httpRequestUrl the HTTP request path.
     * @param httpResponseStatusCode the HTTP Status code.
     * @param httpResponseReason the HTTP reason.
     */
    public RemoteSystemNotFoundException(String httpRequestMethod, String httpRequestUrl, Integer httpResponseStatusCode, String httpResponseReason) {
        super(httpRequestMethod, httpRequestUrl, httpResponseStatusCode, httpResponseReason);
    }

    /**
     * Constructor.
     * @param httpRequestMethod the HTTP request method.
     * @param httpRequestUrl the HTTP request path.
     * @param httpResponseStatusCode the HTTP Status code.
     * @param httpResponseReason the HTTP reason.
     * @param e the original exception.
     */
    public RemoteSystemNotFoundException(String httpRequestMethod, String httpRequestUrl, Integer httpResponseStatusCode, String httpResponseReason, Throwable e) {
        super(httpRequestMethod, httpRequestUrl, httpResponseStatusCode, httpResponseReason, e);
    }
}
