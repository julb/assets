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

package me.julb.applications.authorizationserver.services.dto.session;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to get user session id token.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserSessionAccessTokenWithIdTokenDTO extends UserSessionAccessTokenDTO {

    //@formatter:off
     /**
     * The idToken attribute.
     * -- GETTER --
     * Getter for {@link #idToken} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #idToken} property.
     * @param idToken the value to set.
     */
     //@formatter:on
    private String idToken;

    //@formatter:off
     /**
     * The idTokenExpiresIn attribute.
     * -- GETTER --
     * Getter for {@link #idTokenExpiresIn} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #idTokenExpiresIn} property.
     * @param idTokenExpiresIn the value to set.
     */
     //@formatter:on
    private Long idTokenExpiresIn;

    //@formatter:off
     /**
     * The idTokenExpiresAt attribute.
     * -- GETTER --
     * Getter for {@link #idTokenExpiresAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #idTokenExpiresAt} property.
     * @param idTokenExpiresAt the value to set.
     */
     //@formatter:on
    private String idTokenExpiresAt;

}
