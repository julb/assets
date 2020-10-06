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
package me.julb.library.utility.josejwt.digest;

import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Calendar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.julb.library.utility.josejwt.digest.TokenDigestUtility;
import me.julb.library.utility.josejwt.exceptions.JOSEJWTException;

/**
 * A test for token digest.
 * <P>
 * @author Julb.
 */
public class TokenDigestUtilityTest {

    /**
     * A raw JSON web token.
     */
    private JWTClaimsSet jwtClaimsSet;

    // ------------------------------------------ Constructors.

    // ------------------------------------------ Before/After methods.

    /**
     * Sets-up the test.
     */
    @BeforeEach
    public void setUp()
        throws JOSEJWTException {
        Calendar issueTime = Calendar.getInstance();

        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR, 1);

        // @formatter:off
        this.jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer("API_Gateway")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .expirationTime(expirationTime.getTime())
            .build();
        // @formatter:on
    }

    /**
     * Test method.
     */
    @Test
    public void whenHashingToken_thenReturnValidHash()
        throws Exception {
        //@formatter:off
        String hashedToken = TokenDigestUtility.hash(this.jwtClaimsSet.toString());
        //@formatter:on

        Assertions.assertEquals(64, hashedToken.length());
    }
}
