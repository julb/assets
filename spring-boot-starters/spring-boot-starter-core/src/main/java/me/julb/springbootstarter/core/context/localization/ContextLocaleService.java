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

package me.julb.springbootstarter.core.context.localization;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.core.localization.LocaleService;

/**
 * The locale service.
 * <br>
 * @author Julb.
 */
public class ContextLocaleService {
    /**
     * The config sources ervice.
     */
    @Autowired
    private LocaleService localeService;

    /**
     * Gets the default locale.
     * @return the default locale.
     */
    public Locale getDefaultLocale() {
        String tm = TrademarkContextHolder.getTrademark();
        return localeService.getDefaultLocale(tm);
    }

    /**
     * Gets the supported locales.
     * @return th supported locales.
     */
    public List<Locale> getSupportedLocales() {
        String tm = TrademarkContextHolder.getTrademark();
        return localeService.getSupportedLocales(tm);
    }

    /**
     * Gets the default timezone.
     * @return the default timezone.
     */
    public TimeZone getDefaultTimeZone() {
        String tm = TrademarkContextHolder.getTrademark();
        return localeService.getDefaultTimeZone(tm);
    }

    /**
     * Resolves the best locale for the given language range.
     * @param requestLanguageRange the request language range.
     * @return the best locale.
     */
    public Locale resolveLocale(String requestLanguageRange) {
        String tm = TrademarkContextHolder.getTrademark();
        return localeService.resolveLocale(tm, requestLanguageRange);
    }

    /**
     * Resolves the best locale for the given language range.
     * @param requestLanguageRange the request language range.
     * @param supportedLocaleLanguageTags the supported locale languages tags..
     * @return the best locale.
     */
    public Locale resolveLocaleWithLanguageTags(String requestLanguageRange, Collection<String> supportedLocaleLanguageTags) {
        String tm = TrademarkContextHolder.getTrademark();
        return localeService.resolveLocaleWithLanguageTags(tm, requestLanguageRange, supportedLocaleLanguageTags);
    }

    /**
     * Resolves the best locale for the given language range.
     * @param requestLanguageRange the request language range.
     * @param supportedLocales the supported locales.
     * @return the best locale.
     */
    public Locale resolveLocale(String requestLanguageRange, Collection<Locale> supportedLocales) {
        String tm = TrademarkContextHolder.getTrademark();
        return localeService.resolveLocale(tm, requestLanguageRange, supportedLocales);
    }
}
