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

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to get user session access token.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class UserSessionAccessTokenDTO {

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
    private String type;

    //@formatter:off
     /**
     * The accessToken attribute.
     * -- GETTER --
     * Getter for {@link #accessToken} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #accessToken} property.
     * @param accessToken the value to set.
     */
     //@formatter:on
    private String accessToken;

    //@formatter:off
     /**
     * The expiresIn attribute.
     * -- GETTER --
     * Getter for {@link #expiresIn} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #expiresIn} property.
     * @param expiresIn the value to set.
     */
     //@formatter:on
    private Long expiresIn;

    //@formatter:off
     /**
     * The expiresAt attribute.
     * -- GETTER --
     * Getter for {@link #expiresAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #expiresAt} property.
     * @param expiresAt the value to set.
     */
     //@formatter:on
    private String expiresAt;

}
