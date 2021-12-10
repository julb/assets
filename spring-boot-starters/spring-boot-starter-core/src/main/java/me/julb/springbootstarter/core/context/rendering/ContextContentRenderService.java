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

package me.julb.springbootstarter.core.context.rendering;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import me.julb.library.utility.interfaces.Contentable;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;
import me.julb.springbootstarter.core.rendering.ContentRenderService;

/**
 * The context content render service.
 * <br>
 * This class uses ThreadLocal to extract context and delegates to {@link ContentRenderService}.
 * @author Julb.
 */
public class ContextContentRenderService {

    /**
     * The content render service.
     */
    @Autowired
    private ContentRenderService contentRenderService;

    /**
     * Renders the localized content to HTML.
     * @param localizedContent the localized content.
     * @return the HTML content.
     */
    public String renderToHtml(Map<String, ? extends Contentable> localizedContent) {
        String tm = TrademarkContextHolder.getTrademark();
        CustomLocaleContext locale = (CustomLocaleContext) LocaleContextHolder.getLocaleContext();
        return contentRenderService.renderToHtml(tm, locale, localizedContent);
    }

    /**
     * Renders the localized content to text.
     * @param localizedContent the localized content.
     * @return the content rendered as simple text.
     */
    public String renderToText(Map<String, ? extends Contentable> localizedContent) {
        String tm = TrademarkContextHolder.getTrademark();
        CustomLocaleContext locale = (CustomLocaleContext) LocaleContextHolder.getLocaleContext();
        return contentRenderService.renderToText(tm, locale, localizedContent);
    }
}
