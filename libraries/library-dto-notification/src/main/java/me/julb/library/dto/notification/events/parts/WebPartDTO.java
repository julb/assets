/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package me.julb.library.dto.notification.events.parts;

import java.util.Collection;
import java.util.HashSet;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.notification.events.WebNotificationPriority;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.DateTimeInFuture;

/**
 * A DTO configuring web notification.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class WebPartDTO {

    //@formatter:off
     /**
     * The TOs recipients of the notification.
     * <P>
     * User identifiers are expected.
     * -- GETTER --
     * Getter for {@link #tos} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tos} property.
     * @param tos the value to set.
     */
     //@formatter:on
    @NotEmpty
    @Valid
    private Collection<UserRefDTO> tos = new HashSet<>();

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
    private WebNotificationPriority priority = WebNotificationPriority.L3_LOW;

}
