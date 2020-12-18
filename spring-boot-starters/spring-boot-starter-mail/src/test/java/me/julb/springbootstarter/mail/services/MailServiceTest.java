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
package me.julb.springbootstarter.mail.services;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import javax.mail.internet.MimeMessage;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import me.julb.library.dto.mail.MailDTO;
import me.julb.springbootstarter.mail.services.impl.MailServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Email service unit test.
 * <P>
 * @author Julb.
 */
@ContextConfiguration(classes = {MailServiceImpl.class, MailSenderAutoConfiguration.class})
public class MailServiceTest extends AbstractBaseTest {

    /**
     * The mail service.
     */
    @Autowired
    private MailService mailService;

    /**
     * The greenMail attribute.
     */
    @RegisterExtension
    static GreenMailExtension GREEN_MAIL = new GreenMailExtension(ServerSetupTest.SMTP);

    /**
     * Test method.
     */
    @Test
    public void whenSendingEmail_thenMailSent()
        throws Exception {
        MailDTO dto = new MailDTO();
        dto.setFrom("no-reply@julb.me");
        dto.setTos(Lists.newArrayList("contact@julb.me"));
        dto.setCcs(Lists.newArrayList("cc@julb.me"));
        dto.setBccs(Lists.newArrayList("bcc@julb.me"));
        dto.setSubject("Some Subject");
        dto.setHtml("<html><body>Some HTML content</body></html>");
        mailService.send(dto);

        MimeMessage[] mimeMessages = GREEN_MAIL.getReceivedMessages();
        Assertions.assertEquals(3, mimeMessages.length);

        MimeMessage mimeMessage = mimeMessages[0];
        Assertions.assertEquals(dto.getFrom(), mimeMessage.getFrom()[0].toString());
        Assertions.assertEquals(dto.getTos().iterator().next(), mimeMessage.getRecipients(javax.mail.Message.RecipientType.TO)[0].toString());
        Assertions.assertEquals(dto.getCcs().iterator().next(), mimeMessage.getRecipients(javax.mail.Message.RecipientType.CC)[0].toString());
        Assertions.assertEquals(dto.getSubject(), mimeMessage.getSubject());
        Assertions.assertEquals(dto.getHtml() + "\r\n", mimeMessage.getContent().toString());

    }
}
