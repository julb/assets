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

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.IPAddress;

/**
 * The DTO used to patch user session.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserSessionPatchDTO {

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
    @Schema(description = "IP V4 Address of the session")
    @IPAddress
    private String ipv4Address;

   //@formatter:off
    /**
    * The lastUseDateTime attribute.
    * -- GETTER --
    * Getter for {@link #lastUseDateTime} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #lastUseDateTime} property.
    * @param lastUseDateTime the value to set.
    */
    //@formatter:on
    @Schema(description = "Date/Time of the latest activity on this session")
    @DateTimeISO8601
    private String lastUseDateTime;

   //@formatter:off
    /**
    * The operatingSystem attribute.
    * -- GETTER --
    * Getter for {@link #operatingSystem} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #operatingSystem} property.
    * @param operatingSystem the value to set.
    */
    //@formatter:on
    @Schema(description = "Operating system used to run the session")
    @Size(max = 32)
    private String operatingSystem;

   //@formatter:off
    /**
    * The browser attribute.
    * -- GETTER --
    * Getter for {@link #browser} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #browser} property.
    * @param browser the value to set.
    */
    //@formatter:on
    @Schema(description = "Browser used to run the session")
    @Size(max = 32)
    private String browser;
}
