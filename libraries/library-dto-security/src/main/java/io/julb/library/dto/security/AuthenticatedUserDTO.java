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
package io.julb.library.dto.security;

import java.util.Collection;
import java.util.HashSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The authenticated user DTO.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AuthenticatedUserDTO {

    //@formatter:off
     /**
     * The identity attribute.
     * -- GETTER --
     * Getter for {@link #identity} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #identity} property.
     * @param identity the value to set.
     */
     //@formatter:on
    private AuthenticatedUserIdentityDTO identity;

    //@formatter:off
     /**
     * The type attribute.
     * -- GETTER --
     * Getter for {@link #type} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #type} property.
     * @param type the value to set.
     */
     //@formatter:on
    private AuthenticatedUserTokenType type;

    //@formatter:off
     /**
     * The roles attribute.
     * -- GETTER --
     * Getter for {@link #roles} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #roles} property.
     * @param roles the value to set.
     */
     //@formatter:on
    private Collection<AuthenticatedUserRole> roles = new HashSet<>();

    //@formatter:off
     /**
     * The enabled attribute.
     * -- GETTER --
     * Getter for {@link #enabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #enabled} property.
     * @param enabled the value to set.
     */
     //@formatter:on
    private Boolean enabled;

    //@formatter:off
     /**
     * The permissions attribute.
     * -- GETTER --
     * Getter for {@link #permissions} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #permissions} property.
     * @param permissions the value to set.
     */
     //@formatter:on
    private Collection<String> permissions = new HashSet<>();

}
