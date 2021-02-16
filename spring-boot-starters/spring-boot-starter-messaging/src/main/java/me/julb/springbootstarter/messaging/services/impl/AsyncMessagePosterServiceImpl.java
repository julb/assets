/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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

package me.julb.springbootstarter.messaging.services.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.library.dto.messaging.events.AuditAsyncMessageDTO;
import me.julb.library.dto.messaging.events.EventCollectorAsyncMessageDTO;
import me.julb.library.dto.messaging.events.EventCollectorAsyncMessageLevel;
import me.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.WebAnalyticsAsyncMessageDTO;
import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.dto.notification.events.NotificationDispatchAsyncMessageDTO;
import me.julb.springbootstarter.messaging.builders.AuditAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;

/**
 * The message poster service.
 * <P>
 * @author Julb.
 */
@Service
@Slf4j
@Validated
public class AsyncMessagePosterServiceImpl implements AsyncMessagePosterService {

    /**
     * Thee routing key header name.
     */
    private static final String ROUTING_KEY_DEFAULT_HEADER_NAME = "routingKey";

    /**
     * The main producer default channel name.
     */
    private static final String MAIN_PRODUCER_DEFAULT_CHANNEL_NAME = "main";

    /**
     * The main producer channel name.
     */
    private String mainProducerChannelName = MAIN_PRODUCER_DEFAULT_CHANNEL_NAME;

    /**
     * The routing key heeader name.
     */
    private String routingKeyHeaderName = ROUTING_KEY_DEFAULT_HEADER_NAME;

    /**
     * The bridge to post messages.
     */
    @Autowired
    private StreamBridge streamBridge;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postMessage(@NotNull @NotBlank String routingKeyValue, @NotNull @Valid AsyncMessageDTO<T> asyncMessage) {
        // Logs a message.
        LOGGER.debug("Posting message with id <{}> using the routing key <{}>.", asyncMessage.getId(), routingKeyValue);

        //@formatter:off
        MessageBuilder<AsyncMessageDTO<T>> amqpMessageBuilder = MessageBuilder.withPayload(asyncMessage);
        if(MapUtils.isNotEmpty(asyncMessage.getAttributes())) {
            for(Map.Entry<String, String> mapEntry : asyncMessage.getAttributes().entrySet()) {
                amqpMessageBuilder.setHeader(mapEntry.getKey(), mapEntry.getValue());
            }
        }
        amqpMessageBuilder.setHeader(routingKeyHeaderName, routingKeyValue.toLowerCase());
        Message<AsyncMessageDTO<T>> amqpMessage = amqpMessageBuilder.build();
        //@formatter:on
        streamBridge.send(String.format("%s-out-0", this.mainProducerChannelName), amqpMessage);

        // Logs successful
        LOGGER.info("Message with id <{}> posted successfully.", asyncMessage.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postResourceEventMessage(@NotNull @Valid ResourceEventAsyncMessageDTO messagingPost) {
        String routingKey = String.format("resource.%s.%s", messagingPost.getResourceType(), messagingPost.getEventType().toString()).toLowerCase();
        this.postMessage(routingKey, messagingPost);

        //@formatter:off
        AuditAsyncMessageDTO<Void> auditMessage = new AuditAsyncMessageBuilder<Void>()
            .level(EventCollectorAsyncMessageLevel.INFO)
            .objectType(messagingPost.getResourceType())
            .objectReference(messagingPost.getResourceId())
            .objectName(messagingPost.getResourceName())
            .product("Resource")
            .action(messagingPost.getEventType().toString())
            .user(messagingPost.getUser())
            .build();
        //@formatter:on

        this.postAuditMessage(auditMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postEventCollectorMessage(@NotNull @NotBlank String type, @NotNull @Valid EventCollectorAsyncMessageDTO<T> messagingPost) {
        String routingKey = String.format("%s.%s.created", type, messagingPost.getLevel().toString()).toLowerCase();
        this.postMessage(routingKey, messagingPost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postJobExecutionResultMessage(@NotNull @Valid JobResultAsyncMessageDTO<T> messagingPost) {
        String routingKey = String.format("job-execution-result.%s.created", messagingPost.getLevel().toString()).toLowerCase();
        this.postMessage(routingKey, messagingPost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postWebAnalyticsMessage(@NotNull @Valid WebAnalyticsAsyncMessageDTO<T> messagingPost) {
        String routingKey = String.format("analytics.%s.created", messagingPost.getLevel().toString()).toLowerCase();
        this.postMessage(routingKey, messagingPost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postAuditMessage(@NotNull @Valid AuditAsyncMessageDTO<T> messagingPost) {
        String routingKey = String.format("audit.%s.created", messagingPost.getLevel().toString()).toLowerCase();
        this.postMessage(routingKey, messagingPost);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void postNotificationMessage(@NotNull @Valid NotificationDispatchAsyncMessageDTO messagingPost) {
        this.postMessage("notification.info.posted", messagingPost);
    }

}
