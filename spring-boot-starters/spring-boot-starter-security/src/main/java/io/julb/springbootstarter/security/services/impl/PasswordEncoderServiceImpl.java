/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package io.julb.springbootstarter.security.services.impl;

import io.julb.springbootstarter.security.services.PasswordEncoderService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * The password encoder service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
public class PasswordEncoderServiceImpl implements PasswordEncoderService {

    /**
     * The password encoder.
     */
    private PasswordEncoder genericPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public String hash(@NotNull @NotBlank String rawValue) {
        return DigestUtils.sha256Hex(rawValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(@NotNull @NotBlank String rawValue) {
        return genericPasswordEncoder.encode(hash(rawValue));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@NotNull @NotBlank String rawValue, @NotNull @NotBlank String securedValue) {
        return genericPasswordEncoder.matches(hash(rawValue), securedValue);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.
}
