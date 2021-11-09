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

package me.julb.applications.authorizationserver.services.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.SecurePassword;

/**
 * The DTO used to change the password of an authentication.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserAuthenticationByPasswordPasswordChangeDTO {

    //@formatter:off
     /**
     * The oldPassword attribute.
     * -- GETTER --
     * Getter for {@link #oldPassword} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #oldPassword} property.
     * @param oldPassword the value to set.
     */
     //@formatter:on
    @Schema(description = "Old password for the authentication", required = true)
    @NotNull
    @NotBlank
    @SecurePassword
    private String oldPassword;

    //@formatter:off
     /**
     * The newPassword attribute.
     * -- GETTER --
     * Getter for {@link #newPassword} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #newPassword} property.
     * @param newPassword the value to set.
     */
     //@formatter:on
    @Schema(description = "New password for the authentication", required = true)
    @NotNull
    @NotBlank
    @SecurePassword
    private String newPassword;
}
