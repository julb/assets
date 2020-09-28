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

package io.julb.applications.authorizationserver.services.dto.authentication;

import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import io.julb.library.utility.validator.constraints.SecurePassword;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to create a user authentication.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class UserAuthenticationByPasswordCreationDTO extends AbstractUserAuthenticationCreationDTO {

    //@formatter:off
     /**
     * The password attribute.
     * -- GETTER --
     * Getter for {@link #password} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #password} property.
     * @param password the value to set.
     */
     //@formatter:on
    @Schema(description = "Password for the authentication", required = true)
    @NotNull
    @NotBlank
    @SecurePassword
    private String password;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationType getType() {
        return UserAuthenticationType.PASSWORD;
    }
}
