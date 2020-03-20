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

package me.julb.applications.announcement.services.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.SortedSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.content.LargeContentPatchDTO;
import me.julb.library.dto.simple.interval.date.DateTimeIntervalPatchDTO;
import me.julb.library.utility.validator.constraints.DateTimeInFuture;
import me.julb.library.utility.validator.constraints.Tag;

/**
 * The DTO used to patch an announcement.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class AnnouncementPatchDTO {

    //@formatter:off
     /**
     * The visibilityDateTime attribute.
     * -- GETTER --
     * Getter for {@link #visibilityDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #visibilityDateTime} property.
     * @param visibilityDateTime the value to set.
     */
     //@formatter:on
    @Schema(description = "Datetime interval in which which the announcement is visible")
    @Valid
    @DateTimeInFuture
    private DateTimeIntervalPatchDTO visibilityDateTime;

   //@formatter:off
    /**
    * The level attribute.
    * -- GETTER --
    * Getter for {@link #level} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #level} property.
    * @param level the value to set.
    */
    //@formatter:on
    @Schema(description = "Classification level of the announcement")
    private AnnouncementLevel level;

   //@formatter:off
    /**
    * The localizedMessage attribute.
    * -- GETTER --
    * Getter for {@link #localizedMessage} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #localizedMessage} property.
    * @param localizedMessage the value to set.
    */
    //@formatter:on
    @Schema(description = "Localized message for the announcement")
    @Valid
    private Map<String, LargeContentPatchDTO> localizedMessage;

    //@formatter:off
     /**
     * The tags attribute.
     * -- GETTER --
     * Getter for {@link #tags} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tags} property.
     * @param tags the value to set.
     */
     //@formatter:on
    @Schema(description = "Tags to associate to the announcement")
    private SortedSet<@NotNull @Tag String> tags;
}
