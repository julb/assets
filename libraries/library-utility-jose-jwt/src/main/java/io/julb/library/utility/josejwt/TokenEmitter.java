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
import io.julb.library.utility.josejwt.jwk.IJWKProvider;
import io.julb.library.utility.josejwt.operations.TokenEncryptionOperation;
import io.julb.library.utility.josejwt.operations.TokenSignatureOperation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JWT emitter.
 * <P>
 * @author Julb.
 */
public class TokenEmitter {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEmitter.class);

    /**
     * The signature private key.
     */
    private IJWKProvider signatureJWKProvider;

    /**
     * The encryption key.
     */
    private IJWKProvider encryptionJWKProvider;

    // ------------------------------------------ Constructors.

    /**
     * Default Constructor.
     */
    public TokenEmitter() {
        super();
    }

    // ------------------------------------------ Builder methods.

    /**
     * Setter for property encryptionJWKProvider.
     * @param encryptionJWKProvider New value of property encryptionJWKProvider.
     * @return the current instance.
     */
    public TokenEmitter setEncryptionJWKProvider(IJWKProvider encryptionJWKProvider) {
        this.encryptionJWKProvider = encryptionJWKProvider;
        return this;
    }

    /**
     * Setter for property signatureJWKProvider.
     * @param signatureJWKProvider New value of property signatureJWKProvider.
     * @return the current instance.
     */
    public TokenEmitter setSignatureJWKProvider(IJWKProvider signatureJWKProvider) {
        this.signatureJWKProvider = signatureJWKProvider;
        return this;
    }

    // ------------------------------------------ Utility methods.

    /**
     * Emits a JSON web token signed and encrypted.
     * @param jwtClaims the raw JSON web token.
     * @return the JSON web token signed and encrypted.
     * @throws JOSEJWTException if an error occurs.
     */
    public String emit(String jwtClaims)
        throws JOSEJWTException {
        try {
            // Check all informations are provided.
            if (StringUtils.isBlank(jwtClaims)) {
                throw new IllegalArgumentException("token must not be blank");
            }

            if (this.signatureJWKProvider == null) {
                throw new IllegalArgumentException("signature private key must not be null");
            }

            // Hash for tracking purpose.
            String hash = TokenDigestUtility.hash(jwtClaims);
            LOGGER.debug("Token <{}> - Start emitting.", hash);

            // 1. Signing the token.
            TokenSignatureOperation tokenSignatureOperation = new TokenSignatureOperation(this.signatureJWKProvider);
            String signedToken = tokenSignatureOperation.execute(jwtClaims);

            // 2. Encrypting the token.
            if (this.encryptionJWKProvider != null) {
                TokenEncryptionOperation tokenEncryptionOperation = new TokenEncryptionOperation(this.encryptionJWKProvider);
                String encryptedToken = tokenEncryptionOperation.execute(signedToken);

                // 3. Return the token
                LOGGER.debug("Emitting token {} - Finish.", hash);
                return encryptedToken;
            } else {
                // 3. Return the token
                LOGGER.debug("Emitting token {} - Finish.", hash);
                return signedToken;
            }
        } catch (JOSEJWTException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

}
