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

package me.julb.springbootstarter.messaging.builders;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.julb.library.dto.mail.MailAttachmentDTO;
import me.julb.library.dto.mail.MailInlineAttachmentDTO;
import me.julb.library.dto.notification.events.NotificationDispatchAsyncMessageDTO;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.dto.notification.events.WebNotificationPriority;
import me.julb.library.dto.notification.events.parts.GoogleChatPartDTO;
import me.julb.library.dto.notification.events.parts.GoogleChatRoomDTO;
import me.julb.library.dto.notification.events.parts.MailPartDTO;
import me.julb.library.dto.notification.events.parts.MailRecipientDTO;
import me.julb.library.dto.notification.events.parts.SmsPartDTO;
import me.julb.library.dto.notification.events.parts.SmsRecipientDTO;
import me.julb.library.dto.notification.events.parts.WebPartDTO;
import me.julb.library.dto.simple.mobilephone.MobilePhoneDTO;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;

/**
 * A builder for {@link NotificationDispatchAsyncMessageDTO} instances.
 * <P>
 * @author Julb.
 */
public class NotificationDispatchAsyncMessageBuilder {

    /**
     * The message.
     */
    private NotificationDispatchAsyncMessageDTO message;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     */
    public NotificationDispatchAsyncMessageBuilder() {
        this.message = new NotificationDispatchAsyncMessageDTO();
        this.message.setTimestamp(DateUtility.dateTimeNow());
        this.message.setId(IdentifierUtility.generateId());
        this.message.setVersion(Integers.ONE);
        this.message.setAttributes(new HashMap<>());
    }

    // ------------------------------------------ Class methods.

    /**
     * Setter for property identifier.
     * @param identifier New value of property identifier.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder identifier(String identifier) {
        this.message.setId(identifier);
        return this;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder version(Integer version) {
        this.message.setVersion(version);
        return this;
    }

    /**
     * Add an attribute to the message.
     * @param name the attribute name to set.
     * @param value the attribute value to set.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder attribute(String name, String value) {
        this.message.getAttributes().put(name, value);
        return this;
    }

    /**
     * Setter for property tm.
     * @param tm New value of property tm.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder tm(String tm) {
        this.message.setTm(tm);
        return this;
    }

    /**
     * Alias for {@link #tm(String)}.
     * <P>
     * Setter for property trademark.
     * @param trademark New value of property trademark.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder trademark(String trademark) {
        this.message.setTm(trademark);
        return this;
    }

    /**
     * Setter for property kind.
     * @param kind New value of property kind.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder kind(NotificationKind kind) {
        this.message.setKind(kind);
        return this;
    }

    /**
     * Add an parameter to the message.
     * @param name the parameter name to set.
     * @param value the parameter value to set.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder parameter(String name, Object value) {
        this.message.getParameters().put(name, value);
        return this;
    }

    /**
     * Setter for property parameters.
     * @param parameters New value of property parameters.
     * @return the current builder instance.
     */
    public NotificationDispatchAsyncMessageBuilder parameters(Map<String, ?> parameters) {
        this.message.getParameters().putAll(parameters);
        return this;
    }

    /**
     * Setter for property mail.
     * @return the current builder instance.
     */
    public MailPartBuilder mail() {
        return new MailPartBuilder(this);
    }

    /**
     * Setter for property sms.
     * @return the current builder instance.
     */
    public SmsPartBuilder sms() {
        return new SmsPartBuilder(this);
    }

    /**
     * Setter for property web.
     * @return the current builder instance.
     */
    public WebPartBuilder web() {
        return new WebPartBuilder(this);
    }

    /**
     * Setter for property sms.
     * @return the current builder instance.
     */
    public GoogleChatPartBuilder googleChatRoom() {
        return new GoogleChatPartBuilder(this);
    }

    /**
     * Builds the message instance.
     * @return the message instance.
     */
    public NotificationDispatchAsyncMessageDTO build() {
        return this.message;
    }

    // ------------------------------------------ Inner classes methods.

    /**
     * The mail part builder.
     * <P>
     * @author Julb.
     */
    public class MailPartBuilder {

        /**
         * The part.
         */
        private MailPartDTO part;

        /**
         * The parent builder.
         */
        private NotificationDispatchAsyncMessageBuilder parent;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         * @param parent the parent.
         */
        public MailPartBuilder(NotificationDispatchAsyncMessageBuilder parent) {
            this.part = new MailPartDTO();
            this.parent = parent;
        }

        // ------------------------------------------ Class methods.

        /**
         * Setter for property to.
         * @param to New value of property to.
         * @param locale the target locale.
         * @return the current builder instance.
         */
        public MailPartBuilder to(String to, Locale locale) {
            this.part.getTos().add(new MailRecipientDTO(to, locale));
            return this;
        }

        /**
         * Setter for property to.
         * @param to New value of property to.
         * @return the current builder instance.
         */
        public MailPartBuilder to(UserRefDTO... to) {
            for (UserRefDTO user : to) {
                this.part.getTos().add(new MailRecipientDTO(user.getMail(), user.getLocale()));
            }
            return this;
        }

        /**
         * Setter for property cc.
         * @param cc New value of property cc.
         * @param locale the target locale.
         * @return the current builder instance.
         */
        public MailPartBuilder cc(String cc, Locale locale) {
            this.part.getCcs().add(new MailRecipientDTO(cc, locale));
            return this;
        }

        /**
         * Setter for property cc.
         * @param cc New value of property cc.
         * @return the current builder instance.
         */
        public MailPartBuilder cc(UserRefDTO... cc) {
            for (UserRefDTO user : cc) {
                this.part.getCcs().add(new MailRecipientDTO(user.getMail(), user.getLocale()));
            }
            return this;
        }

        /**
         * Setter for property bcc.
         * @param bcc New value of property bcc.
         * @param locale the target locale.
         * @return the current builder instance.
         */
        public MailPartBuilder bcc(String bcc, Locale locale) {
            this.part.getBccs().add(new MailRecipientDTO(bcc, locale));
            return this;
        }

        /**
         * Setter for property bcc.
         * @param bcc New value of property bcc.
         * @return the current builder instance.
         */
        public MailPartBuilder bcc(UserRefDTO... bcc) {
            for (UserRefDTO user : bcc) {
                this.part.getBccs().add(new MailRecipientDTO(user.getMail(), user.getLocale()));
            }
            return this;
        }

        /**
         * Setter for property inlineAttachment.
         * @param inlineAttachment New value of property inlineAttachment.
         * @return the current builder instance.
         */
        public MailPartBuilder inlineAttachment(MailInlineAttachmentDTO inlineAttachment) {
            this.part.getInlineAttachments().add(inlineAttachment);
            return this;
        }

        /**
         * Setter for property attachment.
         * @param attachment New value of property attachment.
         * @return the current builder instance.
         */
        public MailPartBuilder attachment(MailAttachmentDTO attachment) {
            this.part.getAttachments().add(attachment);
            return this;
        }

        /**
         * Gets the parent builder.
         * @return the parent builder.
         */
        public NotificationDispatchAsyncMessageBuilder and() {
            this.parent.message.setMail(this.part);
            return this.parent;
        }
    }

    /**
     * The SMS part builder.
     * <P>
     * @author Julb.
     */
    public class SmsPartBuilder {

        /**
         * The part.
         */
        private SmsPartDTO part;

        /**
         * The parent builder.
         */
        private NotificationDispatchAsyncMessageBuilder parent;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         * @param parent the parent.
         */
        public SmsPartBuilder(NotificationDispatchAsyncMessageBuilder parent) {
            this.part = new SmsPartDTO();
            this.parent = parent;
        }

        // ------------------------------------------ Class methods.

        /**
         * Setter for property to.
         * @param to New value of property to.
         * @param locale the target locale.
         * @return the current builder instance.
         */
        public SmsPartBuilder to(String to, Locale locale) {
            this.part.getTos().add(new SmsRecipientDTO(to, locale));
            return this;
        }

        /**
         * Setter for property to.
         * @param to New value of property to.
         * @param locale the target locale.
         * @return the current builder instance.
         */
        public SmsPartBuilder to(MobilePhoneDTO to, Locale locale) {
            return this.to(to.getE164Number(), locale);
        }

        /**
         * Setter for property to.
         * @param to New value of property to.
         * @return the current builder instance.
         */
        public SmsPartBuilder to(UserRefDTO... to) {
            for (UserRefDTO user : to) {
                this.part.getTos().add(new SmsRecipientDTO(user.getE164Number(), user.getLocale()));
            }
            return this;
        }

        /**
         * Gets the parent builder.
         * @return the parent builder.
         */
        public NotificationDispatchAsyncMessageBuilder and() {
            this.parent.message.setSms(this.part);
            return this.parent;
        }
    }

    /**
     * The Web notification part builder.
     * <P>
     * @author Julb.
     */
    public class WebPartBuilder {

        /**
         * The part.
         */
        private WebPartDTO part;

        /**
         * The parent builder.
         */
        private NotificationDispatchAsyncMessageBuilder parent;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         * @param parent the parent.
         */
        public WebPartBuilder(NotificationDispatchAsyncMessageBuilder parent) {
            this.part = new WebPartDTO();
            this.parent = parent;
        }

        // ------------------------------------------ Class methods.

        /**
         * Setter for property to.
         * @param to New value of property to.
         * @return the current builder instance.
         */
        public WebPartBuilder to(UserRefDTO... to) {
            this.part.getTos().addAll(Arrays.asList(to));
            return this;
        }

        /**
         * Setter for property expiresInDays.
         * @param expiresInDays New value of property expiresInDays.
         * @return the current builder instance.
         */
        public WebPartBuilder expiresInDays(Integer expiresInDays) {
            this.part.setExpiryDateTime(DateUtility.dateTimePlus(expiresInDays, ChronoUnit.DAYS));
            return this;
        }

        /**
         * Setter for property priority.
         * @param priority New value of property priority.
         * @return the current builder instance.
         */
        public WebPartBuilder priority(WebNotificationPriority priority) {
            this.part.setPriority(priority);
            return this;
        }

        /**
         * Gets the parent builder.
         * @return the parent builder.
         */
        public NotificationDispatchAsyncMessageBuilder and() {
            this.parent.message.setWeb(this.part);
            return this.parent;
        }
    }

    /**
     * The Google Chart notification part builder.
     * <P>
     * @author Julb.
     */
    public class GoogleChatPartBuilder {

        /**
         * The part.
         */
        private GoogleChatPartDTO part;

        /**
         * The parent builder.
         */
        private NotificationDispatchAsyncMessageBuilder parent;

        // ------------------------------------------ Constructors.

        /**
         * Constructor.
         * @param parent the parent.
         */
        public GoogleChatPartBuilder(NotificationDispatchAsyncMessageBuilder parent) {
            this.part = new GoogleChatPartDTO();
            this.parent = parent;
        }

        // ------------------------------------------ Class methods.

        /**
         * Adds a room to the google chat notification.
         * @param room New value of property room.
         * @param locale New value of property locale.
         * @return the current builder instance.
         */
        public GoogleChatPartBuilder room(String room, Locale locale) {
            return room(room, locale, null);
        }

        /**
         * Adds a room to the google chat notification.
         * @param room New value of property room.
         * @param locale New value of property locale.
         * @param threadKey New value of property threadKey.
         * @return the current builder instance.
         */
        public GoogleChatPartBuilder room(String room, Locale locale, String threadKey) {
            this.part.getRooms().add(new GoogleChatRoomDTO(room, locale, threadKey));
            return this;
        }

        /**
         * Gets the parent builder.
         * @return the parent builder.
         */
        public NotificationDispatchAsyncMessageBuilder and() {
            this.parent.message.setGoogleChat(this.part);
            return this.parent;
        }
    }
}
