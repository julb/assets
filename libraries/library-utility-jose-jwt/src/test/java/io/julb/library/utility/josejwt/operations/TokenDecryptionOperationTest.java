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
package io.julb.library.utility.josejwt.operations;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;

import io.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.UnresolvableKeyJOSEJWTException;
import io.julb.library.utility.josejwt.jwk.IJWKProvider;
import io.julb.library.utility.josejwt.jwk.IJWKSetProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualAsymmetricJWKProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualJWKSetProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualSymmetricJWKProvider;

import java.util.Calendar;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import net.javacrumbs.jsonunit.JsonAssert;

/**
 * Unit test class for {@link TokenDecryptionOperation}.
 * <P>
 * @author Julb.
 */
public class TokenDecryptionOperationTest {

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
    @Before
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
            .algorithm("dir")
            .keyId(UUID.randomUUID().toString())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        RSAKey rsaKey = new RSAKeyGenerator(2048).generate();
        this.asymmetricRSAJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWEAlgorithm.RSA_OAEP_256.getName())
            .keyId(UUID.randomUUID().toString())
            .keyPair(rsaKey.toRSAPrivateKey(), rsaKey.toRSAPublicKey())
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        ECKey ecKey = new ECKeyGenerator(Curve.P_384).generate();
        this.asymmetricECJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWEAlgorithm.ECDH_ES.getName())
            .keyId(UUID.randomUUID().toString())
            .keyPair(ecKey.toECPrivateKey(), ecKey.toECPublicKey())
            .useForEncryption()
            .build();
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test
    public void whenDecryptingTokenSymmetric_thenReturnValidJWT()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        String rawToken = new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test
    public void whenDecryptingTokenAsymmetricRSA_thenReturnValidJWT()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(asymmetricRSAJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricRSAJWKProvider).build();
        String rawToken = new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test
    public void whenDecryptingTokenAsymmetricEC_thenReturnValidJWT()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(asymmetricECJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricECJWKProvider).build();
        String rawToken = new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test
    public void whenDecryptingToken_thenSelectValidKidAndReturnValidJWT()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider, asymmetricECJWKProvider, asymmetricRSAJWKProvider).build();
        String rawToken = new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test(expected = UnresolvableKeyJOSEJWTException.class)
    public void whenDecryptingTokenNoValidKey_thenThrowUnresolvableKeyJOSEJWTException()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricECJWKProvider).build();
        new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = UnresolvableKeyJOSEJWTException.class)
    public void whenDecryptingTokenInvalidUse_thenUnresolvableKeyJOSEJWTException()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKProvider lookAlikeSymmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm("dir")
            .keyId(symmetricJWKProvider.toJWK().getKeyID())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForSignature()
            .build();
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(lookAlikeSymmetricJWKProvider).build();
        new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = JOSEJWTException.class)
    public void whenDecryptingTokenValidKidInvalidKey_thenThrowJOSEJWTException()
        throws Exception {
        //@formatter:off
        String encryptedToken = new TokenEncryptionOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKProvider lookAlikeSymmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm("dir")
            .keyId(symmetricJWKProvider.toJWK().getKeyID())
            .secretKey("eeeeeeeeffffffffgggggggghhhhhhhh")
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(lookAlikeSymmetricJWKProvider).build();
        new TokenDecryptionOperation(jwkSetProvider).execute(encryptedToken);
        //@formatter:on
    }
}
