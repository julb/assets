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

package me.julb.applications.authorizationserver.services.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.security.UserRole;
import me.julb.library.dto.simple.audit.AbstractAuditedDTO;

/**
 * The DTO used to return an user.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class UserDTO extends AbstractAuditedDTO {

    //@formatter:off
     /**
     * The id attribute.
     * -- GETTER --
     * Getter for {@link #id} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #id} property.
     * @param id the value to set.
     */
     //@formatter:on
    @Schema(description = "Unique ID for the user")
    private String id;

    //@formatter:off
     /**
     * The mail attribute.
     * -- GETTER --
     * Getter for {@link #mail} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mail} property.
     * @param mail the value to set.
     */
     //@formatter:on
    @Schema(description = "Mail for the user")
    private String mail;

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
    @Schema(description = "Flag to indicate if the user account is enabled")
    private Boolean enabled;

    //@formatter:off
     /**
     * The accountNonLocked attribute.
     * -- GETTER --
     * Getter for {@link #accountNonLocked} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #accountNonLocked} property.
     * @param accountNonLocked the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag to indicate if the user account is not locked")
    private Boolean accountNonLocked;

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
    @Schema(description = "Roles of the user")
    private Collection<UserRole> roles;
}
