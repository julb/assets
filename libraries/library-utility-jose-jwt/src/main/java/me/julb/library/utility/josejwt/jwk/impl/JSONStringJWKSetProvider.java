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
package me.julb.library.utility.josejwt.jwk.impl;

import com.nimbusds.jose.jwk.JWKSet;

import java.text.ParseException;

import me.julb.library.utility.josejwt.exceptions.badrequest.TokenNotParseableJOSEJWTException;
import me.julb.library.utility.josejwt.jwk.IJWKSetProvider;

/**
 * A JWKS provider based on a stringified JSON.
 * <P>
 * @author Julb.
 */
public class JSONStringJWKSetProvider implements IJWKSetProvider {

    /**
     * The string containing the JWK.
     */
    private String s;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public JSONStringJWKSetProvider() {
        super();
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public JWKSet toJWKSet() {
        try {
            return JWKSet.parse(s);
        } catch (ParseException e) {
            throw new TokenNotParseableJOSEJWTException(e);
        }
    }

    /**
     * The builder for JWKS.
     * <P>
     * @author Julb.
     */
    public static class Builder {

        /**
         * The built instance
         */
        private JSONStringJWKSetProvider instance;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         */
        public Builder() {
            super();
            this.instance = new JSONStringJWKSetProvider();
        }

        // ------------------------------------------ Getters/Setters.

        /**
         * Setter for property s.
         * @param s New value of property s.
         * @return the builder instance.
         */
        public Builder fromJSONString(String s) {
            this.instance.s = s;
            return this;
        }

        /**
         * Returns the built instance.
         * @return the instance.
         */
        public JSONStringJWKSetProvider build() {
            return this.instance;
        }
    }
}
