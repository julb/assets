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

package me.julb.springbootstarter.web.mvc.resolvers.locale;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;

import me.julb.library.utility.constants.LocalizationRequestAttributes;
import me.julb.springbootstarter.core.context.localization.ContextLocaleService;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;

/**
 * The locale resolver based on a HTTP header.
 * <br>
 * @author Julb.
 */
public class CustomLocaleContextResolver extends AbstractLocaleContextResolver {

    /**
     * The locale service.
     */
    @Autowired
    private ContextLocaleService localeService;

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        // Locale.
        String languageParameter = request.getParameter(LocalizationRequestAttributes.LANGUAGE_PARAMETER_NAME);
        if (StringUtils.isBlank(languageParameter)) {
            languageParameter = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        }
        Locale locale = localeService.resolveLocale(languageParameter);

        // Timezone.
        String timezoneId = request.getParameter(LocalizationRequestAttributes.TIMEZONE_PARAMETER_NAME);
        TimeZone timeZone;
        if (StringUtils.isNotBlank(timezoneId) && ArrayUtils.contains(TimeZone.getAvailableIDs(), timezoneId)) {
            timeZone = TimeZone.getTimeZone(timezoneId);
        } else {
            timeZone = localeService.getDefaultTimeZone();
        }

        // Return custom locale context.
        CustomLocaleContext customLocaleContext = new CustomLocaleContext(locale, timeZone);
        customLocaleContext.setRequestLanguageRange(languageParameter);
        customLocaleContext.setAvailableLocales(localeService.getSupportedLocales());
        customLocaleContext.setDefaultLocale(localeService.getDefaultLocale());
        return customLocaleContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale resolution strategy");
    }
}
