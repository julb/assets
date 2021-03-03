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
package me.julb.library.dto.notification.events;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.dto.notification.events.parts.GoogleChatPartDTO;
import me.julb.library.dto.notification.events.parts.MailPartDTO;
import me.julb.library.dto.notification.events.parts.SmsPartDTO;
import me.julb.library.dto.notification.events.parts.WebPartDTO;

/**
 * A message linked to a resource event.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class NotificationDispatchAsyncMessageDTO extends AsyncMessageDTO<Void> {

    //@formatter:off
     /**
     * The kind attribute.
     * -- GETTER --
     * Getter for {@link #kind} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #kind} property.
     * @param kind the value to set.
     */
     //@formatter:on
    private NotificationKind kind;

    //@formatter:off
     /**
     * The parameters attribute.
     * -- GETTER --
     * Getter for {@link #parameters} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #parameters} property.
     * @param parameters the value to set.
     */
     //@formatter:on
    private Map<String, Object> parameters = new HashMap<>();

    //@formatter:off
     /**
     * The mail attribute.
     * -- GETTER --
     * Getter for {@link #mail} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mail} property.
     * @param mail the value to set.
     */
     //@formatter:on
    private MailPartDTO mail;

    //@formatter:off
     /**
     * The sms attribute.
     * -- GETTER --
     * Getter for {@link #sms} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #sms} property.
     * @param sms the value to set.
     */
     //@formatter:on
    private SmsPartDTO sms;

    //@formatter:off
     /**
     * The web attribute.
     * -- GETTER --
     * Getter for {@link #web} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #web} property.
     * @param web the value to set.
     */
     //@formatter:on
    private WebPartDTO web;

    //@formatter:off
     /**
     * The googleChat attribute.
     * -- GETTER --
     * Getter for {@link #googleChat} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #googleChat} property.
     * @param googleChat the value to set.
     */
     //@formatter:on
    private GoogleChatPartDTO googleChat;

}
