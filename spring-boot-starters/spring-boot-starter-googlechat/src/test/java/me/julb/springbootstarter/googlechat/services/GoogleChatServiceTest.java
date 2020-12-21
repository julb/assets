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
package me.julb.springbootstarter.googlechat.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import me.julb.library.dto.googlechat.MessageDTO;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.springbootstarter.googlechat.configurations.GoogleChatConfiguration;
import me.julb.springbootstarter.googlechat.consumers.GoogleChatFeignClient;
import me.julb.springbootstarter.googlechat.consumers.GoogleChatTextBodyDTO;
import me.julb.springbootstarter.googlechat.services.impl.GoogleChatServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Google Chat service unit test.
 * <P>
 * @author Julb.
 */
@ContextConfiguration(classes = {GoogleChatConfiguration.class, GoogleChatServiceImpl.class})
public class GoogleChatServiceTest extends AbstractBaseTest {

    /**
     * The Google chat service.
     */
    @Autowired
    private GoogleChatService googleChatService;

    /**
     * The Google chat feign client.
     */
    @MockBean
    private GoogleChatFeignClient googleChatFeignClient;

    /**
     * Test method.
     */
    @Test
    public void whenSendingGoogleChatNotificationWithDefaultThreadKey_thenMessageSentToDefaultThreadKey()
        throws Exception {
        MessageDTO dto = new MessageDTO();
        dto.setRoom("__NAME_A__");
        dto.setText("hello");
        googleChatService.send(dto);

        Mockito.verify(googleChatFeignClient).createTextMessage(Mockito.eq("__SPACE_ID_A__"), Mockito.eq("__KEY_A__"), Mockito.eq("__TOKEN_A__"), Mockito.eq("__DEFAULT_THREAD_KEY_A__"), Mockito.eq(new GoogleChatTextBodyDTO(dto.getText())));
    }

    /**
     * Test method.
     */
    @Test
    public void whenSendingGoogleChatNotificationWithCustomThreadKey_thenMessageSentToCustomThreadKey()
        throws Exception {
        MessageDTO dto = new MessageDTO();
        dto.setRoom("__NAME_A__");
        dto.setText("hello");
        dto.setThreadKey("__OVERRIDE_THREAD_KEY__");

        googleChatService.send(dto);

        Mockito.verify(googleChatFeignClient).createTextMessage(Mockito.eq("__SPACE_ID_A__"), Mockito.eq("__KEY_A__"), Mockito.eq("__TOKEN_A__"), Mockito.eq(dto.getThreadKey()), Mockito.eq(new GoogleChatTextBodyDTO(dto.getText())));
    }

    /**
     * Test method.
     */
    @Test
    public void whenSendingGoogleChatNotificationNoThreadKey_thenMessageSentWithoutThreadKey()
        throws Exception {
        MessageDTO dto = new MessageDTO();
        dto.setRoom("__NAME_B__");
        dto.setText("hello");
        googleChatService.send(dto);

        Mockito.verify(googleChatFeignClient).createTextMessage(Mockito.eq("__SPACE_ID_B__"), Mockito.eq("__KEY_B__"), Mockito.eq("__TOKEN_B__"), Mockito.isNull(), Mockito.eq(new GoogleChatTextBodyDTO(dto.getText())));
    }

    /**
     * Test method.
     */
    @Test
    public void whenSendingGoogleChatNotificationWithUnknownRoom_thenThrowsResourceNotFoundException()
        throws Exception {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            MessageDTO dto = new MessageDTO();
            dto.setRoom("do-not-exist");
            dto.setText("hello");
            googleChatService.send(dto);
        });

        Mockito.verify(googleChatFeignClient, Mockito.never()).createTextMessage(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }
}
