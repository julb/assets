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

package me.julb.applications.mail.configurations;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import me.julb.library.dto.mail.MailDTO;
import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.springbootstarter.mail.services.MailService;
import me.julb.springbootstarter.messaging.configurations.AbstractAsyncConsumerConfiguration;

/**
 * The local configuration.
 * <br>
 * @author Julb.
 */
@Configuration
public class CustomAsyncConfiguration extends AbstractAsyncConsumerConfiguration {

    /**
     * The service to send email.
     */
    @Autowired
    private MailService mailService;

    /**
     * Consumer for mails.
     * @return a function to consume mails.
     */
    @Bean
    public Consumer<Message<AsyncMessageDTO<MailDTO>>> mail() {
        return mailMessage -> {
            // Trace input.
            onReceiveStart(mailMessage);

            // Invoke consumer.
            mailService.send(mailMessage.getPayload().getBody());

            // Trace finished.
            onReceiveEnd(mailMessage);
        };
    }
}
