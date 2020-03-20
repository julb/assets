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

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;

import io.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.MissingAudienceInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.MissingExpirationInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.MissingIssuerInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.ExpiredTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.InvalidAudienceInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.InvalidIssuerInTokenJOSEJWTException;
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
 * Unit test class for {@link TokenVerifierOperation}.
 * <P>
 * @author Julb.
 */
public class TokenVerifierOperationTest {

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
            .audience("API_backend")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .expirationTime(expirationTime.getTime())
            .build();
        // @formatter:on

        //@formatter:off
        this.symmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.HS256.getName())
            .keyId(UUID.randomUUID().toString())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForSignature()
            .build();
        //@formatter:on

        //@formatter:off
        RSAKey rsaKey = new RSAKeyGenerator(2048).generate();
        this.asymmetricRSAJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.RS384.getName())
            .keyId(UUID.randomUUID().toString())
            .keyPair(rsaKey.toRSAPrivateKey(), rsaKey.toRSAPublicKey())
            .useForSignature()
            .build();
        //@formatter:on

        //@formatter:off
        ECKey ecKey = new ECKeyGenerator(Curve.P_384).generate();
        this.asymmetricECJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.ES384.getName())
            .keyId(UUID.randomUUID().toString())
            .keyPair(ecKey.toECPrivateKey(), ecKey.toECPublicKey())
            .useForSignature()
            .build();
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test
    public void whenVerifyingTokenSymmetric_thenReturnValid()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        String rawToken = new TokenVerifierOperation(jwkSetProvider, this.jwtClaimsSet.getAudience().get(0), this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test
    public void whenVerifyingTokenAsymmetricRSA_thenReturnValid()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(asymmetricRSAJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricRSAJWKProvider).build();
        String rawToken = new TokenVerifierOperation(jwkSetProvider, this.jwtClaimsSet.getAudience().get(0), this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test
    public void whenVerifyingTokenAsymmetricEC_thenReturnValidJWT()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(asymmetricECJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricECJWKProvider).build();
        String rawToken = new TokenVerifierOperation(jwkSetProvider, this.jwtClaimsSet.getAudience().get(0), this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test
    public void whenVerifyingToken_thenSelectValidKidAndReturnValidJWT()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider, asymmetricECJWKProvider, asymmetricRSAJWKProvider).build();
        String rawToken = new TokenVerifierOperation(jwkSetProvider).execute(signedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test(expected = UnresolvableKeyJOSEJWTException.class)
    public void whenVerifyingTokenNoValidKey_thenThrowUnresolvableKeyJOSEJWTException()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(asymmetricECJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider).execute(signedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = UnresolvableKeyJOSEJWTException.class)
    public void whenVerifyingTokenInvalidUse_thenUnresolvableKeyJOSEJWTException()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKProvider lookAlikeSymmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.HS256.getName())
            .keyId(symmetricJWKProvider.toJWK().getKeyID())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(lookAlikeSymmetricJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider).execute(signedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = JOSEJWTException.class)
    public void whenVerifyingTokenValidKidInvalidKey_thenThrowJOSEJWTException()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKProvider lookAlikeSymmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm(JWSAlgorithm.HS256.getName())
            .keyId(symmetricJWKProvider.toJWK().getKeyID())
            .secretKey("eeeeeeeeffffffffgggggggghhhhhhhh")
            .useForEncryption()
            .build();
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(lookAlikeSymmetricJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider).execute(signedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = InvalidIssuerInTokenJOSEJWTException.class)
    public void whenVerifyingTokenInvalidIssuer_thenThrowInvalidIssuerInTokenJOSEJWTException()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        String rawToken = new TokenVerifierOperation(jwkSetProvider, this.jwtClaimsSet.getAudience().get(0), "badIssuer")
            .execute(signedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test(expected = InvalidAudienceInTokenJOSEJWTException.class)
    public void whenVerifyingTokenInvalidAudience_thenThrowInvalidAudienceInTokenJOSEJWTException()
        throws Exception {
        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        String rawToken = new TokenVerifierOperation(jwkSetProvider, "badAudience", this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on

        JsonAssert.assertJsonEquals(this.jwtClaimsSet.toString(), new JSONObject(rawToken));
    }

    /**
     * Test method.
     */
    @Test(expected = ExpiredTokenJOSEJWTException.class)
    public void whenVerifyingTokenExpired_thenThrowExpiredTokenJOSEJWTException()
        throws Exception {
        Calendar issueTime = Calendar.getInstance();
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR, -20);

        //@formatter:off
        this.jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer("API_Gateway")
            .audience("API_backend")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .expirationTime(expirationTime.getTime())
            .build();
        //@formatter:on

        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider, "badAudience", this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = MissingIssuerInTokenJOSEJWTException.class)
    public void whenVerifyingTokenMissingIssuer_thenThrowMissingIssuerInTokenJOSEJWTException()
        throws Exception {
        Calendar issueTime = Calendar.getInstance();
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR, 1);

        //@formatter:off
        this.jwtClaimsSet = new JWTClaimsSet.Builder()
            .audience("API_backend")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .expirationTime(expirationTime.getTime())
            .build();
        //@formatter:on

        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider,this.jwtClaimsSet.getAudience().get(0), "API_Gateway")
            .execute(signedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = MissingAudienceInTokenJOSEJWTException.class)
    public void whenVerifyingTokenMissingAudience_thenThrowMissingAudienceInTokenJOSEJWTException()
        throws Exception {
        Calendar issueTime = Calendar.getInstance();
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR, 1);

        //@formatter:off
        this.jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer("API_Gateway")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .expirationTime(expirationTime.getTime())
            .build();
        //@formatter:on

        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider, "audience", this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test(expected = MissingExpirationInTokenJOSEJWTException.class)
    public void whenVerifyingTokenMissingExpiration_thenThrowMissingExpirationInTokenJOSEJWTException()
        throws Exception {
        Calendar issueTime = Calendar.getInstance();
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR, 1);

        //@formatter:off
        this.jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer("API_Gateway")
            .audience("API_backend")
            .jwtID("jwtId")
            .subject("contact@julb.io")
            .claim("typ", "U2M")
            .issueTime(issueTime.getTime())
            .build();
        //@formatter:on

        //@formatter:off
        String signedToken = new TokenSignatureOperation(symmetricJWKProvider)
            .execute(this.jwtClaimsSet.toString());
        //@formatter:on

        //@formatter:off
        IJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder().addJWKProvider(symmetricJWKProvider).build();
        new TokenVerifierOperation(jwkSetProvider,this.jwtClaimsSet.getAudience().get(0), this.jwtClaimsSet.getIssuer())
            .execute(signedToken);
        //@formatter:on
    }
}
