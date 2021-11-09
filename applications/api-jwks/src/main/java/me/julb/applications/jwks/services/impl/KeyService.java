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
package me.julb.applications.jwks.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.julb.applications.jwks.configurations.properties.ApplicationProperties;
import me.julb.applications.jwks.configurations.properties.JwkDefinitionProperties;
import me.julb.applications.jwks.configurations.properties.JwkProperties;
import me.julb.applications.jwks.services.IKeyService;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import me.julb.library.utility.josejwt.jwk.IJWKProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualAsymmetricJWKProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualJWKSetProvider;
import me.julb.library.utility.josejwt.jwk.impl.ManualSymmetricJWKProvider;
import me.julb.library.utility.josejwt.keyloader.PEMKeyLoader;

/**
 * The service to handle keyset service.
 * <br>
 * @author Julb.
 */
@Service
@Slf4j
public class KeyService implements IKeyService {

    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public String findAll(@NotNull String keysetName) {
        LOGGER.info("Loading keyset <{}>.", keysetName);

        // Find the JWK properties.
        JwkProperties jwkProperties = getJwkPropertiesByName(keysetName);
        if (jwkProperties == null) {
            throw new ResourceNotFoundException(JwkProperties.class, "name", keysetName);
        }

        // Build a collection of JWKSet
        Collection<IJWKProvider> jwkProviders = new ArrayList<>();

        // Asymmetric definition
        for (JwkDefinitionProperties jwkDefinition : jwkProperties.getDefinitions()) {
            // Build a JWK provider.
            IJWKProvider jwkProvider = buildJwkProvider(keysetName, jwkDefinition, jwkProperties.getPublicKeysOnly());

            // Add it to the list.
            CollectionUtils.addIgnoreNull(jwkProviders, jwkProvider);
        }

        //@formatter:off
        return new ManualJWKSetProvider.Builder()
            .addAllJWKProviders(jwkProviders)
            .build()
            .toJSONString(jwkProperties.getPublicKeysOnly());
        //@formatter:on
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findByKeyId(@NotNull String keysetName, @NotNull String keyId) {
        // Find the JWK properties.
        JwkProperties jwkProperties = getJwkPropertiesByName(keysetName);
        if (jwkProperties == null) {
            throw new ResourceNotFoundException(JwkProperties.class, "name", keysetName);
        }

        // Find the JWK definition.
        JwkDefinitionProperties jwkDefinition = null;
        for (JwkDefinitionProperties def : jwkProperties.getDefinitions()) {
            if (StringUtils.equalsIgnoreCase(def.getKeyId(), keyId)) {
                jwkDefinition = def;
                break;
            }
        }

        // Key ID not found.
        if (jwkDefinition == null) {
            throw new ResourceNotFoundException(JwkProperties.class, Map.<String, String> of("name", keysetName, "keyId", keyId, "publicKeysOnly", jwkProperties.getPublicKeysOnly().toString()));
        }

        // Build a JWK provider.
        IJWKProvider jwkProvider = buildJwkProvider(keysetName, jwkDefinition, jwkProperties.getPublicKeysOnly());
        if (jwkProvider == null) {
            throw new ResourceNotFoundException(JwkProperties.class, Map.<String, String> of("name", keysetName, "keyId", keyId, "publicKeysOnly", jwkProperties.getPublicKeysOnly().toString()));
        }

        // Build a JSON string.
        return jwkProvider.toJSONString();
    }

    // ------------------------------------------ Private methods.

    /**
     * Gets the JWKS properties by name.
     * @param keysetName the keyset by name.
     * @return the JWKS properties.
     */
    private JwkProperties getJwkPropertiesByName(String keysetName) {
        List<JwkProperties> keys = applicationProperties.getJwks().getKeys();
        for (JwkProperties key : keys) {
            if (StringUtils.equalsIgnoreCase(key.getName(), keysetName)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Builds a JWK provider instance from a JWK definition.
     * @param keysetName the keyset by name.
     * @param jwkDefinition the JWK definition.
     * @param publicKeysOnly export public keys only.
     * @return the JWK provider instance.
     */
    private IJWKProvider buildJwkProvider(String keysetName, JwkDefinitionProperties jwkDefinition, Boolean publicKeysOnly) {
        try {
            IJWKProvider jwkProvider = null;

            if (JwkDefinitionProperties.JwkInstanceType.ASYMMETRIC.equals(jwkDefinition.getType())) {
                // Extract PEM.
                InputStream pemKey = null;
                if (StringUtils.isNotBlank(jwkDefinition.getKey())) {
                    pemKey = IOUtils.toInputStream(jwkDefinition.getKey(), StandardCharsets.UTF_8);
                } else if (jwkDefinition.getPath() != null) {
                    pemKey = jwkDefinition.getPath().getInputStream();
                }

                KeyPair loadKeyPair = PEMKeyLoader.loadKey(pemKey);

                PrivateKey privateKey = null;
                PublicKey publicKey = null;

                if (publicKeysOnly) {
                    publicKey = loadKeyPair.getPublic();
                } else {
                    privateKey = loadKeyPair.getPrivate();
                    publicKey = loadKeyPair.getPublic();
                }

                //@formatter:off
                jwkProvider = new ManualAsymmetricJWKProvider.Builder()
                    .algorithm(jwkDefinition.getAlgorithm())
                    .keyId(jwkDefinition.getKeyId())
                    .keyPair(privateKey, publicKey)
                    .use(jwkDefinition.getUse())
                    .build();
                //@formatter:on
            } else if (!publicKeysOnly && JwkDefinitionProperties.JwkInstanceType.SYMMETRIC.equals(jwkDefinition.getType())) {
                // Extract secret key.
                String secretKey = null;
                if (StringUtils.isNotBlank(jwkDefinition.getKey())) {
                    secretKey = jwkDefinition.getKey();
                } else if (jwkDefinition.getPath() != null) {
                    secretKey = IOUtils.toString(jwkDefinition.getPath().getInputStream(), StandardCharsets.UTF_8);
                }

                //@formatter:off
                jwkProvider = new ManualSymmetricJWKProvider.Builder()
                    .algorithm(jwkDefinition.getAlgorithm())
                    .keyId(jwkDefinition.getKeyId())
                    .secretKey(secretKey)
                    .use(jwkDefinition.getUse())
                    .build();
                //@formatter:on
            } else {
                return null;
            }

            // Force validation.
            jwkProvider.toJWK();

            // Return provider.
            return jwkProvider;
        } catch (JOSEJWTException | IOException e) {
            LOGGER.error("Unable to load JWK from key definition <{}/{}>", keysetName, jwkDefinition.getKeyId());
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

}
