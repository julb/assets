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
package me.julb.library.utility.http;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.julb.library.utility.http.HttpHeaderUtility;

/**
 * A generic configuration class for a SSL client.
 * <P>
 * @author Julb.
 */
public final class HttpHeaderUtilityTest {

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBearerTokenFromToken_thenReturnBearerToken() {
        String bearerToken = HttpHeaderUtility.toBearerToken("sometoken");
        Assertions.assertEquals("Bearer sometoken", bearerToken);
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBearerTokenFromNothing_thenReturnNull() {
        Assertions.assertNull(HttpHeaderUtility.toBearerToken(null));
        Assertions.assertNull(HttpHeaderUtility.toBearerToken(""));
        Assertions.assertNull(HttpHeaderUtility.toBearerToken(" "));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBearerToken_thenReturnToken() {
        String token = HttpHeaderUtility.fromBearerToken("Bearer sometoken");
        Assertions.assertEquals("sometoken", token);
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromUnmatchBearerToken_thenReturnNull() {
        Assertions.assertNull(HttpHeaderUtility.fromBearerToken("token"));
        Assertions.assertNull(HttpHeaderUtility.fromBearerToken("bearer token"));
        Assertions.assertNull(HttpHeaderUtility.fromBearerToken("BEARER token"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBlankBearer_thenReturnNull() {
        Assertions.assertNull(HttpHeaderUtility.fromBearerToken(null));
        Assertions.assertNull(HttpHeaderUtility.fromBearerToken(""));
        Assertions.assertNull(HttpHeaderUtility.fromBearerToken(" "));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBasicTokenFromUsernamePassword_thenReturnBasicToken() {
        String basicToken = HttpHeaderUtility.toBasicToken("admin", "my:password");
        Assertions.assertEquals("Basic YWRtaW46bXk6cGFzc3dvcmQ", basicToken);
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBasicTokenFromNothing_thenReturnNull() {
        Assertions.assertNull(HttpHeaderUtility.toBasicToken(null, "my:password"));
        Assertions.assertNull(HttpHeaderUtility.toBasicToken("", "my:password"));
        Assertions.assertNull(HttpHeaderUtility.toBasicToken(" ", "my:password"));
        Assertions.assertNull(HttpHeaderUtility.toBasicToken("admin", null));
        Assertions.assertNull(HttpHeaderUtility.toBasicToken("admin", ""));
        Assertions.assertNull(HttpHeaderUtility.toBasicToken("admin", " "));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBasicToken_thenReturnToken() {
        Pair<String, String> token = HttpHeaderUtility.fromBasicToken("Basic YWRtaW46bXk6cGFzc3dvcmQ");
        Assertions.assertEquals("admin", token.getLeft());
        Assertions.assertEquals("my:password", token.getRight());
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromUnmatchBasicToken_thenReturnNull() {
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken("token"));
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken("basic token"));
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken("BASIC token"));
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken("Basic YWRtaW4="));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBlankBasic_thenReturnNull() {
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken(null));
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken(""));
        Assertions.assertNull(HttpHeaderUtility.fromBasicToken(" "));
    }
}