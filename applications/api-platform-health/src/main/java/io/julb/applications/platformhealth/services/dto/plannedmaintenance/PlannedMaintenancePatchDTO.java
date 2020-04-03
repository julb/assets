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

package io.julb.applications.platformhealth.services.dto.plannedmaintenance;

import io.julb.library.dto.simple.content.LargeContentPatchDTO;
import io.julb.library.dto.simple.content.ShortContentPatchDTO;
import io.julb.library.dto.simple.interval.date.DateTimeIntervalPatchDTO;
import io.julb.library.utility.validator.constraints.DateTimeInFuture;
import io.julb.library.utility.validator.constraints.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.SortedSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to patch an planned maintenance.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class PlannedMaintenancePatchDTO {

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
    @Schema(description = "Localized title for the planned maintenance")
    @Valid
    private Map<String, ShortContentPatchDTO> localizedTitle;

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
    @Schema(description = "Localized message for the planned maintenance")
    @Valid
    private Map<String, LargeContentPatchDTO> localizedMessage;

    //@formatter:off
     /**
     * The slotDateTime attribute.
     * -- GETTER --
     * Getter for {@link #slotDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #slotDateTime} property.
     * @param slotDateTime the value to set.
     */
     //@formatter:on
    @Schema(description = "Slot of the planned maintenance")
    @DateTimeInFuture
    @Valid
    private DateTimeIntervalPatchDTO slotDateTime;

    //@formatter:off
     /**
     * The complexity attribute.
     * -- GETTER --
     * Getter for {@link #complexity} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #complexity} property.
     * @param complexity the value to set.
     */
     //@formatter:on
    @Schema(description = "Complexity of the planned maintenance")
    private PlannedMaintenanceComplexity complexity;

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
    @Schema(description = "Tags to associate to the planned maintenance")
    private SortedSet<@NotNull @Tag String> tags;
}
