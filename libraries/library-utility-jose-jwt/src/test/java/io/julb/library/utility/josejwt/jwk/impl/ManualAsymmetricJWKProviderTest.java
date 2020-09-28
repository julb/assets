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
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import io.julb.library.utility.josejwt.exceptions.internalservererror.MissingFieldInKeyFormatException;

import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The symmetric JWK.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ManualAsymmetricJWKProviderTest {

    /**
     * The key ID.
     */
    private String keyId;

    /**
     * The algorithm to test.
     */
    private String rsaSignatureAlgorithm;

    /**
     * The RSA key.
     */
    private RSAPublicKey rsaSignaturePublicKey;

    /**
     * The algorithm to test.
     */
    private String rsaEncryptionAlgorithm;

    /**
     * The RSA key.
     */
    private RSAPublicKey rsaEncryptionPublicKey;

    /**
     * The algorithm to test.
     */
    private String ecSignatureAlgorithm;

    /**
     * The EC key.
     */
    private ECPublicKey ecSignaturePublicKey;

    /**
     * The algorithm to test.
     */
    private String ecEncryptionAlgorithm;

    /**
     * The EC key.
     */
    private ECPublicKey ecEncryptionPublicKey;

    /**
     * Sets-up the test before run.
     */
    @BeforeEach
    public void setUp()
        throws JOSEException {
        this.keyId = UUID.randomUUID().toString();
        this.rsaSignatureAlgorithm = JWSAlgorithm.RS384.getName();
        this.rsaSignaturePublicKey = new RSAKeyGenerator(2048).generate().toRSAPublicKey();
        this.rsaEncryptionAlgorithm = JWEAlgorithm.RSA_OAEP_256.getName();
        this.rsaEncryptionPublicKey = new RSAKeyGenerator(2048).generate().toRSAPublicKey();
        this.ecSignatureAlgorithm = JWSAlgorithm.ES384.getName();
        this.ecSignaturePublicKey = new ECKeyGenerator(Curve.P_384).generate().toECPublicKey();
        this.ecEncryptionAlgorithm = JWEAlgorithm.ECDH_ES.getName();
        this.ecEncryptionPublicKey = new ECKeyGenerator(Curve.P_384).generate().toECPublicKey();
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildRSAKeyForSignature_thenReturnValidJSONString() {
        //@formatter:off
        String jsonString = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(rsaSignatureAlgorithm)
            .keyId(keyId)
            .publicKey(rsaSignaturePublicKey)
            .useForSignature()
            .build()
            .toJSONString();
        //@formatter:on

        JSONObject jsonObject = new JSONObject(jsonString);
        Assertions.assertEquals(rsaSignatureAlgorithm, jsonObject.getString("alg"));
        Assertions.assertEquals("sig", jsonObject.getString("use"));
        Assertions.assertEquals(keyId, jsonObject.getString("kid"));
        Assertions.assertEquals("RSA", jsonObject.getString("kty"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildRSAKeyForEncryption_thenReturnValidJSONString() {
        //@formatter:off
        String jsonString = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(rsaEncryptionAlgorithm)
            .keyId(keyId)
            .publicKey(rsaEncryptionPublicKey)
            .useForEncryption()
            .build()
            .toJSONString();
        //@formatter:on

        JSONObject jsonObject = new JSONObject(jsonString);
        Assertions.assertEquals(rsaEncryptionAlgorithm, jsonObject.getString("alg"));
        Assertions.assertEquals("enc", jsonObject.getString("use"));
        Assertions.assertEquals(keyId, jsonObject.getString("kid"));
        Assertions.assertEquals("RSA", jsonObject.getString("kty"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildECKeyForEncryption_thenReturnValidJSONString() {
        //@formatter:off
        String jsonString = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(ecEncryptionAlgorithm)
            .keyId(keyId)
            .publicKey(ecEncryptionPublicKey)
            .useForEncryption()
            .build()
            .toJSONString();
        //@formatter:on

        JSONObject jsonObject = new JSONObject(jsonString);
        Assertions.assertEquals(ecEncryptionAlgorithm, jsonObject.getString("alg"));
        Assertions.assertEquals("enc", jsonObject.getString("use"));
        Assertions.assertEquals(keyId, jsonObject.getString("kid"));
        Assertions.assertEquals("EC", jsonObject.getString("kty"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildECKeyForSignature_thenReturnValidJSONString() {
        //@formatter:off
        String jsonString = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(ecSignatureAlgorithm)
            .keyId(keyId)
            .publicKey(ecSignaturePublicKey)
            .useForSignature()
            .build()
            .toJSONString();
        //@formatter:on

        JSONObject jsonObject = new JSONObject(jsonString);
        Assertions.assertEquals(ecSignatureAlgorithm, jsonObject.getString("alg"));
        Assertions.assertEquals("sig", jsonObject.getString("use"));
        Assertions.assertEquals(keyId, jsonObject.getString("kid"));
        Assertions.assertEquals("EC", jsonObject.getString("kty"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenMissingAlgorithm_thenThrowException() {
        Assertions.assertThrows(MissingFieldInKeyFormatException.class, () -> {
            //@formatter:off
            new ManualAsymmetricJWKProvider.Builder()
                .keyId(keyId)
                .publicKey(rsaSignaturePublicKey)
                .useForSignature()
                .build()
                .toJSONString();
            //@formatter:on
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenMissingKeyId_thenThrowException() {
        Assertions.assertThrows(MissingFieldInKeyFormatException.class, () -> {
            //@formatter:off
            new ManualAsymmetricJWKProvider.Builder()
                .algorithm(rsaSignatureAlgorithm)
                .publicKey(rsaSignaturePublicKey)
                .useForSignature()
                .build()
                .toJSONString();
            //@formatter:on
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenMissingKey_thenThrowException() {
        Assertions.assertThrows(MissingFieldInKeyFormatException.class, () -> {
            //@formatter:off
            new ManualAsymmetricJWKProvider.Builder()
                .algorithm(rsaSignatureAlgorithm)
                .keyId(keyId)
                .useForSignature()
                .build()
                .toJSONString();
            //@formatter:on
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenMissingUse_thenThrowException() {
        Assertions.assertThrows(MissingFieldInKeyFormatException.class, () -> {
            //@formatter:off
            new ManualAsymmetricJWKProvider.Builder()
                .algorithm(rsaSignatureAlgorithm)
                .keyId(keyId)
                .publicKey(rsaSignaturePublicKey)
                .build()
                .toJSONString();
            //@formatter:on
        });
    }

}
