package me.julb.functions;

import java.util.Locale;
import java.util.Map;

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

import java.util.Optional;
import java.util.function.Function;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.http.MediaType;

import me.julb.library.dto.notification.events.NotificationDispatchType;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.springbootstarter.templating.notification.services.NotificationTemplatingService;
import me.julb.springbootstarter.templating.notification.services.dto.GenerateNotificationContentDTO;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The function to generate notification content
 * <P>
 * @author Julb.
 */
public class GenerateNotificationContentFunctionTest extends AbstractBaseTest {
    /**
     * The function catalog.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    /**
     * The notification templating service.
     */
    @MockBean
    private NotificationTemplatingService notificationTemplatingService;

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunction_thenReturnTemplate()
        throws Exception {

        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        dto.setLocale(Locale.FRANCE);
        dto.setName("test");
        dto.setType(NotificationDispatchType.SMS);
        dto.setTm("julb.me");
        dto.setParameters(Map.of("user", Map.of("displayName", "John")));

        LargeContentDTO largeContent = new LargeContentDTO();
        largeContent.setMimeType(MediaType.TEXT_PLAIN_VALUE);
        largeContent.setContent("Hello");
        Mockito.when(notificationTemplatingService.render(dto)).thenReturn(largeContent);

        // Call function
        Function<GenerateNotificationContentDTO, Optional<LargeContentDTO>> function = functionCatalog.lookup("generateNotificationContentFunction");
        Optional<LargeContentDTO> contentOptional = function.apply(dto);

        Assertions.assertTrue(contentOptional.isPresent());
        LargeContentDTO content = contentOptional.get();
        Assertions.assertEquals(largeContent.getMimeType(), content.getMimeType());
        Assertions.assertEquals(largeContent.getContent(), content.getContent());

        Mockito.verify(notificationTemplatingService).render(dto);
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunctionWithInvalidPayload_thenReturnEmpty()
        throws Exception {
        Mockito.when(notificationTemplatingService.render(Mockito.any())).thenThrow(ConstraintViolationException.class);

        Function<GenerateNotificationContentDTO, Optional<LargeContentDTO>> function = functionCatalog.lookup("generateNotificationContentFunction");

        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        Optional<LargeContentDTO> contentOptional = function.apply(dto);
        Assertions.assertFalse(contentOptional.isPresent());

        Mockito.verify(notificationTemplatingService).render(dto);
    }
}
