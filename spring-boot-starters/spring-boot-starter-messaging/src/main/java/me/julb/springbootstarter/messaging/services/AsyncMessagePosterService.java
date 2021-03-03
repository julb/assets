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

package me.julb.springbootstarter.messaging.services;

import java.util.Locale;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import me.julb.library.dto.messaging.events.AuditAsyncMessageDTO;
import me.julb.library.dto.messaging.events.EventCollectorAsyncMessageDTO;
import me.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.WebAnalyticsAsyncMessageDTO;
import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.dto.notification.events.NotificationDispatchAsyncMessageDTO;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The main service to post message.
 * <P>
 * @author Julb.
 */
public interface AsyncMessagePosterService {

    /**
     * Posts a message to the broker.
     * @param routingKey the routing key to use.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postMessage(@NotNull @NotBlank String routingKey, @NotNull @Valid AsyncMessageDTO<T> messagingPost);

    /**
     * Posts a message to the broker.
     * @param trademark the trademark.
     * @param locale the locale.
     * @param routingKey the routing key to use.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postMessage(@NotNull @NotBlank @Trademark String trademark, @NotNull Locale locale, @NotNull @NotBlank String routingKey, @NotNull @Valid AsyncMessageDTO<T> messagingPost);

    /**
     * Posts a event collector message to the broker.
     * @param trademark the trademark.
     * @param type the type of the event.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postEventCollectorMessage(@NotNull @NotBlank String type, @NotNull @Valid EventCollectorAsyncMessageDTO<T> messagingPost);

    /**
     * Posts a resource message to the broker.
     * @param trademark the trademark.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postResourceEventMessage(@NotNull @Valid ResourceEventAsyncMessageDTO messagingPost);

    /**
     * Posts a job execution result message to the broker.
     * @param trademark the trademark.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postJobExecutionResultMessage(@NotNull @Valid JobResultAsyncMessageDTO<T> messagingPost);

    /**
     * Posts a web analytics message to the broker.
     * @param trademark the trademark.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postWebAnalyticsMessage(@NotNull @Valid WebAnalyticsAsyncMessageDTO<T> messagingPost);

    /**
     * Posts a audit message to the broker.
     * @param trademark the trademark.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postAuditMessage(@NotNull @Valid AuditAsyncMessageDTO<T> messagingPost);

    /**
     * Posts a notification message to the broker.
     * @param trademark the trademark.
     * @param messagingPost the message to post.
     * @param <T> the type of bean associated to the message.
     */
    <T> void postNotificationMessage(@NotNull @Valid NotificationDispatchAsyncMessageDTO messagingPost);

}
