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
package io.julb.library.utility.http;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals("Bearer sometoken", bearerToken);
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBearerTokenFromNothing_thenReturnNull() {
        Assert.assertNull(HttpHeaderUtility.toBearerToken(null));
        Assert.assertNull(HttpHeaderUtility.toBearerToken(""));
        Assert.assertNull(HttpHeaderUtility.toBearerToken(" "));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBearerToken_thenReturnToken() {
        String token = HttpHeaderUtility.fromBearerToken("Bearer sometoken");
        Assert.assertEquals("sometoken", token);
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromUnmatchBearerToken_thenReturnNull() {
        Assert.assertNull(HttpHeaderUtility.fromBearerToken("token"));
        Assert.assertNull(HttpHeaderUtility.fromBearerToken("bearer token"));
        Assert.assertNull(HttpHeaderUtility.fromBearerToken("BEARER token"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBlankBearer_thenReturnNull() {
        Assert.assertNull(HttpHeaderUtility.fromBearerToken(null));
        Assert.assertNull(HttpHeaderUtility.fromBearerToken(""));
        Assert.assertNull(HttpHeaderUtility.fromBearerToken(" "));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBasicTokenFromUsernamePassword_thenReturnBasicToken() {
        String basicToken = HttpHeaderUtility.toBasicToken("admin", "my:password");
        Assert.assertEquals("Basic YWRtaW46bXk6cGFzc3dvcmQ", basicToken);
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingBasicTokenFromNothing_thenReturnNull() {
        Assert.assertNull(HttpHeaderUtility.toBasicToken(null, "my:password"));
        Assert.assertNull(HttpHeaderUtility.toBasicToken("", "my:password"));
        Assert.assertNull(HttpHeaderUtility.toBasicToken(" ", "my:password"));
        Assert.assertNull(HttpHeaderUtility.toBasicToken("admin", null));
        Assert.assertNull(HttpHeaderUtility.toBasicToken("admin", ""));
        Assert.assertNull(HttpHeaderUtility.toBasicToken("admin", " "));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBasicToken_thenReturnToken() {
        Pair<String, String> token = HttpHeaderUtility.fromBasicToken("Basic YWRtaW46bXk6cGFzc3dvcmQ");
        Assert.assertEquals("admin", token.getLeft());
        Assert.assertEquals("my:password", token.getRight());
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromUnmatchBasicToken_thenReturnNull() {
        Assert.assertNull(HttpHeaderUtility.fromBasicToken("token"));
        Assert.assertNull(HttpHeaderUtility.fromBasicToken("basic token"));
        Assert.assertNull(HttpHeaderUtility.fromBasicToken("BASIC token"));
        Assert.assertNull(HttpHeaderUtility.fromBasicToken("Basic YWRtaW4="));
    }

    /**
     * Test method.
     */
    @Test
    public void whenGeneratingTokenFromBlankBasic_thenReturnNull() {
        Assert.assertNull(HttpHeaderUtility.fromBasicToken(null));
        Assert.assertNull(HttpHeaderUtility.fromBasicToken(""));
        Assert.assertNull(HttpHeaderUtility.fromBasicToken(" "));
    }
}