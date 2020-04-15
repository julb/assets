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

import io.julb.library.dto.simple.content.LargeContentCreationDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to create an planned maintenance history.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class PlannedMaintenanceHistoryCreationDTO {

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
    @Schema(description = "Status of the planned maintenance", required = true)
    @NotNull
    private PlannedMaintenanceStatus status;

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
    @Schema(description = "Localized message for the planned maintenance history", required = true)
    @NotNull
    @Size(min = 1)
    @Valid
    private Map<String, LargeContentCreationDTO> localizedMessage;

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
    @Schema(description = "Flag to send notification related to this history of the planned maintenance", required = true)
    @NotNull
    private Boolean sendNotification;
}
