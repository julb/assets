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
package me.julb.applications.authorizationserver.configurations.properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The security properties.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class AccessTokenJwtForgeryProperties {

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
    private String issuer;

    //@formatter:off
     /**
     * The coreAudience attribute.
     * -- GETTER --
     * Getter for {@link #coreAudience} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #coreAudience} property.
     * @param coreAudience the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String coreAudience;

    //@formatter:off
     /**
     * The validityInSeconds attribute.
     * -- GETTER --
     * Getter for {@link #validityInSeconds} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #validityInSeconds} property.
     * @param validityInSeconds the value to set.
     */
     //@formatter:on
    private Long validityInSeconds;

    //@formatter:off
     /**
     * The signature attribute.
     * -- GETTER --
     * Getter for {@link #signature} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #signature} property.
     * @param signature the value to set.
     */
     //@formatter:on
    private AccessTokenJwtKeyProperties signature;

}
