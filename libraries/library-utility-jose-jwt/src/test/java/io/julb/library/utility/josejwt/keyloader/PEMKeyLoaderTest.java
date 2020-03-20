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
package io.julb.library.utility.josejwt.keyloader;

import io.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.internalservererror.InvalidPEMKeyFormatException;
import io.julb.library.utility.josejwt.exceptions.internalservererror.InvalidPasswordPEMKeyException;
import io.julb.library.utility.josejwt.exceptions.internalservererror.MissingPasswordPEMKeyException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A loader to build {@link java.security.PublicKey} and {@link java.security.PrivateKey} from PEM files.
 * <P>
 * @author Julb.
 */
public final class PEMKeyLoaderTest {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PEMKeyLoader.class);

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    private PEMKeyLoaderTest() {
        super();
    }

    // ------------------------------------------ Utility methods.

    /**
     * Loads a public key.
     * @param inputStream the input stream.
     * @return the public key.
     * @throws JOSEJWTException if an error occurs.
     */
    public static PublicKey loadPublicKey(InputStream inputStream)
        throws JOSEJWTException {
        Security.addProvider(new BouncyCastleProvider());

        LOGGER.info("Loading PEM public key from input stream.");

        try (PEMParser pemParser = new PEMParser(new InputStreamReader(inputStream))) {
            Object o = pemParser.readObject();

            // If null, it means that the provided element is not a PEM key.
            if (o == null) {
                throw new InvalidPEMKeyFormatException();
            }

            if (o instanceof PEMKeyPair) {
                PEMKeyPair pemkeyPair = (PEMKeyPair) o;

                // Convert to Java (JCA) format
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                PublicKey publicKey = converter.getKeyPair(pemkeyPair).getPublic();

                LOGGER.debug("Public key successfully loaded from a keypair.");
                return publicKey;
            } else if (o instanceof SubjectPublicKeyInfo) {
                // Generate key public
                SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) o;

                // Convert to Java (JCA) format
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);

                LOGGER.debug("Public key successfully loaded from a pubkey.");
                return publicKey;
            } else {
                LOGGER.error("Unable to determine private key type from instance {}", o.getClass().getName());
                throw new JOSEJWTException("Unable to determine private key type from instance.");
            }
        } catch (IOException e) {
            throw new JOSEJWTException(e);
        }
    }

    /**
     * Loads a private key from a file not protected by password.
     * @param inputStream the private key file.
     * @return the key pair
     * @throws JOSEJWTException if an error occurs.
     */
    public static Pair<PrivateKey, PublicKey> loadPrivateKey(InputStream inputStream)
        throws JOSEJWTException {
        Security.addProvider(new BouncyCastleProvider());

        LOGGER.info("Loading PEM unprotected private key from input stream.");

        try (PEMParser pemParserSign = new PEMParser(new InputStreamReader(inputStream))) {
            Object o = pemParserSign.readObject();

            // If null, it means that the provided element is not a PEM key.
            if (o == null) {
                throw new InvalidPEMKeyFormatException();
            }

            if (o instanceof PEMKeyPair) {
                PEMKeyPair pemkeyPair = (PEMKeyPair) o;

                // Convert to Java (JCA) format
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                KeyPair keyPair = converter.getKeyPair(pemkeyPair);

                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();

                LOGGER.debug("PEM unprotected private key loaded from a keypair.");
                return new ImmutablePair<PrivateKey, PublicKey>(privateKey, publicKey);
            } else if (o instanceof PEMEncryptedKeyPair) {
                LOGGER.error("Private key is encrypted and no password provided.");
                throw new MissingPasswordPEMKeyException();
            } else if (o instanceof SubjectPublicKeyInfo) {
                LOGGER.error("Provided key is a public one.");
                throw new InvalidPEMKeyFormatException("Public key provided. Expected private one.");
            } else {
                LOGGER.error("Unable to determine private key type from instance {}", o.getClass().getName());
                throw new JOSEJWTException("Unable to determine private key type from instance.");
            }
        } catch (IOException e) {
            throw new JOSEJWTException(e);
        }
    }

    /**
     * Loads a private key from an input stream protected by a password.
     * @param inputStream the input stream.
     * @param password the password.
     * @return the keypair.
     * @throws JOSEJWTException if an error occurs.
     */
    public static Pair<PrivateKey, PublicKey> loadPasswordProtectedPrivateKey(InputStream inputStream, String password)
        throws JOSEJWTException {
        Security.addProvider(new BouncyCastleProvider());

        LOGGER.info("Loading PEM password-protected private key from input stream.");

        try (PEMParser pemParserSign = new PEMParser(new InputStreamReader(inputStream))) {
            Object o = pemParserSign.readObject();

            // If null, it means that the provided element is not a PEM key.
            if (o == null) {
                throw new InvalidPEMKeyFormatException();
            }

            // Build a converter.
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (o instanceof PEMKeyPair) {
                // Raw key.
                PEMKeyPair pemkeyPair = (PEMKeyPair) o;

                LOGGER.warn("Private key should be encrypted but this is not the case. Ignoring password parameter.");

                // Convert to Java (JCA) format
                KeyPair keyPair = converter.getKeyPair(pemkeyPair);
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();

                LOGGER.debug("PEM Private key loaded from a keypair.");
                return new ImmutablePair<PrivateKey, PublicKey>(privateKey, publicKey);
            } else if (o instanceof PEMEncryptedKeyPair) {
                LOGGER.debug("Uncrypting private key with provided password.");

                // Encrypted key.
                PEMEncryptedKeyPair pemEncryptedKeyPair = (PEMEncryptedKeyPair) o;

                // Build a PEM decryptor with the password.
                PEMDecryptorProvider pemDecryptorProvider = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());

                // Decrypt the password file.
                PEMKeyPair decryptedKeyPair = pemEncryptedKeyPair.decryptKeyPair(pemDecryptorProvider);

                // Extract private key.
                KeyPair keyPair = converter.getKeyPair(decryptedKeyPair);
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();

                LOGGER.debug("PEM Private key loaded from a protected file.");
                return new ImmutablePair<PrivateKey, PublicKey>(privateKey, publicKey);
            } else if (o instanceof SubjectPublicKeyInfo) {
                LOGGER.error("Provided key is a public one.");
                throw new InvalidPEMKeyFormatException("Public key provided. Expected private one.");
            } else {
                LOGGER.error("Unable to determine private key type from instance {}", o.getClass().getName());
                throw new JOSEJWTException("Unable to determine private key type from instance.");
            }
        } catch (EncryptionException e) {
            throw new InvalidPasswordPEMKeyException(e);
        } catch (IOException e) {
            throw new JOSEJWTException(e);
        }
    }
}
