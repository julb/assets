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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;

/**
 * A DTO configuring google chat rooms notification.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class GoogleChatPartDTO {

    //@formatter:off
     /**
     * The rooms attribute.
     * -- GETTER --
     * Getter for {@link #rooms} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #rooms} property.
     * @param rooms the value to set.
     */
     //@formatter:on
    @Valid
    private Collection<GoogleChatRoomDTO> rooms = new ArrayList<>();

    /**
     * Gets the requested locales.
     * @return the requested locales.
     */
    public Collection<Locale> requestedLocales() {
        Set<Locale> locales = new HashSet<>();
        rooms.forEach((recipient) -> {
            locales.add(recipient.getLocale());
        });
        return locales;
    }

    /**
     * Gets the rooms per locale.
     * @param locale the locale.
     * @return the rooms per locale.
     */
    public Collection<GoogleChatRoomDTO> rooms(Locale locale) {
        return rooms.stream().filter((room) -> {
            return room.getLocale().equals(locale);
        }).collect(Collectors.toList());
    }

}
