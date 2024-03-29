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

package me.julb.applications.disclaimer.services.dto.agreement;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.disclaimer.services.dto.disclaimer.DisclaimerDTO;
import me.julb.library.dto.simple.audit.AbstractAuditedDTO;
import me.julb.library.dto.simple.user.UserRefDTO;

/**
 * The DTO used to return a disclaimer.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class AgreementDTO extends AbstractAuditedDTO {

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
    @Schema(description = "User who agrees the disclaimer")
    private UserRefDTO user;

    //@formatter:off
     /**
     * The disclaimer attribute.
     * -- GETTER --
     * Getter for {@link #disclaimer} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #disclaimer} property.
     * @param disclaimer the value to set.
     */
     //@formatter:on
    @Schema(description = "Disclaimer agreed by the user")
    private DisclaimerDTO disclaimer;

    //@formatter:off
     /**
     * The agreedAt attribute.
     * -- GETTER --
     * Getter for {@link #agreedAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #agreedAt} property.
     * @param agreedAt the value to set.
     */
     //@formatter:on
    @Schema(description = "Date/Time at which the disclaimer has been agreed by the user")
    private String agreedAt;

    //@formatter:off
     /**
     * The ipv4Address attribute.
     * -- GETTER --
     * Getter for {@link #ipv4Address} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ipv4Address} property.
     * @param ipv4Address the value to set.
     */
     //@formatter:on
    @Schema(description = "IPV4 address of the user")
    private String ipv4Address;
}
