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

package me.julb.library.dto.webnotification;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.dto.notification.events.WebNotificationPriority;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.DateTimeInFuture;

/**
 * The web notification entity.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class WebNotificationMessageDTO {

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
     * The parameters attribute.
     * -- GETTER --
     * Getter for {@link #parameters} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #parameters} property.
     * @param parameters the value to set.
     */
     //@formatter:on
    private Map<String, Object> parameters = new HashMap<>();

    //@formatter:off
     /**
     * The users attribute.
     * -- GETTER --
     * Getter for {@link #users} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #users} property.
     * @param users the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private Collection<UserRefDTO> users = new HashSet<>();
}
