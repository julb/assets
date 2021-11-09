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

package me.julb.applications.webnotification.services.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.notification.events.NotificationBusinessCategory;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.dto.notification.events.WebNotificationPriority;
import me.julb.library.dto.simple.audit.AbstractAuditedDTO;

/**
 * The DTO used to return a web notification.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class WebNotificationDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Unique ID for the web notification")
    private String id;

    //@formatter:off
     /**
     * The expiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #expiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #expiryDateTime} property.
     * @param expiryDateTime the value to set.
     */
     //@formatter:on
    @Schema(description = "Datetime at which the web notification expires")
    private String expiryDateTime;

    //@formatter:off
     /**
     * The priority attribute.
     * -- GETTER --
     * Getter for {@link #priority} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #priority} property.
     * @param priority the value to set.
     */
     //@formatter:on
    @Schema(description = "Priority of the web notification")
    private WebNotificationPriority priority;

    //@formatter:off
     /**
     * The kind attribute.
     * -- GETTER --
     * Getter for {@link #kind} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #kind} property.
     * @param kind the value to set.
     */
     //@formatter:on
    @Schema(description = "Kind of the web notification")
    private NotificationKind kind;

    //@formatter:off
     /**
     * The businessCategory attribute.
     * -- GETTER --
     * Getter for {@link #businessCategory} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #businessCategory} property.
     * @param businessCategory the value to set.
     */
     //@formatter:on
    @Schema(description = "Business category of the web notification")
    private NotificationBusinessCategory businessCategory;

    //@formatter:off
     /**
     * The parameters attribute.
     * -- GETTER --
     * Getter for {@link #parameters} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #parameters} property.
     * @param parameters the value to set.
     */
     //@formatter:on
    @Schema(description = "Parameters associated to the web notification")
    private Map<String, Object> parameters = new HashMap<>();

    //@formatter:off
     /**
     * The read attribute.
     * -- GETTER --
     * Getter for {@link #read} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #read} property.
     * @param read the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag indicating if the web notification is read")
    private Boolean read;
}
