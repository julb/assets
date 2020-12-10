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

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.EncryptedJWT;

import java.text.ParseException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.josejwt.digest.TokenDigestUtility;
import me.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import me.julb.library.utility.josejwt.exceptions.badrequest.TokenNotParseableJOSEJWTException;
import me.julb.library.utility.josejwt.exceptions.unauthorized.UnresolvableKeyJOSEJWTException;
import me.julb.library.utility.josejwt.exceptions.unauthorized.UnsupportedKeyTypeJOSEJWTException;
import me.julb.library.utility.josejwt.jwk.IJWKSetProvider;

/**
 * The operation that decrypts a token with a private key.
 * <P>
 * @author Julb.
 */
@Slf4j
public class TokenDecryptionOperation {

    /**
     * The private key used to decrypt the token.
     */
    private IJWKSetProvider jwkSetProvider;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     * @param jwkSetProvider the JWKSet provider.
     */
    public TokenDecryptionOperation(IJWKSetProvider jwkSetProvider) {
        super();
        this.jwkSetProvider = jwkSetProvider;
    }

    // ------------------------------------------ Utility methods.

    /**
     * Execute the operation.
     * @param token the token to decrypt.
     * @return the token decrypted with the private key.
     * @throws JOSEJWTException if an error occurs.
     */
    public String execute(String token)
        throws JOSEJWTException {
        try {
            String safeHash = TokenDigestUtility.hash(token);

            LOGGER.debug("Token <{}> - Decrypting the token.", safeHash);

            // Parse the encrypted JWT
            EncryptedJWT jweObject = EncryptedJWT.parse(token);

            // Get the key ID for the signature.
            JWEAlgorithm alg = jweObject.getHeader().getAlgorithm();
            EncryptionMethod enc = jweObject.getHeader().getEncryptionMethod();
            String kid = jweObject.getHeader().getKeyID();

            LOGGER.debug("Token <{}> - alg/enc/kid used to sign the token is <{},{},{}>.", safeHash, alg, enc, kid);

            // Get the corresponding JWK.
            LOGGER.debug("Token <{}> - Searching JWK among the JWKSet.", safeHash);
            JWK jwk = getDecryptionJWK(kid, KeyUse.ENCRYPTION);

            LOGGER.debug("Token <{}> - Key has been resolved.", safeHash);

            // Decrypt.
            jweObject.decrypt(getJWEDecrypter(jwk));

            LOGGER.debug("Token <{}> - Decryption OK.", safeHash);

            // Decrypt the token.
            String decryptedToken = jweObject.getPayload().toString();

            LOGGER.debug("Token <{}> - Token decrypted successfully.", safeHash);

            return decryptedToken;
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new TokenNotParseableJOSEJWTException(e);
        } catch (JOSEException e) {
            LOGGER.error(e.getMessage(), e);
            throw new JOSEJWTException(e);
        }
    }

    // ------------------------------------------ Private methods.

    /**
     * Returns a {@link JWEDecrypter} instance based on the private key.
     * @return the appropriate JWE decrypter.
     * @throws JOSEJWTException if an error occurs.
     */
    private JWEDecrypter getJWEDecrypter(JWK jwk)
        throws JOSEJWTException {
        try {
            if (jwk instanceof ECKey) {
                return new ECDHDecrypter((ECKey) jwk);
            } else if (jwk instanceof RSAKey) {
                return new RSADecrypter((RSAKey) jwk);
            } else if (jwk instanceof OctetSequenceKey) {
                return new DirectDecrypter((OctetSequenceKey) jwk);
            } else {
                throw new UnsupportedKeyTypeJOSEJWTException(jwk.getKeyType().getValue());
            }
        } catch (JOSEException e) {
            LOGGER.error(e.getMessage(), e);
            throw new JOSEJWTException(e);
        }
    }

    /**
     * Gets the decryption JWK.
     * @param kid the KID.
     * @param keyUse the key use.
     * @return the corresponding JWK.
     */
    private JWK getDecryptionJWK(String kid, KeyUse keyUse)
        throws JOSEJWTException {
        //@formatter:off
        JWKMatcher.Builder jwkMatcherBuilder = new JWKMatcher.Builder()
            .keyUse(keyUse)
            .privateOnly(true)
            .keyID(kid);
        //@formatter:on

        // Get the JWK matching.
        JWKSet jwkSet = this.jwkSetProvider.toJWKSet();
        JWKSelector jwkSelector = new JWKSelector(jwkMatcherBuilder.build());
        List<JWK> jwks = jwkSelector.select(jwkSet);

        // No key matching. Refresh & Retry.
        if (jwks.isEmpty() && this.jwkSetProvider.refreshJWKSet()) {
            jwkSet = this.jwkSetProvider.toJWKSet();
            jwks = jwkSelector.select(jwkSet);
        }

        int keyCount = jwks.size();

        // Reject if no keys.
        if (keyCount == Integers.ZERO) {
            LOGGER.error("No key found for kid <{}> and use <{}>.", kid, keyUse);
            throw new UnresolvableKeyJOSEJWTException(kid, keyUse.identifier());
        }

        // Reject if too much keys.
        if (keyCount > Integers.ONE) {
            LOGGER.error("<{}> keys found for kid <{}> and use <{}>. Please check your JWKS configuration.", keyCount, kid, keyUse);
            throw new UnresolvableKeyJOSEJWTException(kid, keyUse.identifier());
        }

        return jwks.get(0);
    }
}
