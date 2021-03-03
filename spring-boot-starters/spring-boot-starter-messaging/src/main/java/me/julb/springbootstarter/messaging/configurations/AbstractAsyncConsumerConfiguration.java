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

package me.julb.springbootstarter.messaging.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.utility.constants.CustomMessagingHeaders;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The local async configuration.
 * <P>
 * @author Julb.
 */
public abstract class AbstractAsyncConsumerConfiguration {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Method to invoke when receiving a async message.
     * @param message the message.
     */
    protected void onReceiveStart(Message<? extends AsyncMessageDTO<?>> message) {
        // Trace input.
        LOGGER.debug("Receiving message <{}>.", message.getPayload().getId());

        // Set trademark.
        TrademarkContextHolder.setTrademark(message.getHeaders().get(CustomMessagingHeaders.X_JULB_TM, String.class));
    }

    /**
     * Method to invoke when processed a async message.
     * @param message the message.
     */
    protected void onReceiveEnd(Message<? extends AsyncMessageDTO<?>> message) {
        // Clear trademark.
        TrademarkContextHolder.unsetTrademark();

        // Trace finished.
        LOGGER.debug("Message <{}> processed successfully.", message.getPayload().getId());
    }
}
