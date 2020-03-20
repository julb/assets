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
package io.julb.library.utility.josejwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;

import io.julb.library.utility.josejwt.jwk.IJWKProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualAsymmetricJWKProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualJWKSetProvider;
import io.julb.library.utility.josejwt.jwk.impl.ManualSymmetricJWKProvider;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import net.javacrumbs.jsonunit.JsonAssert;

/**
 * Unit test class for {@link TokenReceiver}.
 * <P>
 * @author Julb.
 */
public class TokenReceiverTest {

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
    private IJWKProvider asymmetricRSASignatureJWKProvider;

    /**
     * The asymmetric RSA JWK provider.
     */
    private IJWKProvider asymmetricRSAVerifyJWKProvider;

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
            .audience("API_Audience")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .expirationTime(expirationTime.getTime())
            .build();
        // @formatter:on

        //@formatter:off
        RSAKey rsaKey = new RSAKeyGenerator(2048).generate();
        String kid = UUID.randomUUID().toString();
        
        this.asymmetricRSASignatureJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.RS384.getName())
            .keyId(kid)
            .keyPair(rsaKey.toRSAPrivateKey(), rsaKey.toRSAPublicKey())
            .useForSignature()
            .build();
        
        this.asymmetricRSAVerifyJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.RS384.getName())
            .keyId(kid)
            .publicKey(rsaKey.toRSAPublicKey())
            .useForSignature()
            .build();
        //@formatter:on

        //@formatter:off
        this.symmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm("dir")
            .keyId(UUID.randomUUID().toString())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForEncryption()
            .build();
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test
    public void whenReceivingToken_thenReturnValidJWE()
        throws Exception {
        //@formatter:off
        String emittedToken = new TokenEmitter()
            .setSignatureJWKProvider(asymmetricRSASignatureJWKProvider)
            .setEncryptionJWKProvider(symmetricJWKProvider)
            .emit(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        String receivedToken = new TokenReceiver()
            .setExpectedAudience(this.jwtClaimsSet.getAudience().get(0))
            .setExpectedIssuer(this.jwtClaimsSet.getIssuer())
            .setSignatureJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricRSAVerifyJWKProvider).build())
            .setEncryptionJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build())
            .receive(emittedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), receivedToken);
    }

    /**
     * Test method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void whenReceivingTokenWithoutExpectedIssuer_thenThrowIllegalArgumentException()
        throws Exception {
        //@formatter:off
        String emittedToken = new TokenEmitter()
            .setSignatureJWKProvider(asymmetricRSASignatureJWKProvider)
            .setEncryptionJWKProvider(symmetricJWKProvider)
            .emit(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        new TokenReceiver()
            .setExpectedAudience(this.jwtClaimsSet.getAudience().get(0))
            .setSignatureJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricRSAVerifyJWKProvider).build())
            .setEncryptionJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build())
            .receive(emittedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void whenReceivingTokenWithoutExpectedAudience_thenThrowIllegalArgumentException()
        throws Exception {
        //@formatter:off
        String emittedToken = new TokenEmitter()
            .setSignatureJWKProvider(asymmetricRSASignatureJWKProvider)
            .setEncryptionJWKProvider(symmetricJWKProvider)
            .emit(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        new TokenReceiver()
            .setExpectedIssuer(this.jwtClaimsSet.getIssuer())
            .setSignatureJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricRSAVerifyJWKProvider).build())
            .setEncryptionJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build())
            .receive(emittedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void whenReceivingTokenWithoutSignature_thenThrowIllegalArgumentException()
        throws Exception {
        //@formatter:off
        String emittedToken = new TokenEmitter()
            .setSignatureJWKProvider(asymmetricRSASignatureJWKProvider)
            .setEncryptionJWKProvider(symmetricJWKProvider)
            .emit(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        new TokenReceiver()
            .setExpectedAudience(this.jwtClaimsSet.getAudience().get(0))
            .setExpectedIssuer(this.jwtClaimsSet.getIssuer())
            .setEncryptionJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build())
            .receive(emittedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void whenReceivingTokenWithoutEncryption_thenThrowIllegalArgumentException()
        throws Exception {
        //@formatter:off
        String emittedToken = new TokenEmitter()
            .setSignatureJWKProvider(asymmetricRSASignatureJWKProvider)
            .setEncryptionJWKProvider(symmetricJWKProvider)
            .emit(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        new TokenReceiver()
            .setExpectedAudience(this.jwtClaimsSet.getAudience().get(0))
            .setExpectedIssuer(this.jwtClaimsSet.getIssuer())
            .setEncryptionJWKSetProvider(new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build())
            .receive(emittedToken);
        //@formatter:on
    }
}
