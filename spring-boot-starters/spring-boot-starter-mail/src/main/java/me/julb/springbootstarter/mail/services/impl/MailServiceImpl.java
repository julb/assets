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
package me.julb.springbootstarter.mail.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.library.dto.mail.MailAttachmentDTO;
import me.julb.library.dto.mail.MailDTO;
import me.julb.library.dto.mail.MailInlineAttachmentDTO;
import me.julb.library.utility.constants.Chars;
import me.julb.springbootstarter.mail.services.MailService;

/**
 * Email service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
public class MailServiceImpl implements MailService {

    /**
     * The mail sender.
     */
    @Autowired
    private JavaMailSender mailSender;

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(@NotNull @Valid MailDTO mailDto) {
        LOGGER.debug("Sending mail : {}.", mailDto);

        mailSender.send(new MimeMessagePreparator() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void prepare(MimeMessage mimeMessage)
                throws Exception {
                MimeMultipart content = new MimeMultipart("related");

                // Add HTML part.
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setText(mailDto.getHtml(), StandardCharsets.UTF_8.toString(), "html");
                htmlPart.setContentID("__html_body");
                content.addBodyPart(htmlPart);

                // Add inline attachments.
                for (MailInlineAttachmentDTO inlineAttachment : mailDto.getInlineAttachments()) {
                    // Extract inline attachment as a datasource.
                    byte[] inlineAttachmentAsBytes = Base64.getDecoder().decode(inlineAttachment.getContentBase64());
                    DataSource ds = new ByteArrayDataSource(inlineAttachmentAsBytes, inlineAttachment.getMimeType());

                    // Attach mime body part.
                    MimeBodyPart inlineAttachmentBodyPart = new MimeBodyPart();
                    inlineAttachmentBodyPart.setDataHandler(new DataHandler(ds));
                    inlineAttachmentBodyPart.setContentID(StringUtils.join(Chars.LEFT_ANGLE_BRACKET, inlineAttachment.getContentId(), Chars.RIGHT_ANGLE_BRACKET));
                    inlineAttachmentBodyPart.setDisposition(MimeBodyPart.INLINE);
                    content.addBodyPart(inlineAttachmentBodyPart);
                }

                // Add attachment
                for (MailAttachmentDTO attachment : mailDto.getAttachments()) {
                    // Extract inline attachment as a datasource.
                    byte[] attachmentAsBytes = Base64.getDecoder().decode(attachment.getContentBase64());
                    DataSource ds = new ByteArrayDataSource(attachmentAsBytes, attachment.getMimeType());

                    // Attach mime body part.
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    attachmentBodyPart.setDataHandler(new DataHandler(ds));
                    attachmentBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
                    attachmentBodyPart.setFileName(attachment.getFileName());
                    content.addBodyPart(attachmentBodyPart);
                }

                // Mime message.
                mimeMessage.setFrom(mailDto.getFrom());
                mimeMessage.setSentDate(new Date());
                mimeMessage.setSubject(mailDto.getSubject());
                mimeMessage.setContent(content);

                try {
                    if (CollectionUtils.isNotEmpty(mailDto.getTos())) {
                        for (String email : mailDto.getTos()) {
                            mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(email));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(mailDto.getCcs())) {
                        for (String email : mailDto.getCcs()) {
                            mimeMessage.addRecipient(RecipientType.CC, new InternetAddress(email));
                        }
                    }
                    if (CollectionUtils.isNotEmpty(mailDto.getBccs())) {
                        for (String email : mailDto.getBccs()) {
                            mimeMessage.addRecipient(RecipientType.BCC, new InternetAddress(email));
                        }
                    }
                } catch (AddressException e) {
                    LOGGER.error("An error occurred with email address.", e);
                }
            }
        });

        LOGGER.debug("Mail sent successfully.");
    }

}
