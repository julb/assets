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

package me.julb.springbootstarter.web.reactive.resolvers.locale;

import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import me.julb.library.utility.constants.LocalizationRequestAttributes;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;
import me.julb.springbootstarter.core.localization.LocaleService;

/**
 * The locale resolver based on a HTTP header.
 * <br>
 * @author Julb.
 */
public class CustomLocaleContextResolver implements LocaleContextResolver {

    /**
     * The locale service.
     */
    @Autowired
    private LocaleService localeService;

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        // Locale.
        String languageParameter = request.getQueryParams().getFirst(LocalizationRequestAttributes.LANGUAGE_PARAMETER_NAME);
        if (StringUtils.isBlank(languageParameter)) {
            languageParameter = request.getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
        }
        Locale locale = localeService.resolveLocale(languageParameter);

        // Timezone.
        String timezoneId = request.getQueryParams().getFirst(LocalizationRequestAttributes.TIMEZONE_PARAMETER_NAME);
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

    @Override
    public void setLocaleContext(ServerWebExchange arg0, LocaleContext arg1) {
        throw new UnsupportedOperationException("Cannot change HTTP accept header - use a different locale resolution strategy");
    }
}
