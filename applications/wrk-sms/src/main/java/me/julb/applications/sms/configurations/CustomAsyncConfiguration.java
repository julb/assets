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

package me.julb.applications.sms.configurations;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.dto.sms.SmsMessageDTO;
import me.julb.springbootstarter.messaging.configurations.AbstractAsyncConsumerConfiguration;
import me.julb.springbootstarter.sms.services.SmsService;

/**
 * The local configuration.
 * <br>
 * @author Julb.
 */
@Configuration
public class CustomAsyncConfiguration extends AbstractAsyncConsumerConfiguration {

    /**
     * The SMS service.
     */
    @Autowired
    private SmsService smsService;

    /**
     * Consumer for SMS messages.
     * @return a function to consume SMS messages.
     */
    @Bean
    public Consumer<Message<AsyncMessageDTO<SmsMessageDTO>>> sms() {
        return smsMessage -> {
            onReceiveStart(smsMessage);

            // Invoke consumer.
            smsService.send(smsMessage.getPayload().getBody());

            onReceiveEnd(smsMessage);
        };
    }
}
