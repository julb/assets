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

package io.julb.applications.announcement.services.dto;

import io.julb.library.dto.simple.audit.AbstractAuditedDTO;
import io.julb.library.dto.simple.content.ContentDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to return an announcement.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class AnnouncementDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Unique ID for the announcement", example = "2020-01-01T00:00:00.000Z")
    private String id;

    //@formatter:off
    /**
    * The visibilityFromDateTime attribute.
    * -- GETTER --
    * Getter for {@link #visibilityFromDateTime} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #visibilityFromDateTime} property.
    * @param visibilityFromDateTime the value to set.
    */
    //@formatter:on
    @Schema(description = "Datetime from which the announcement is visible", example = "2020-01-01T00:00:00.000Z")
    private String visibilityFromDateTime;

   //@formatter:off
    /**
    * The visibilityToDateTime attribute.
    * -- GETTER --
    * Getter for {@link #visibilityToDateTime} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #visibilityToDateTime} property.
    * @param visibilityToDateTime the value to set.
    */
    //@formatter:on
    @Schema(description = "Datetime until which the announcement is visible", example = "2020-01-01T00:00:00.000Z")
    private String visibilityToDateTime;

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
    private Map<String, ContentDTO> localizedMessage;
}
