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
package io.julb.library.utility.josejwt.jwk.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;

import io.julb.library.utility.josejwt.exceptions.badrequest.TokenNotParseableJOSEJWTException;

import lombok.Getter;
import lombok.Setter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.javacrumbs.jsonunit.JsonAssert;

/**
 * The symmetric JWK.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class JSONStringJWKSetProviderTest {

    /**
     * Sets-up the test before run.
     */
    @BeforeEach
    public void setUp()
        throws JOSEException {

    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildJWKSetFromJSON_thenReturnValidJSONString() {
        //@formatter:off
        String inputString = "{\"keys\":[{\"e\":\"AQAB\",\"n\":\"kWp2zRA23Z3vTL4uoe8kTFptxBVFunIoP4t_8TDYJrOb7D1iZNDXVeEsYKp6ppmrTZDAgd-cNOTKLd4M39WJc5FN0maTAVKJc7NxklDeKc4dMe1BGvTZNG4MpWBo-taKULlYUu0ltYJuLzOjIrTHfarucrGoRWqM0sl3z2-fv9k\",\"kty\":\"RSA\",\"kid\":\"1\"}]}";
        JWKSet jwkSet = new JSONStringJWKSetProvider.Builder()
            .fromJSONString(inputString)
            .build()
            .toJWKSet();
        //@formatter:on

        JsonAssert.assertJsonEquals(jwkSet.toJSONObject(), inputString);
        JsonAssert.assertJsonEquals(1, jwkSet.getKeys().size());
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildJWKSNoString_thenThrowTokenIsNotParseableException() {
        Assertions.assertThrows(TokenNotParseableJOSEJWTException.class, () -> {
            new JSONStringJWKSetProvider.Builder().build().toJWKSet();
        });
    }
}
