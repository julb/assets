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

import io.julb.library.utility.validator.constraints.HTTPLink;

import javax.validation.Valid;
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
@ConfigurationProperties(prefix = "security.jwt")
@Validated
public class SecurityJwtProperties {

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
     * The signatureJwksUrl attribute.
     * -- GETTER --
     * Getter for {@link #signatureJwksUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #signatureJwksUrl} property.
     * @param signatureJwksUrl the value to set.
     */
     //@formatter:on
    @HTTPLink
    private String signatureJwksUrl;

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
    @Valid
    private SecurityJwtKeyProperties signatureKey;

}
