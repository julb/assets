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

package me.julb.applications.webnotification.configurations;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.julb.applications.webnotification.services.WebNotificationIngestionService;
import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.dto.webnotification.WebNotificationMessageDTO;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The local async configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@Slf4j
public class CustomAsyncConfiguration {

    /**
     * The web notification ingestion service.
     */
    @Autowired
    private WebNotificationIngestionService webNotificationIngestionService;

    /**
     * Consumer for dispatched web notifications.
     * @return a function to consume dispatched web notifications.
     */
    @Bean
    public Consumer<AsyncMessageDTO<WebNotificationMessageDTO>> dispatchedWebNotification() {
        return notificationAsyncMessage -> {
            // Trace input.
            LOGGER.debug("Receiving message <{}>.", notificationAsyncMessage.getId());

            // Set trademark.
            TrademarkContextHolder.setTrademark(notificationAsyncMessage.getAttributes().get("tm"));

            // Invoke consumer.
            webNotificationIngestionService.ingest(notificationAsyncMessage.getBody());

            // Unset trademark.
            TrademarkContextHolder.unsetTrademark();

            // Trace finished.
            LOGGER.debug("Message <{}> processed successfully.", notificationAsyncMessage.getId());
        };
    }
}
