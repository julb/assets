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
package me.julb.applications.jwks.services;

import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.Cacheable;

import me.julb.applications.jwks.configurations.caching.CacheConstants;
import me.julb.library.utility.validator.constraints.JSONWebKeyId;
import me.julb.library.utility.validator.constraints.JSONWebKeysetName;

/**
 * The keyset service.
 * <br>
 * @author Julb.
 */
public interface IKeyService {

    /**
     * Finds the keys using the keyset name.
     * @param keysetName the name.
     * @return the JSON representation of this keyset.
     */
    @Cacheable(value = CacheConstants.KEYSET_KEYS_CACHE_KEY, unless = "#result == null")
    String findAll(@NotNull @JSONWebKeysetName String keysetName);

    /**
     * Finds the keys by keyset name and key ID.
     * @param keysetName the keyset name.
     * @param keyId the key ID.
     * @return the JSON representation of this key.
     */
    @Cacheable(value = CacheConstants.KEYSET_KEY_CACHE_KEY, unless = "#result == null")
    String findByKeyId(@NotNull @JSONWebKeysetName String keysetName, @NotNull @JSONWebKeyId String keyId);
}
