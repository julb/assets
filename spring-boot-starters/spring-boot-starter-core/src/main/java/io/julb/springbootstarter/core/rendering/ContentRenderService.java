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

package io.julb.springbootstarter.core.rendering;

import io.julb.library.utility.constants.HTMLTags;
import io.julb.library.utility.constants.MediaType;
import io.julb.library.utility.constants.Strings;
import io.julb.library.utility.interfaces.Contentable;
import io.julb.springbootstarter.core.localization.CustomLocaleContext;
import io.julb.springbootstarter.core.localization.LocaleService;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * The content render service.
 * <P>
 * @author Julb.
 */
public class ContentRenderService {

    /**
     * The locale service.
     */
    @Autowired
    private LocaleService localeService;

    /**
     * Renders the localized content to HTML.
     * @param localizedContent the localized content.
     * @return the HTML content.
     */
    public String renderToHtml(Map<String, ? extends Contentable> localizedContent) {
        Contentable contentable = getLocalizedValue(localizedContent);
        if (contentable == null || StringUtils.isBlank(contentable.getContent())) {
            return Strings.EMPTY;
        }

        if (MediaType.TEXT_PLAIN.equalsIgnoreCase(contentable.getMimeType())) {
            return StringUtils.join(HTMLTags.SPAN_LT, contentable.getContent(), HTMLTags.SPAN_RT);
        } else if (MediaType.TEXT_MARKDOWN.equalsIgnoreCase(contentable.getMimeType())) {
            Parser parser = Parser.builder().build();
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            return renderer.render(parser.parse(contentable.getContent()));
        } else {
            return contentable.getContent();
        }
    }

    /**
     * Renders the localized content to text.
     * @param localizedContent the localized content.
     * @return the content rendered as simple text.
     */
    public String renderToText(Map<String, ? extends Contentable> localizedContent) {
        Contentable contentable = getLocalizedValue(localizedContent);
        if (contentable == null || StringUtils.isBlank(contentable.getContent())) {
            return Strings.EMPTY;
        }

        if (MediaType.TEXT_HTML.equalsIgnoreCase(contentable.getMimeType())) {
            return Jsoup.parse(contentable.getContent()).text();
        } else if (MediaType.TEXT_MARKDOWN.equalsIgnoreCase(contentable.getMimeType())) {
            Parser parser = Parser.builder().build();
            TextContentRenderer renderer = TextContentRenderer.builder().build();
            return renderer.render(parser.parse(contentable.getContent()));
        } else {
            return contentable.getContent();
        }
    }

    /**
     * Gets the localized value matching the best the given locale.
     * @param <T> the value.
     * @param localizedMessage the localized message.
     * @param locale the locale.
     * @param defaultLocale the default locale.
     * @return the localized value.
     */
    private <T extends Contentable> T getLocalizedValue(Map<String, T> localizedMessage) {
        // The default locale.
        CustomLocaleContext locale = (CustomLocaleContext) LocaleContextHolder.getLocaleContext();

        // Find the appropriate locale.
        Locale resolvedLocale = localeService.resolveLocaleWithLanguageTags(locale.getRequestLanguageRange(), localizedMessage.keySet());

        // Returns the localized message.
        return localizedMessage.get(resolvedLocale.toLanguageTag());
    }

}
