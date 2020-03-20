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

package me.julb.applications.authorizationserver.services.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.authorizationserver.services.dto.user.UserDTO;

/**
 * A DTO to hold the user session credentials.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class UserSessionCredentialsDTO {

    //@formatter:off
     /**
     * The user attribute.
     * -- GETTER --
     * Getter for {@link #user} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #user} property.
     * @param user the value to set.
     */
     //@formatter:on
    @Schema(description = "User matching the authentication")
    private UserDTO user;

    //@formatter:off
     /**
     * The userSession attribute.
     * -- GETTER --
     * Getter for {@link #userSession} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #userSession} property.
     * @param userSession the value to set.
     */
     //@formatter:on
    @Schema(description = "User session corresponding to the credentials")
    private UserSessionDTO userSession;

    //@formatter:off
     /**
     * The credentials attribute.
     * -- GETTER --
     * Getter for {@link #credentials} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #credentials} property.
     * @param credentials the value to set.
     */
     //@formatter:on
    @Schema(description = "Credentials to match the given authentication.")
    private String credentials;

    //@formatter:off
     /**
     * The credentialsNonExpired attribute.
     * -- GETTER --
     * Getter for {@link #credentialsNonExpired} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #credentialsNonExpired} property.
     * @param credentialsNonExpired the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag indicating if the credentials are not expired.")
    private Boolean credentialsNonExpired;
}
