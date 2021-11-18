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

package me.julb.applications.webnotification.configurations;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import me.julb.applications.webnotification.services.WebNotificationIngestionService;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.dto.webnotification.WebNotificationMessageDTO;
import me.julb.springbootstarter.messaging.configurations.AbstractAsyncConsumerConfiguration;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;

/**
 * The local async configuration.
 * <br>
 * @author Julb.
 */
@Configuration
public class CustomAsyncConfiguration extends AbstractAsyncConsumerConfiguration {

    /**
     * The web notification ingestion service.
     */
    @Autowired
    private WebNotificationIngestionService webNotificationIngestionService;

    /**
     * Consumer for the resource event.
     * @return a function to consume resource event.
     */
    @Bean
    public Consumer<Message<ResourceEventAsyncMessageDTO>> resourceEvent() {
        return resourceEventAsyncMessage -> {
            onReceiveStart(resourceEventAsyncMessage);

            // The payload.
            ResourceEventAsyncMessageDTO payload = resourceEventAsyncMessage.getPayload();

            // The resource concerns a user.
            if (ResourceTypes.USER.equals(payload.getResourceType())) {
                if (ResourceEventType.UPDATED.equals(payload.getEventType())) {

                } else if (ResourceEventType.DELETED.equals(payload.getEventType())) {

                }
            }

            onReceiveEnd(resourceEventAsyncMessage);
        };
    }

    /**
     * Consumer for dispatched web notifications.
     * @return a function to consume dispatched web notifications.
     */
    @Bean
    public Consumer<Message<AsyncMessageDTO<WebNotificationMessageDTO>>> dispatchedWebNotification() {
        return notificationAsyncMessage -> {
            onReceiveStart(notificationAsyncMessage);

            // Invoke consumer.
            webNotificationIngestionService.ingest(notificationAsyncMessage.getPayload().getBody());

            onReceiveEnd(notificationAsyncMessage);
        };
    }
}
