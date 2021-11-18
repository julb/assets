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

package me.julb.applications.platformhealth.services.dto.plannedmaintenance;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.audit.AbstractAuditedDTO;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.dto.simple.user.UserRefDTO;

/**
 * The DTO used to return an planned maintenance history.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class PlannedMaintenanceHistoryDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Unique ID for the history")
    private String id;

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
     * The previousStatus attribute.
     * -- GETTER --
     * Getter for {@link #previousStatus} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #previousStatus} property.
     * @param previousStatus the value to set.
     */
     //@formatter:on
    @Schema(description = "Previous status of the planned maintenance")
    private PlannedMaintenanceStatus previousStatus;

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
     * The sendNotification attribute.
     * -- GETTER --
     * Getter for {@link #sendNotification} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #sendNotification} property.
     * @param sendNotification the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag to send notification related to this history of the planned maintenance")
    private Boolean sendNotification;
}
