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
package io.julb.library.utility.josejwt.jwk;

import com.nimbusds.jose.jwk.JWKSet;

/**
 * An interface describing a JWKSet provider.
 * <P>
 * @author Julb.
 */
@FunctionalInterface
public interface IJWKSetProvider {

    /**
     * Generates a JWK set.
     * @return the JWK set.
     */
    JWKSet toJWKSet();

    /**
     * Refresh the JWKSet.
     * @return <code>true</code> if it has been refreshed, <code>false</code> otherwise.
     */
    default boolean refreshJWKSet() {
        return false;
    }

    /**
     * Generates a JSON string of the keys contained in this JWKSet.
     * @param publicKeyOnly <code>true</code> if only public keys should be exported, <code>false</code> otherwise.
     * @return a JSON string of the keys contained in this JWKSet.
     */
    default String toJSONString(boolean publicKeyOnly) {
        return toJWKSet().toJSONObject(publicKeyOnly).toJSONString();
    }

    /**
     * Generates a JSON string representation of the public keys contained in this JWKSet.
     * @return a JSON string representation of the public keys contained in this JWKSet.
     */
    default String toJSONString() {
        return toJSONString(true);
    }

}
