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

package me.julb.applications.platformhealth.services.dto.incident;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.content.LargeContentUpdateDTO;
import me.julb.library.dto.simple.content.ShortContentUpdateDTO;
import me.julb.library.utility.validator.constraints.Tag;

/**
 * The DTO used to update an incident.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class IncidentUpdateDTO {

    //@formatter:off
     /**
     * The localizedTitle attribute.
     * -- GETTER --
     * Getter for {@link #localizedTitle} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedTitle} property.
     * @param localizedTitle the value to set.
     */
     //@formatter:on
    @Schema(description = "Localized title for the incident", required = true)
    @NotNull
    @Size(min = 1)
    @Valid
    private Map<String, ShortContentUpdateDTO> localizedTitle;

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
    @Schema(description = "Localized message for the incident", required = true)
    @NotNull
    @Size(min = 1)
    @Valid
    private Map<String, LargeContentUpdateDTO> localizedMessage;

    //@formatter:off
     /**
     * The severity attribute.
     * -- GETTER --
     * Getter for {@link #severity} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #severity} property.
     * @param severity the value to set.
     */
     //@formatter:on
    @Schema(description = "Severity of the incident", required = true)
    @NotNull
    private IncidentSeverity severity;

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
    @Schema(description = "Tags to associate to the incident")
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();
}
