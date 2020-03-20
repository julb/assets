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

import lombok.Getter;
import lombok.Setter;

/**
 * This exception is thrown in case a request to a remote system fails.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public abstract class AbstractRemoteSystemException extends BaseException {

    //@formatter:off
     /**
     * The httpRequestMethod attribute.
     * -- GETTER --
     * Getter for {@link #httpRequestMethod} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #httpRequestMethod} property.
     * @param httpRequestMethod the value to set.
     */
     //@formatter:on
    private String httpRequestMethod;

    //@formatter:off
     /**
     * The httpRequestUrl attribute.
     * -- GETTER --
     * Getter for {@link #httpRequestUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #httpRequestUrl} property.
     * @param httpRequestUrl the value to set.
     */
     //@formatter:on
    private String httpRequestUrl;

    //@formatter:off
     /**
     * The httpResponseStatusCode attribute.
     * -- GETTER --
     * Getter for {@link #httpResponseStatusCode} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #httpResponseStatusCode} property.
     * @param httpResponseStatusCode the value to set.
     */
     //@formatter:on
    private Integer httpResponseStatusCode;

    //@formatter:off
     /**
     * The httpResponseReason attribute.
     * -- GETTER --
     * Getter for {@link #httpResponseReason} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #httpResponseReason} property.
     * @param httpResponseReason the value to set.
     */
     //@formatter:on
    private String httpResponseReason;

    /**
     * Constructor.
     * @param httpRequestMethod the HTTP request method.
     * @param httpRequestUrl the HTTP request path.
     * @param httpResponseStatusCode the HTTP Status code.
     * @param httpResponseReason the HTTP reason.
     */
    public AbstractRemoteSystemException(String httpRequestMethod, String httpRequestUrl, Integer httpResponseStatusCode, String httpResponseReason) {
        super();
        this.httpRequestMethod = httpRequestMethod;
        this.httpRequestUrl = httpRequestUrl;
        this.httpResponseStatusCode = httpResponseStatusCode;
        this.httpResponseReason = httpResponseReason;
    }

    /**
     * Constructor.
     * @param httpRequestMethod the HTTP request method.
     * @param httpRequestUrl the HTTP request path.
     * @param httpResponseStatusCode the HTTP Status code.
     * @param httpResponseReason the HTTP reason.
     * @param e the original exception.
     */
    public AbstractRemoteSystemException(String httpRequestMethod, String httpRequestUrl, Integer httpResponseStatusCode, String httpResponseReason, Throwable e) {
        super(e);
        this.httpRequestMethod = httpRequestMethod;
        this.httpRequestUrl = httpRequestUrl;
        this.httpResponseStatusCode = httpResponseStatusCode;
        this.httpResponseReason = httpResponseReason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("Request [%s %s] returned %s - %s.", this.httpRequestMethod, this.httpRequestUrl, String.valueOf(this.httpResponseStatusCode), this.httpResponseReason);
    }
}
