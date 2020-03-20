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
package me.julb.library.utility.josejwt.operations;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Calendar;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.julb.library.utility.josejwt.jwk.IJWKProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualAsymmetricJWKProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualSymmetricJWKProvider;

/**
 * Unit test class for {@link TokenEncryptionOperation}.
 * <P>
 * @author Julb.
 */
public class TokenEncryptionOperationTest {

    /**
     * A raw JSON web token.
     */
    private JWTClaimsSet jwtClaimsSet;

    /**
     * The symmetric JWK provider.
     */
    private IJWKProvider symmetricJWKProvider;

    /**
     * The asymmetric RSA JWK provider.
     */
    private IJWKProvider asymmetricRSAJWKProvider;

    /**
     * The asymmetric EC JWK provider.
     */
    private IJWKProvider asymmetricECJWKProvider;

    // ------------------------------------------ Constructors.

    // ------------------------------------------ Before/After methods.

    /**
     * Sets-up the test.
     */
    @BeforeEach
    public void setUp()
        throws Exception {
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

        //@formatter:off
        this.symmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm(JWEAlgorithm.DIR.getName())
            .keyId(UUID.randomUUID().toString())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        this.asymmetricRSAJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWEAlgorithm.RSA_OAEP_256.getName())
            .keyId(UUID.randomUUID().toString())
            .publicKey(new RSAKeyGenerator(2048).generate().toRSAPublicKey())
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        this.asymmetricECJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWEAlgorithm.ECDH_ES.getName())
            .keyId(UUID.randomUUID().toString())
            .publicKey(new ECKeyGenerator(Curve.P_384).generate().toECPublicKey())
            .useForEncryption()
            .build();
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test
    public void whenEncryptingTokenSymmetric_thenReturnValidJWE()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        EncryptedJWT encryptedJWT = EncryptedJWT.parse(encryptedToken);
        Assertions.assertEquals("JWT", encryptedJWT.getHeader().getContentType());
        Assertions.assertEquals(this.symmetricJWKProvider.toJWK().getAlgorithm().getName(), encryptedJWT.getHeader().getAlgorithm().getName());
        Assertions.assertEquals("A256GCM", encryptedJWT.getHeader().getEncryptionMethod().getName());
        Assertions.assertEquals(this.symmetricJWKProvider.toJWK().getKeyID(), encryptedJWT.getHeader().getKeyID());
    }

    /**
     * Test method.
     */
    @Test
    public void whenEncryptingTokenAsymmetricRSA_thenReturnValidJWE()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(asymmetricRSAJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        EncryptedJWT encryptedJWT = EncryptedJWT.parse(encryptedToken);
        Assertions.assertEquals("JWT", encryptedJWT.getHeader().getContentType());
        Assertions.assertEquals(asymmetricRSAJWKProvider.toJWK().getAlgorithm().getName(), encryptedJWT.getHeader().getAlgorithm().getName());
        Assertions.assertEquals("A256GCM", encryptedJWT.getHeader().getEncryptionMethod().getName());
        Assertions.assertEquals(this.asymmetricRSAJWKProvider.toJWK().getKeyID(), encryptedJWT.getHeader().getKeyID());
    }

    /**
     * Test method.
     */
    @Test
    public void whenEncryptingTokenAsymmetricEC_thenReturnValidJWE()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(asymmetricECJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        EncryptedJWT encryptedJWT = EncryptedJWT.parse(encryptedToken);
        Assertions.assertEquals("JWT", encryptedJWT.getHeader().getContentType());
        Assertions.assertEquals(asymmetricECJWKProvider.toJWK().getAlgorithm().getName(), encryptedJWT.getHeader().getAlgorithm().getName());
        Assertions.assertEquals("A256GCM", encryptedJWT.getHeader().getEncryptionMethod().getName());
        Assertions.assertEquals(this.asymmetricECJWKProvider.toJWK().getKeyID(), encryptedJWT.getHeader().getKeyID());
    }
}
