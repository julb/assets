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
package me.julb.library.utility.exceptions;

/**
 * This exception is thrown for bad requests.
 * <P>
 * @author Julb.
 */
public class BadRequestException extends BaseException {

    /**
     * Default message.
     */
    private static final String DEFAULT_MESSAGE = "Bad request";

    /**
     * Constructor.
     */
    public BadRequestException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructor with message.
     * @param message the message.
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor with exception.
     * @param e the cause.
     */
    public BadRequestException(Throwable e) {
        super(DEFAULT_MESSAGE, e);
    }

    /**
     * Constructor with exception and message.
     * @param message the message.
     * @param e the cause.
     */
    public BadRequestException(String message, Throwable e) {
        super(message, e);
    }

}
