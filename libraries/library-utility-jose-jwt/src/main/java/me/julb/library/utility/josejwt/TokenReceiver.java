/**
 * MIT License
 *
 * Copyright (c) 2017-2021 Julb
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
package me.julb.library.utility.josejwt;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import me.julb.library.utility.josejwt.digest.TokenDigestUtility;
import me.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import me.julb.library.utility.josejwt.jwk.IJWKSetProvider;
import me.julb.library.utility.josejwt.operations.TokenDecryptionOperation;
import me.julb.library.utility.josejwt.operations.TokenVerifierOperation;

/**
 * The JWT receiver.
 * <br>
 * @author Julb.
 */
@Slf4j
public class TokenReceiver {

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
     * @param expectedIssuer the expected issuer.
     * @param expectedAudience the expected audience.
     * @return the JSON web token decrypted and valid for processing.
     * @throws JOSEJWTException if an error occurs.
     */
    public String receive(String token, String expectedIssuer, String expectedAudience)
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

            // Hash for tracking purpose.
            String hash = TokenDigestUtility.hash(token);

            LOGGER.debug("Token <{}> - Start receiving.", hash);

            // 1. Decrypting the token.
            String decryptedToken = null;
            if (encryptionJWKSetProvider != null) {
                TokenDecryptionOperation tokenDecryptionOperation = new TokenDecryptionOperation(encryptionJWKSetProvider);
                decryptedToken = tokenDecryptionOperation.execute(token);
            } else {
                decryptedToken = token;
            }

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
