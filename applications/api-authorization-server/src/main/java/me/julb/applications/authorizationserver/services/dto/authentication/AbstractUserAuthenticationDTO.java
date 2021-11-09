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

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationType;
import me.julb.library.dto.simple.audit.AbstractAuditedDTO;

/**
 * The DTO used to get user authentication.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public abstract class AbstractUserAuthenticationDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Id of the object")
    private String id;

   //@formatter:off
    /**
    * The lastSuccessfulUseDateTime attribute.
    * -- GETTER --
    * Getter for {@link #lastSuccessfulUseDateTime} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #lastSuccessfulUseDateTime} property.
    * @param lastSuccessfulUseDateTime the value to set.
    */
    //@formatter:on
    @Schema(description = "Last successful use of this authentication")
    private String lastSuccessfulUseDateTime;

    /**
     * Gets the user authentication type.
     * @return the user authentication type.
     */
    public abstract UserAuthenticationType getType();

    /**
     * Returns <code>true</code> if MFA is enabled for this authentication, <code>false</code> otherwise.
     * @return <code>true</code> if MFA is enabled for this authentication, <code>false</code> otherwise.
     */
    public abstract Boolean getMfaEnabled();
}
