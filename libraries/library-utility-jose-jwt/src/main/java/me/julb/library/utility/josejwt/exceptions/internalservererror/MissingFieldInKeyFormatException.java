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
package me.julb.library.utility.josejwt.exceptions.internalservererror;

/**
 * The exception is thrown when the key is bad formatted.
 * <P>
 * @author Julb.
 */
public class MissingFieldInKeyFormatException extends InvalidKeyFormatException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -6722003018436170947L;

    /**
     * The field name.
     */
    private String fieldName;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     * @param fieldName the field name.
     */
    public MissingFieldInKeyFormatException(String fieldName) {
        super();
        this.fieldName = fieldName;
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("Missing field <%s> to create the key.", this.fieldName);
    }
}
