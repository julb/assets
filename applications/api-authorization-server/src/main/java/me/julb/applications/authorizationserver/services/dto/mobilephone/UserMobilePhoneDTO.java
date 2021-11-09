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

package me.julb.applications.authorizationserver.services.dto.mobilephone;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.audit.AbstractAuditedDTO;
import me.julb.library.dto.simple.mobilephone.MobilePhoneDTO;

/**
 * The DTO used to get user mobile phones.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserMobilePhoneDTO extends AbstractAuditedDTO {

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
     * The mobilePhone attribute.
     * -- GETTER --
     * Getter for {@link #mobilePhone} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mobilePhone} property.
     * @param mobilePhone the value to set.
     */
     //@formatter:on
    @Schema(description = "Mobile phone of the user")
    private MobilePhoneDTO mobilePhone;

    //@formatter:off
     /**
     * The primary attribute.
     * -- GETTER --
     * Getter for {@link #primary} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #primary} property.
     * @param primary the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag to indicate if the address is the primary")
    private Boolean primary;

    //@formatter:off
     /**
     * The verified attribute.
     * -- GETTER --
     * Getter for {@link #verified} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #verified} property.
     * @param verified the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag to indicate if the address is verified")
    private Boolean verified;

}
