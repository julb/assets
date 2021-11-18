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

package me.julb.applications.webnotification.entities;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.julb.library.dto.notification.events.NotificationBusinessCategory;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.dto.notification.events.WebNotificationPriority;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.DateTimeInFuture;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The web notification entity.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Document("web-notifications")
public class WebNotificationEntity extends AbstractAuditedEntity implements IIdentifiable {

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
    @Id
    @Identifier
    private String id;

    //@formatter:off
     /**
     * The tm attribute.
     * -- GETTER --
     * Getter for {@link #tm} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tm} property.
     * @param tm the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Trademark
    private String tm;

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
    @NotNull
    @DateTimeISO8601
    @DateTimeInFuture
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
    @NotNull
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
    @NotNull
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
    @NotNull
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
    private Map<String, Object> parameters;

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
    @NotNull
    private Boolean read;

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
    @NotNull
    @Valid
    private UserRefEntity user;
}
