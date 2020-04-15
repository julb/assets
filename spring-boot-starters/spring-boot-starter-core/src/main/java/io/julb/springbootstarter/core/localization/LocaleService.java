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

package io.julb.springbootstarter.core.localization;

import io.julb.springbootstarter.core.configs.ConfigSourceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * The locale service.
 * <P>
 * @author Julb.
 */
public class LocaleService {

    /**
     * The config sources ervice.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    /**
     * Gets the default locale.
     * @return the default locale.
     */
    public Locale getDefaultLocale() {
        return Objects.requireNonNullElse(configSourceService.getTypedProperty("locales.default", Locale.class), Locale.getDefault());
    }

    /**
     * Gets the supported locales.
     * @return th supported locales.
     */
    public List<Locale> getSupportedLocales() {
        return Arrays.asList(configSourceService.getTypedProperty("locales", Locale[].class));
    }

    /**
     * Gets the default timezone.
     * @return the default timezone.
     */
    public TimeZone getDefaultTimeZone() {
        return Objects.requireNonNullElse(configSourceService.getTypedProperty("timezones.default", TimeZone.class), TimeZone.getDefault());
    }

    /**
     * Resolves the best locale for the given language range.
     * @param requestLanguageRange the request language range.
     * @return the best locale.
     */
    public Locale resolveLocale(String requestLanguageRange) {
        return resolveLocale(requestLanguageRange, getSupportedLocales());
    }

    /**
     * Resolves the best locale for the given language range.
     * @param requestLanguageRange the request language range.
     * @param supportedLocaleLanguageTags the supported locale languages tags..
     * @return the best locale.
     */
    public Locale resolveLocaleWithLanguageTags(String requestLanguageRange, Collection<String> supportedLocaleLanguageTags) {
        // The locales available in the messages.
        Collection<Locale> locales = new ArrayList<>();
        for (String language : supportedLocaleLanguageTags) {
            locales.add(Locale.forLanguageTag(language));
        }

        return resolveLocale(requestLanguageRange, locales);
    }

    /**
     * Resolves the best locale for the given language range.
     * @param requestLanguageRange the request language range.
     * @param supportedLocales the supported locales.
     * @return the best locale.
     */
    public Locale resolveLocale(String requestLanguageRange, Collection<Locale> supportedLocales) {
        // No language range, return default.
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && requestLanguageRange == null) {
            return defaultLocale;
        }

        // The language ranges.
        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(requestLanguageRange);

        // Search the best fit
        Locale bestFitLocale = Locale.lookup(languageRanges, supportedLocales);
        if (bestFitLocale != null) {
            return bestFitLocale;
        }

        // Return the default one if exists.
        if (supportedLocales.contains(defaultLocale)) {
            return defaultLocale;
        }

        // No locale found.
        return null;
    }
}
