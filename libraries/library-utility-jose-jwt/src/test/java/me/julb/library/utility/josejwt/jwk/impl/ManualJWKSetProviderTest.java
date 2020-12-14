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
package me.julb.library.utility.josejwt.jwk.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.julb.library.utility.josejwt.jwk.IJWKProvider;

/**
 * The symmetric JWK.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ManualJWKSetProviderTest {

    /**
     * The algorithm to test.
     */
    private IJWKProvider symmetricJWKProvider;

    /**
     * The algorithm to test.
     */
    private IJWKProvider asymmetricJWKProvider;

    /**
     * Sets-up the test before run.
     */
    @BeforeEach
    public void setUp()
        throws JOSEException {
        //@formatter:off
        this.symmetricJWKProvider = new ManualSymmetricJWKProvider.Builder()
            .algorithm("A256GCM")
            .keyId(UUID.randomUUID().toString())
            .secretKey("aaaaaaaabbbbbbbbccccccccdddddddd")
            .useForEncryption()
            .build();
        //@formatter:on

      //@formatter:off
        this.asymmetricJWKProvider = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(JWEAlgorithm.RSA_OAEP_256.getName())
            .keyId(UUID.randomUUID().toString())
            .publicKey(new RSAKeyGenerator(2048).generate().toRSAPublicKey())
            .useForSignature()
            .build();
        //@formatter:on
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildJWKSetEmpty_thenReturnValidJSONString() {
        ManualJWKSetProvider jwkSetProvider = new ManualJWKSetProvider();
        String jsonString = jwkSetProvider.toJSONString(false);

        JSONObject jsonObject = new JSONObject(jsonString);
        Assertions.assertEquals(jwkSetProvider.getJwkProviders().size(), jsonObject.getJSONArray("keys").length());
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildJWKSetWithPrivateItems_thenReturnValidJSONString() {
        //@formatter:off
        ManualJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder()
            .addJWKProvider(this.symmetricJWKProvider)
            .addJWKProvider(this.asymmetricJWKProvider)
            .build();
        //@formatter:on
        JSONObject jsonObject = new JSONObject(jwkSetProvider.toJSONString(false));
        Assertions.assertEquals(jwkSetProvider.getJwkProviders().size(), jsonObject.getJSONArray("keys").length());
    }

    /**
     * Test method.
     */
    @Test
    public void whenBuildJWKSetWithPublicItems_thenReturnValidJSONString() {
        //@formatter:off
        ManualJWKSetProvider jwkSetProvider = new ManualJWKSetProvider.Builder()
            .addJWKProvider(this.symmetricJWKProvider)
            .addJWKProvider(this.asymmetricJWKProvider)
            .build();
        //@formatter:on

        JSONObject jsonObject = new JSONObject(jwkSetProvider.toJSONString());
        Assertions.assertEquals(jwkSetProvider.getJwkProviders().size() - 1, jsonObject.getJSONArray("keys").length()); // Exclude symmetric key.
    }
}
