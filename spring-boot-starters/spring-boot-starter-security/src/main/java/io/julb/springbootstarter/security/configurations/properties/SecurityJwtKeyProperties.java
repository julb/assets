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
package io.julb.springbootstarter.security.configurations.properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

/**
 * The security properties.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@Validated
public class SecurityJwtKeyProperties {

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
    @NotNull
    @NotBlank
    private String algorithm;

    //@formatter:off
     /**
     * The keyPath attribute.
     * -- GETTER --
     * Getter for {@link #keyPath} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #keyPath} property.
     * @param keyPath the value to set.
     */
     //@formatter:on
    @NotNull
    private Resource keyPath;

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
    @NotNull
    @NotBlank
    private String keyId;

}