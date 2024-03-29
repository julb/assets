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

package me.julb.applications.authorizationserver.services.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.authorizationserver.constraints.UserFirstName;
import me.julb.applications.authorizationserver.constraints.UserLastName;

/**
 * The DTO used to create a user profile.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserProfileCreationDTO {

  //@formatter:off
    /**
    * The firstName attribute.
    * -- GETTER --
    * Getter for {@link #firstName} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #firstName} property.
    * @param firstName the value to set.
    */
    //@formatter:on
    @Schema(description = "First name of the user", required = true)
    @NotNull
    @NotBlank
    @UserFirstName
    private String firstName;

   //@formatter:off
    /**
    * The lastName attribute.
    * -- GETTER --
    * Getter for {@link #lastName} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #lastName} property.
    * @param lastName the value to set.
    */
    //@formatter:on
    @Schema(description = "Last name of the user", required = true)
    @NotNull
    @NotBlank
    @UserLastName
    private String lastName;
}
