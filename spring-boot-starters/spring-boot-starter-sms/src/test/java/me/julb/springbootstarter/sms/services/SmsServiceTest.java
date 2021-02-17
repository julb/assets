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
package me.julb.springbootstarter.sms.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import me.julb.springbootstarter.sms.configurations.SmsConfiguration;
import me.julb.springbootstarter.sms.services.impl.SmsServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * SMS service unit test.
 * <P>
 * @author Julb.
 */
@ContextConfiguration(classes = {SmsServiceImpl.class, SmsConfiguration.class})
public class SmsServiceTest extends AbstractBaseTest {

    /**
     * The mail service.
     */
    @Autowired
    private SmsService smsService;

    /**
     * Test method.
     */
    @Test
    public void whenSendingSms_thenMailSent()
        throws Exception {
        Assertions.assertNotNull(smsService);
    }
}
