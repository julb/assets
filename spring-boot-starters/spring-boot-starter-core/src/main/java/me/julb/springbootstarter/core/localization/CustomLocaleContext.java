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

package me.julb.springbootstarter.core.localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;

/**
 * A custom locale context.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class CustomLocaleContext extends SimpleTimeZoneAwareLocaleContext {

    //@formatter:off
     /**
     * The requestLanguageRange attribute.
     * -- GETTER --
     * Getter for {@link #requestLanguageRange} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #requestLanguageRange} property.
     * @param requestLanguageRange the value to set.
     */
     //@formatter:on
    private String requestLanguageRange;

    //@formatter:off
     /**
     * The availableLocales attribute.
     * -- GETTER --
     * Getter for {@link #availableLocales} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #availableLocales} property.
     * @param availableLocales the value to set.
     */
     //@formatter:on
    private List<Locale> availableLocales = new ArrayList<Locale>();

    //@formatter:off
     /**
     * The defaultLocale attribute.
     * -- GETTER --
     * Getter for {@link #defaultLocale} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #defaultLocale} property.
     * @param defaultLocale the value to set.
     */
     //@formatter:on
    private Locale defaultLocale;

    /**
     * Default constructor.
     * @param locale the locale.
     * @param timeZone the timezone.
     */
    public CustomLocaleContext(Locale locale, TimeZone timeZone) {
        super(locale, timeZone);
    }

}
