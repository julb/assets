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

package me.julb.applications.notificationdispatcher.services.impl;

import java.util.EnumMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.notificationdispatcher.services.NotificationDispatcherService;
import me.julb.library.dto.googlechat.GoogleChatMessageDTO;
import me.julb.library.dto.mail.MailDTO;
import me.julb.library.dto.notification.events.NotificationDispatchAsyncMessageDTO;
import me.julb.library.dto.notification.events.NotificationDispatchType;
import me.julb.library.dto.notification.events.parts.GoogleChatRoomDTO;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.dto.simple.content.LargeContentWithSubjectDTO;
import me.julb.library.dto.sms.SmsMessageDTO;
import me.julb.springbootstarter.messaging.builders.AsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;
import me.julb.springbootstarter.templating.notification.services.NotificationTemplatingService;
import me.julb.springbootstarter.templating.notification.services.dto.GenerateNotificationContentDTO;

/**
 * A service that process notifications to dispatch.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class NotificationDispatcherServiceImpl implements NotificationDispatcherService {

    /**
     * The templating service.
     */
    @Autowired
    private NotificationTemplatingService notificationTemplatingService;

    /**
     * The async message poster service.
     */
    @Autowired
    private AsyncMessagePosterService asyncMessagePosterService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(@NotNull @Valid NotificationDispatchAsyncMessageDTO notification) {
        EnumMap<NotificationDispatchType, AtomicLong> metrics = new EnumMap<>(NotificationDispatchType.class);

        // Generate notification content.
        GenerateNotificationContentDTO generateNotificationContent = new GenerateNotificationContentDTO();
        generateNotificationContent.setName(notification.getKind());
        generateNotificationContent.setParameters(notification.getParameters());
        generateNotificationContent.setTm(notification.getTm());

        // Mail notification
        if (notification.getMail() != null) {
            for (Locale locale : notification.getMail().requestedLocales()) {
                // Configure the generation DTO.
                generateNotificationContent.setType(NotificationDispatchType.MAIL);
                generateNotificationContent.setLocale(locale);

                // Render content.
                LargeContentWithSubjectDTO largeContentWithSubject = (LargeContentWithSubjectDTO) notificationTemplatingService.render(generateNotificationContent);

                // Build a GChat message DTO and post it.
                MailDTO mailMessage = new MailDTO();
                mailMessage.getAttachments().addAll(notification.getMail().getAttachments());
                mailMessage.getInlineAttachments().addAll(notification.getMail().getInlineAttachments());
                mailMessage.setHtml(largeContentWithSubject.getContent());
                mailMessage.setSubject(largeContentWithSubject.getSubject());
                mailMessage.getTos().addAll(notification.getMail().toEmails(locale));
                mailMessage.getCcs().addAll(notification.getMail().ccEmails(locale));
                mailMessage.getBccs().addAll(notification.getMail().bccEmails(locale));

                // Dispatch message.
                dispatchNotification(generateNotificationContent.getTm(), generateNotificationContent.getType(), generateNotificationContent.getLocale(), mailMessage);

                // Increment metrics.
                metrics.get(generateNotificationContent.getType()).incrementAndGet();
            }
        }

        // SMS
        if (notification.getSms() != null) {
            for (Locale locale : notification.getSms().requestedLocales()) {
                // Configure the generation DTO.
                generateNotificationContent.setType(NotificationDispatchType.SMS);
                generateNotificationContent.setLocale(locale);

                // Render content.
                LargeContentDTO largeContent = notificationTemplatingService.render(generateNotificationContent);

                for (String e164Number : notification.getSms().toE164Numbers(locale)) {
                    // Build a SMS message DTO and post it.
                    SmsMessageDTO smsMessage = new SmsMessageDTO();
                    smsMessage.setE164Number(e164Number);
                    smsMessage.setText(largeContent.getContent());

                    // Dispatch message.
                    dispatchNotification(generateNotificationContent.getTm(), generateNotificationContent.getType(), generateNotificationContent.getLocale(), smsMessage);

                    // Increment metrics.
                    metrics.get(generateNotificationContent.getType()).incrementAndGet();
                }
            }
        }

        // Web notif.

        // Google Chat Room
        if (notification.getGoogleChat() != null) {
            for (Locale locale : notification.getGoogleChat().requestedLocales()) {

                // Configure the generation DTO.
                generateNotificationContent.setType(NotificationDispatchType.GOOGLE_CHAT);
                generateNotificationContent.setLocale(locale);

                // Render content.
                LargeContentDTO largeContent = notificationTemplatingService.render(generateNotificationContent);

                for (GoogleChatRoomDTO room : notification.getGoogleChat().rooms(locale)) {
                    // Build a GChat message DTO and post it.
                    GoogleChatMessageDTO googleChatMessage = new GoogleChatMessageDTO();
                    googleChatMessage.setRoom(room.getRoom());
                    googleChatMessage.setText(largeContent.getContent());
                    googleChatMessage.setThreadKey(room.getThreadKey());

                    // Dispatch message.
                    dispatchNotification(generateNotificationContent.getTm(), generateNotificationContent.getType(), generateNotificationContent.getLocale(), googleChatMessage);

                    // Increment metrics.
                    metrics.get(generateNotificationContent.getType()).incrementAndGet();
                }
            }
        }

        LOGGER.info("Notification dispatch request processed successfully : {}.", metrics.toString());
    }

    /**
     * Dispatch a notification.
     * @param <T> the body type.
     * @param trademark the current trademark.
     * @param locale the locale.
     * @param notificationDispatchType the notification dispatch type.
     * @param body the body.
     */
    protected <T> void dispatchNotification(String trademark, NotificationDispatchType notificationDispatchType, Locale locale, T body) {
        //@formatter:off
        asyncMessagePosterService.postMessage(null, new AsyncMessageBuilder<>()
            .attribute("tm", trademark)
            .attribute("notificationDispatchType", notificationDispatchType.toString())
            .attribute("locale", locale.toLanguageTag())
            .body(body)
            .build()
        );
        //@formatter:on
    }
}
