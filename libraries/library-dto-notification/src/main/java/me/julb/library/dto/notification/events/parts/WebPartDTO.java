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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

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
    private Collection<WebRecipientDTO> tos = new HashSet<>();

    /**
     * Gets the requested locales.
     * @return the requested locales.
     */
    public Collection<Locale> requestedLocales() {
        Set<Locale> locales = new HashSet<>();
        tos.forEach((recipient) -> {
            locales.add(recipient.getLocale());
        });
        return locales;
    }

    /**
     * Gets the recipients per locale.
     * @param locale the locale.
     * @return the recipients per locale.
     */
    public Collection<WebRecipientDTO> tos(Locale locale) {
        return tos.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).collect(Collectors.toList());
    }

    /**
     * Gets the user IDs per locale.
     * @param locale the locale.
     * @return the user IDs numbers per locale.
     */
    public Collection<String> toIds(Locale locale) {
        return tos.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).map(WebRecipientDTO::getId).collect(Collectors.toList());
    }

}
