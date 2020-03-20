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

import io.julb.library.utility.josejwt.digest.TokenDigestUtility;
import io.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import io.julb.library.utility.josejwt.jwk.IJWKSetProvider;
import io.julb.library.utility.josejwt.operations.TokenDecryptionOperation;
import io.julb.library.utility.josejwt.operations.TokenVerifierOperation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JWT receiver.
 * <P>
 * @author Julb.
 */
public class TokenReceiver {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenReceiver.class);

    /**
     * The issuer to expect.
     */
    private String expectedIssuer;

    /**
     * The audience to expect.
     */
    private String expectedAudience;

    /**
     * The signatures keys.
     */
    private IJWKSetProvider signatureJWKSetProvider;

    /**
     * The encryption keys.
     */
    private IJWKSetProvider encryptionJWKSetProvider;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public TokenReceiver() {
        super();
    }

    // ------------------------------------------ Builder methods.

    /**
     * Setter for property expectedIssuer.
     * @param expectedIssuer New value of property expectedIssuer.
     * @return the current instance.
     */
    public TokenReceiver setExpectedIssuer(String expectedIssuer) {
        this.expectedIssuer = expectedIssuer;
        return this;
    }

    /**
     * Setter for property expectedAudience.
     * @param expectedAudience New value of property expectedAudience.
     * @return the current instance.
     */
    public TokenReceiver setExpectedAudience(String expectedAudience) {
        this.expectedAudience = expectedAudience;
        return this;
    }

    /**
     * Setter for property encryptionJWKSetProvider.
     * @param encryptionJWKSetProvider New value of property encryptionJWKSetProvider.
     * @return the current instance.
     */
    public TokenReceiver setEncryptionJWKSetProvider(IJWKSetProvider encryptionJWKSetProvider) {
        this.encryptionJWKSetProvider = encryptionJWKSetProvider;
        return this;
    }

    /**
     * Setter for property signatureJWKSetProvider.
     * @param signatureJWKSetProvider New value of property signatureJWKSetProvider.
     * @return the current instance.
     */
    public TokenReceiver setSignatureJWKSetProvider(IJWKSetProvider signatureJWKSetProvider) {
        this.signatureJWKSetProvider = signatureJWKSetProvider;
        return this;
    }

    // ------------------------------------------ Utility methods.

    /**
     * Receives a JSON web token signed and encrypted, decrypts it and check its signature.
     * @param token the ciphered and signed JSON web token.
     * @return the JSON web token decrypted and valid for processing.
     * @throws JOSEJWTException if an error occurs.
     */
    public String receive(String token)
        throws JOSEJWTException {
        try {
            // Check all informations are provided.
            if (StringUtils.isBlank(token)) {
                throw new IllegalArgumentException("token must not be blank");
            }

            if (StringUtils.isBlank(expectedIssuer)) {
                throw new IllegalArgumentException("issuer must not be blank");
            }

            if (StringUtils.isBlank(expectedAudience)) {
                throw new IllegalArgumentException("audience must not be blank");
            }

            if (signatureJWKSetProvider == null) {
                throw new IllegalArgumentException("signature key must be provided");
            }

            if (encryptionJWKSetProvider == null) {
                throw new IllegalArgumentException("encryption key must be provided");
            }

            // Hash for tracking purpose.
            String hash = TokenDigestUtility.hash(token);

            LOGGER.debug("Token <{}> - Start receiving.", hash);

            // 1. Decrypting the token.
            TokenDecryptionOperation tokenDecryptionOperation = new TokenDecryptionOperation(encryptionJWKSetProvider);
            String decryptedToken = tokenDecryptionOperation.execute(token);

            // 2. Check signature of token.
            TokenVerifierOperation tokenVerifierOperation = new TokenVerifierOperation(signatureJWKSetProvider, expectedAudience, expectedIssuer);
            String jwtClaims = tokenVerifierOperation.execute(decryptedToken);

            // 3. Finish
            LOGGER.debug("Token <{}> - Finish receiving.", hash);
            return jwtClaims;
        } catch (JOSEJWTException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }
}
