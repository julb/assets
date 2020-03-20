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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.julb.library.utility.josejwt.digest.TokenDigestUtility;
import io.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.TokenNotParseableJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.internalservererror.UnsupportedJWSAlgorithmJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.UnsupportedKeyTypeJOSEJWTException;
import io.julb.library.utility.josejwt.jwk.IJWKProvider;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The operation that signs a token with a private key.
 * <P>
 * @author Julb.
 */
public class TokenSignatureOperation {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSignatureOperation.class);

    /**
     * The private key used to sign the token.
     */
    private IJWKProvider jwkProvider;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     * @param jwkProvider the JWK provider.
     */
    public TokenSignatureOperation(IJWKProvider jwkProvider) {
        super();
        this.jwkProvider = jwkProvider;
    }

    // ------------------------------------------ Utility methods.

    /**
     * Execute the operation.
     * @param token the token to sign.
     * @return the token signed with the key.
     * @throws JOSEJWTException if an error occurs.
     */
    public String execute(String token)
        throws JOSEJWTException {
        try {
            String safeHash = TokenDigestUtility.hash(token);

            LOGGER.debug("Token <{}> - Signing the token.", safeHash);

            // Extract JWK.
            JWK jwk = this.jwkProvider.toJWK();
            JWSSigner jwsSigner = this.getJWSSigner(jwk);
            JWSAlgorithm jwsAlgorithm = this.getJWSAlgorithm(jwk);
            Set<JWSAlgorithm> supportedJWSAlgorithms = jwsSigner.supportedJWSAlgorithms();
            if (!supportedJWSAlgorithms.contains(jwsAlgorithm)) {
                List<String> algorithmList = supportedJWSAlgorithms.stream().map(JWSAlgorithm::getName).collect(Collectors.toList());
                throw new UnsupportedJWSAlgorithmJOSEJWTException(algorithmList, jwsAlgorithm.getName());
            }

            // Parse the token
            JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(token);

            // Sign the token.
            //@formatter:off
            JWSHeader jwsHeader = new JWSHeader.Builder(jwsAlgorithm)
                .keyID(jwk.getKeyID())
                .build();
            //@formatter:on

            SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
            signedJWT.sign(jwsSigner);
            String signedSerializedToken = signedJWT.serialize();

            LOGGER.debug("Token <{}> - Token signed successfully.", safeHash);

            return signedSerializedToken;
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
     * Returns a {@link JWSSigner} instance based on the private key.
     * @param jwk the JWK.
     * @return the appropriate JWS signer.
     * @throws JOSEJWTException if an error occurs.
     */
    private JWSSigner getJWSSigner(JWK jwk)
        throws JOSEJWTException {
        try {
            if (jwk instanceof ECKey) {
                return new ECDSASigner((ECKey) jwk);
            } else if (jwk instanceof RSAKey) {
                return new RSASSASigner((RSAKey) jwk);
            } else if (jwk instanceof OctetSequenceKey) {
                return new MACSigner((OctetSequenceKey) jwk);
            } else {
                throw new UnsupportedKeyTypeJOSEJWTException(jwk.getKeyType().getValue());
            }
        } catch (JOSEException e) {
            LOGGER.error(e.getMessage(), e);
            throw new JOSEJWTException(e);
        }
    }

    /**
     * Returns a {@link JWSAlgorithm} to use based on the private key.
     * @param jwk the JWK.
     * @return the appropriate JWS algorithm.
     * @throws JOSEJWTException if an error occurs.
     */
    private JWSAlgorithm getJWSAlgorithm(JWK jwk)
        throws JOSEJWTException {
        if (jwk.getAlgorithm() != null) {
            return new JWSAlgorithm(jwk.getAlgorithm().getName());
        } else if (jwk instanceof ECKey) {
            return JWSAlgorithm.ES384;
        } else if (jwk instanceof RSAKey) {
            return JWSAlgorithm.RS384;
        } else if (jwk instanceof OctetSequenceKey) {
            return JWSAlgorithm.HS256;
        } else {
            throw new UnsupportedKeyTypeJOSEJWTException(jwk.getKeyType().getValue());
        }
    }
}
