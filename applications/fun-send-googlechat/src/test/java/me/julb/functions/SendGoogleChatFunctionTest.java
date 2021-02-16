/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

import me.julb.library.dto.googlechat.GoogleChatMessageDTO;
import me.julb.springbootstarter.googlechat.services.GoogleChatService;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The function to send Google chat.
 * <P>
 * @author Julb.
 */
public class SendGoogleChatFunctionTest extends AbstractBaseTest {

    /**
     * The google chat service mocked.
     */
    @MockBean
    private GoogleChatService googleChatService;

    /**
     * The function catalog.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunction_thenSendGoogleChat()
        throws Exception {
        Consumer<GoogleChatMessageDTO> function = functionCatalog.lookup("sendGoogleChatFunction");

        GoogleChatMessageDTO dto = new GoogleChatMessageDTO();
        dto.setRoom("Room");
        dto.setText("Hello!");
        function.accept(dto);

        Mockito.verify(googleChatService).send(dto);

    }

}
