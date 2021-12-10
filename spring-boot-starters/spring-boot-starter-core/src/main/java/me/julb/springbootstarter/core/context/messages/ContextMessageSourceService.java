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

package me.julb.springbootstarter.core.context.messages;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;

import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.core.messages.MessageSourceService;

/**
 * The context message source service.
 * <br>
 * This class uses ThreadLocal to extract context and delegates to {@link MessageSourceService}.
 * @author Julb.
 */
public class ContextMessageSourceService {

    /**
     * The message source service.
     */
    @Autowired
    private MessageSourceService messageSourceService;

    /**
     * Try to resolve the message.
     * @param code the message code to lookup.
     * @param args the arguments.
     * @param defaultMessage the default message if not found.
     * @param locale the locale.
     * @return the messsage if found.
     */
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        String tm = TrademarkContextHolder.getTrademark();
        return messageSourceService.getMessage(tm, code, args, defaultMessage, locale);
    }

    /**
     * Try to resolve the message.
     * @param code the message code to lookup.
     * @param args the arguments.
     * @param locale the locale.
     * @return the messsage if found.
     * @throws NoSuchMessageException if the message is not found.
     */
    public String getMessage(String code, Object[] args, Locale locale)
        throws NoSuchMessageException {
        String tm = TrademarkContextHolder.getTrademark();
        return messageSourceService.getMessage(tm, code, args, locale);
    }
}
