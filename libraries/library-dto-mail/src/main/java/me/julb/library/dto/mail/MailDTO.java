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
package me.julb.library.dto.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.MailContent;
import me.julb.library.utility.validator.constraints.MailSubject;

/**
 * DTO to send a mail.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class MailDTO {

    //@formatter:off
     /**
     * The from attribute.
     * -- GETTER --
     * Getter for {@link #from} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #from} property.
     * @param from the value to set.
     */
     //@formatter:on
    @NotBlank
    @NotNull
    @Email
    private String from;

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
    private Collection<@Email @NotBlank String> tos = new ArrayList<>();

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
    private Collection<@Email @NotBlank String> ccs = new ArrayList<>();

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
    private Collection<@Email @NotBlank String> bccs = new ArrayList<>();

    //@formatter:off
     /**
     * The subject attribute.
     * -- GETTER --
     * Getter for {@link #subject} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #subject} property.
     * @param subject the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @MailSubject
    private String subject;

    //@formatter:off
     /**
     * The HTML content of the mail.
     * -- GETTER --
     * Getter for {@link #html} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #html} property.
     * @param html the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @MailContent
    private String html;

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
    private Collection<MailAttachmentDTO> attachments = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        //@formatter:off
        return String.format("From: <%s>, Tos: <%s>, Ccs: <%s>, Bcc: <%s>, Subject: %s", 
            from,
            Arrays.toString(tos.toArray()),
            Arrays.toString(ccs.toArray()),
            Arrays.toString(bccs.toArray()),
            subject
        );
        //@formatter:on
    }

}
