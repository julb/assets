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

package me.julb.functions;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import me.julb.library.dto.sms.SmsMessageDTO;
import me.julb.springbootstarter.sms.services.SmsService;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The function to send sms.
 * <br>
 * @author Julb.
 */
public class SendSmsFunctionTest extends AbstractBaseTest {

    /**
     * The SMS service mocked.
     */
    @MockBean
    private SmsService smsService;

    /**
     * The function catalog.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunction_thenSendSms()
        throws Exception {
        Consumer<Message<SmsMessageDTO>> function = functionCatalog.lookup("sendSmsFunction");

        SmsMessageDTO dto = new SmsMessageDTO();
        dto.setE164Number("+33123456789");
        dto.setText("Hello, John");
        function.accept(MessageBuilder.withPayload(dto).build());

        Mockito.verify(smsService).send(dto);
    }
}
