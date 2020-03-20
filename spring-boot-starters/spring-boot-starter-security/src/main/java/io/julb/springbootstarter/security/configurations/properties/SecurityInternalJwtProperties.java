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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * The security properties.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.internal-jwt")
@Validated
public class SecurityInternalJwtProperties {

    //@formatter:off
     /**
     * The audience attribute.
     * -- GETTER --
     * Getter for {@link #audience} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #audience} property.
     * @param audience the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String audience;

    //@formatter:off
     /**
     * The issuer attribute.
     * -- GETTER --
     * Getter for {@link #issuer} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #issuer} property.
     * @param issuer the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String issuer;

    //@formatter:off
     /**
     * The signatureAlgorithm attribute.
     * -- GETTER --
     * Getter for {@link #signatureAlgorithm} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #signatureAlgorithm} property.
     * @param signatureAlgorithm the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String signatureAlgorithm;

    //@formatter:off
     /**
     * The signatureKey attribute.
     * -- GETTER --
     * Getter for {@link #signatureKey} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #signatureKey} property.
     * @param signatureKey the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String signatureKey;

    //@formatter:off
     /**
     * The signatureKeyId attribute.
     * -- GETTER --
     * Getter for {@link #signatureKeyId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #signatureKeyId} property.
     * @param signatureKeyId the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String signatureKeyId;
}
