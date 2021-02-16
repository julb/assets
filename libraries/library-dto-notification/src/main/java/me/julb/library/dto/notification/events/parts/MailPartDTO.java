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
package me.julb.library.dto.notification.events.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.mail.MailAttachmentDTO;
import me.julb.library.dto.mail.MailInlineAttachmentDTO;

/**
 * A DTO configuring mail notification.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class MailPartDTO {

  //@formatter:off
    /**
    * The TO recipients of the email.
    * -- GETTER --
    * Getter for {@link #tos} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #tos} property.
    * @param tos the value to set.
    */
    //@formatter:on
    @Valid
    private Collection<MailRecipientDTO> tos = new HashSet<>();

   //@formatter:off
    /**
    * The CC recipients of the email.
    * -- GETTER --
    * Getter for {@link #ccs} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #ccs} property.
    * @param ccs the value to set.
    */
    //@formatter:on
    @Valid
    private Collection<MailRecipientDTO> ccs = new HashSet<>();

   //@formatter:off
    /**
    * The BCC recipients of the email.
    * -- GETTER --
    * Getter for {@link #bccs} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #bccs} property.
    * @param bccs the value to set.
    */
    //@formatter:on
    @Valid
    private Collection<MailRecipientDTO> bccs = new HashSet<>();

    //@formatter:off
     /**
     * The inlineAttachments attribute.
     * -- GETTER --
     * Getter for {@link #inlineAttachments} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #inlineAttachments} property.
     * @param inlineAttachments the value to set.
     */
     //@formatter:on
    @Valid
    private Collection<MailInlineAttachmentDTO> inlineAttachments = new ArrayList<>();

    //@formatter:off
     /**
     * The attachments attribute.
     * -- GETTER --
     * Getter for {@link #attachments} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #attachments} property.
     * @param attachments the value to set.
     */
     //@formatter:on
    @Valid
    private Collection<MailAttachmentDTO> attachments = new ArrayList<>();

    /**
     * Gets the requested locales.
     * @return the requested locales.
     */
    public Set<Locale> requestedLocales() {
        Set<Locale> locales = new HashSet<>();
        tos.forEach((recipient) -> {
            locales.add(recipient.getLocale());
        });
        ccs.forEach((recipient) -> {
            locales.add(recipient.getLocale());
        });
        bccs.forEach((recipient) -> {
            locales.add(recipient.getLocale());
        });
        return locales;
    }

    /**
     * Gets the TO recipients per locale.
     * @param locale the locale.
     * @return the TO recipients per locale.
     */
    public Collection<MailRecipientDTO> tos(Locale locale) {
        return tos.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).collect(Collectors.toList());
    }

    /**
     * Gets the TO email addresses per locale.
     * @param locale the locale.
     * @return the TO email addresses numbers per locale.
     */
    public Collection<String> toEmails(Locale locale) {
        return tos.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).map(MailRecipientDTO::getMail).collect(Collectors.toList());
    }

    /**
     * Gets the CC recipients per locale.
     * @param locale the locale.
     * @return the CC recipients per locale.
     */
    public Collection<MailRecipientDTO> ccs(Locale locale) {
        return ccs.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).collect(Collectors.toList());
    }

    /**
     * Gets the CC email addresses per locale.
     * @param locale the locale.
     * @return the CC email addresses numbers per locale.
     */
    public Collection<String> ccEmails(Locale locale) {
        return ccs.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).map(MailRecipientDTO::getMail).collect(Collectors.toList());
    }

    /**
     * Gets the BCC recipients per locale.
     * @param locale the locale.
     * @return the BCC recipients per locale.
     */
    public Collection<MailRecipientDTO> bccs(Locale locale) {
        return bccs.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).collect(Collectors.toList());
    }

    /**
     * Gets the BCC email addresses per locale.
     * @param locale the locale.
     * @return the BCC email addresses numbers per locale.
     */
    public Collection<String> bccEmails(Locale locale) {
        return bccs.stream().filter((recipient) -> {
            return recipient.getLocale().equals(locale);
        }).map(MailRecipientDTO::getMail).collect(Collectors.toList());
    }

}
