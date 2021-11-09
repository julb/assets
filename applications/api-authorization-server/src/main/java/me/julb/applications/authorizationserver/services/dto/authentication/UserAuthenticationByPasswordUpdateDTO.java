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

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;

/**
 * The DTO used to update user authentication.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserAuthenticationByPasswordUpdateDTO extends AbstractUserAuthenticationUpdateDTO {

    //@formatter:off
     /**
     * The mfaEnabled attribute.
     * -- GETTER --
     * Getter for {@link #mfaEnabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mfaEnabled} property.
     * @param mfaEnabled the value to set.
     */
     //@formatter:on
    @Schema(description = "Enable or not the MFA on this authentication", required = true)
    @NotNull
    private Boolean mfaEnabled;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthenticationType getType() {
        return UserAuthenticationType.PASSWORD;
    }
}
