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

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import io.julb.library.utility.josejwt.exceptions.internalservererror.InvalidKeyFormatException;
import io.julb.library.utility.josejwt.exceptions.internalservererror.MissingFieldInKeyFormatException;
import io.julb.library.utility.josejwt.jwk.IJWKProvider;

import java.text.ParseException;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The symmetric JWK.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ManualSymmetricJWKProvider implements IJWKProvider {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ManualSymmetricJWKProvider.class);

    //@formatter:off
     /**
     * The keyId attribute.
     * -- GETTER --
     * Getter for {@link #keyId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #keyId} property.
     * @param keyId the value to set.
     */
     //@formatter:on
    private String keyId;

    //@formatter:off
     /**
     * The secretKey attribute.
     * -- GETTER --
     * Getter for {@link #secretKey} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #secretKey} property.
     * @param secretKey the value to set.
     */
     //@formatter:on
    private String secretKey;

    //@formatter:off
     /**
     * The algorithm attribute.
     * -- GETTER --
     * Getter for {@link #algorithm} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #algorithm} property.
     * @param algorithm the value to set.
     */
     //@formatter:on
    private String algorithm;

    //@formatter:off
     /**
     * The use attribute.
     * -- GETTER --
     * Getter for {@link #use} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #use} property.
     * @param use the value to set.
     */
     //@formatter:on
    private String use;

    /**
     * {@inheritDoc}
     */
    @Override
    public JWK toJWK() {
        if (StringUtils.isBlank(secretKey)) {
            throw new MissingFieldInKeyFormatException("secretKey");
        }
        if (StringUtils.isBlank(keyId)) {
            throw new MissingFieldInKeyFormatException("keyId");
        }
        if (StringUtils.isBlank(algorithm)) {
            throw new MissingFieldInKeyFormatException("algorithm");
        }
        if (StringUtils.isBlank(use)) {
            throw new MissingFieldInKeyFormatException("use");
        }
        try {
            //@formatter:off
            return new OctetSequenceKey.Builder(secretKey.getBytes())
                .keyID(keyId)
                .algorithm(new Algorithm(algorithm))
                .keyUse(KeyUse.parse(use))
                .build();
            //@formatter:on
        } catch (ParseException e) {
            LOGGER.error("Unsupported 'use' value for symmetric key : <{}>.", use);
            LOGGER.error(e.getMessage(), e);
            throw new InvalidKeyFormatException(e);
        }
    }

    /**
     * The symmetic JWK.
     * <P>
     * @author Julb.
     */
    public static class Builder {

        //@formatter:off
         /**
         * The instance attribute.
         * -- GETTER --
         * Getter for {@link #instance} property.
         * @return the value.
         * -- SETTER --
         * Setter for {@link #instance} property.
         * @param instance the value to set.
         */
         //@formatter:on
        private ManualSymmetricJWKProvider instance;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         */
        public Builder() {
            super();
            this.instance = new ManualSymmetricJWKProvider();
        }

        // ------------------------------------------ Getters/Setters.

        /**
         * Setter for property keyId.
         * @param keyId New value of property keyId.
         * @return the builder instance.
         */
        public Builder keyId(String keyId) {
            this.instance.keyId = keyId;
            return this;
        }

        /**
         * Setter for property secretKey.
         * @param secretKey New value of property secretKey.
         * @return the builder instance.
         */
        public Builder secretKey(String secretKey) {
            this.instance.secretKey = secretKey;
            return this;
        }

        /**
         * Setter for property algorithm.
         * @param algorithm New value of property algorithm.
         * @return the builder instance.
         */
        public Builder algorithm(String algorithm) {
            this.instance.algorithm = algorithm;
            return this;
        }

        /**
         * Setter for property use for encryption.
         * @return the builder instance.
         */
        public Builder useForEncryption() {
            this.instance.use = KeyUse.ENCRYPTION.identifier();
            return this;
        }

        /**
         * Setter for property use.
         * @return the builder instance.
         */
        public Builder useForSignature() {
            this.instance.use = KeyUse.SIGNATURE.identifier();
            return this;
        }

        /**
         * Setter for property use.
         * @param use New value of property use.
         * @return the builder instance.
         */
        public Builder use(String use) {
            this.instance.use = use;
            return this;
        }

        /**
         * Returns the built instance.
         * @return the instance.
         */
        public ManualSymmetricJWKProvider build() {
            return this.instance;
        }
    }
}
