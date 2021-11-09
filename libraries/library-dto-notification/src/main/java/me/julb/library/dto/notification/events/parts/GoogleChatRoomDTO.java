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
package me.julb.library.dto.notification.events.parts;

import java.util.Locale;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.GoogleChatRoom;
import me.julb.library.utility.validator.constraints.GoogleChatThreadKey;

/**
 * A DTO configuring google chat room notification.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleChatRoomDTO {

    //@formatter:off
     /**
     * The room attribute.
     * -- GETTER --
     * Getter for {@link #room} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #room} property.
     * @param room the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @GoogleChatRoom
    private String room;

    //@formatter:off
    /**
    * The locale attribute.
    * -- GETTER --
    * Getter for {@link #locale} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #locale} property.
    * @param locale the value to set.
    */
    //@formatter:on
    @NotNull
    private Locale locale;

    //@formatter:off
     /**
     * The threadKey attribute.
     * -- GETTER --
     * Getter for {@link #threadKey} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #threadKey} property.
     * @param threadKey the value to set.
     */
     //@formatter:on
    @GoogleChatThreadKey
    private String threadKey;

}
