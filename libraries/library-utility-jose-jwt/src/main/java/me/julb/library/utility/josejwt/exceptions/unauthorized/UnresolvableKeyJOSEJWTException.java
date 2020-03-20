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
package me.julb.library.utility.josejwt.exceptions.unauthorized;

import me.julb.library.utility.josejwt.exceptions.JOSEJWTException;

/**
 * This exception is thrown when the token has an invalid audience.
 * <P>
 * @author Julb.
 */
public class UnresolvableKeyJOSEJWTException extends JOSEJWTException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -128234853992116297L;

    /**
     * The expected key ID.
     */
    private String expectedKid;

    /**
     * The expected key use.
     */
    private String expectedKeyUse;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     * @param expectedKid the expected KID.
     * @param expectedKeyUse the expected key use.
     */
    public UnresolvableKeyJOSEJWTException(String expectedKid, String expectedKeyUse) {
        super();
        this.expectedKid = expectedKid;
        this.expectedKeyUse = expectedKeyUse;
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("Unable to resolve key with kid = <%s> and use = <%s>.", expectedKid, expectedKeyUse);
    }
}
