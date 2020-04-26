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

package io.julb.applications.authorizationserver.services.dto.mail;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to update user mail.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class UserMailUpdateDTO {

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
    @Schema(description = "Flag to indicate if the address is the primary", required = true)
    @NotNull
    private Boolean primary;

}
