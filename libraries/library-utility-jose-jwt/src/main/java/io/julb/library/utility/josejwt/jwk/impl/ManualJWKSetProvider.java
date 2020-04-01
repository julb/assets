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

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

import io.julb.library.utility.josejwt.jwk.IJWKProvider;
import io.julb.library.utility.josejwt.jwk.IJWKSetProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

/**
 * The JWKS provider.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ManualJWKSetProvider implements IJWKSetProvider {

    //@formatter:off
     /**
     * The jwkProviders attribute.
     * -- GETTER --
     * Getter for {@link #jwkProviders} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #jwkProviders} property.
     * @param jwkProviders the value to set.
     */
     //@formatter:on
    private Collection<IJWKProvider> jwkProviders = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public JWKSet toJWKSet() {
        List<JWK> jwks = jwkProviders.stream().map(IJWKProvider::toJWK).collect(Collectors.toList());
        JWKSet jwkSet = new JWKSet(jwks);
        return jwkSet;
    }

    /**
     * The JWKSet builder.
     * <P>
     * @author Julb.
     */
    public static class Builder {

        /**
         * The built instance
         */
        private ManualJWKSetProvider instance;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         */
        public Builder() {
            super();
            this.instance = new ManualJWKSetProvider();
        }

        // ------------------------------------------ Getters/Setters.

        /**
         * Add a bunch of JWK Providers.
         * @param jwkProvider the JWT provider to add.
         * @return the builder instance.
         */
        public Builder addJWKProvider(IJWKProvider... jwkProvider) {
            return addAllJWKProviders(Arrays.asList(jwkProvider));
        }

        /**
         * Add a bunch of JWK Providers.
         * @param jwkProviders the JWT providers to add.
         * @return the builder instance.
         */
        public Builder addAllJWKProviders(Collection<IJWKProvider> jwkProviders) {
            this.instance.getJwkProviders().addAll(jwkProviders);
            return this;
        }

        /**
         * Returns the built instance.
         * @return the instance.
         */
        public ManualJWKSetProvider build() {
            return this.instance;
        }
    }
}
