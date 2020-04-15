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

import io.julb.library.dto.simple.audit.AbstractAuditedDTO;
import io.julb.library.dto.simple.content.LargeContentDTO;
import io.julb.library.dto.simple.content.ShortContentDTO;
import io.julb.library.dto.simple.interval.date.DateTimeIntervalDTO;
import io.julb.library.dto.simple.user.UserRefDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to return an planned maintenance.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class PlannedMaintenanceDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Unique ID for the planned maintenance")
    private String id;

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
    private Map<String, ShortContentDTO> localizedTitle;

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
    private Map<String, LargeContentDTO> localizedMessage;

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
    private DateTimeIntervalDTO slotDateTime;

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
    @Schema(description = "Author of the planned maintenance")
    private UserRefDTO user;

    //@formatter:off
     /**
     * The status attribute.
     * -- GETTER --
     * Getter for {@link #status} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #status} property.
     * @param status the value to set.
     */
     //@formatter:on
    @Schema(description = "Status of the planned maintenance")
    private PlannedMaintenanceStatus status;

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
    private SortedSet<String> tags = new TreeSet<String>();
}
