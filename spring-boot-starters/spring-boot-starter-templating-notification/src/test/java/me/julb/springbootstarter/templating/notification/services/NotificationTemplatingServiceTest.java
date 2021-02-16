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

package me.julb.springbootstarter.templating.notification.services;

import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import me.julb.library.dto.notification.events.NotificationDispatchType;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.dto.simple.content.LargeContentWithSubjectDTO;
import me.julb.springbootstarter.templating.configurations.TemplatingConfiguration;
import me.julb.springbootstarter.templating.notification.configurations.NotificationTemplatingConfiguration;
import me.julb.springbootstarter.templating.notification.services.dto.GenerateNotificationContentDTO;
import me.julb.springbootstarter.templating.notification.services.impl.NotificationTemplatingServiceImpl;
import me.julb.springbootstarter.templating.services.impl.TemplatingServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The templating service unit test.
 * <P>
 * @author Julb.
 */
@ContextConfiguration(classes = {NotificationTemplatingServiceImpl.class, TemplatingServiceImpl.class, TemplatingConfiguration.class, NotificationTemplatingConfiguration.class})
public class NotificationTemplatingServiceTest extends AbstractBaseTest {

    /**
     * The notification templating service.
     */
    @Autowired
    private NotificationTemplatingService notificationTemplatingService;

    /**
     * Unit test method.
     */
    @Test
    public void whenRenderingSmsNotification_thenReturnContent()
        throws Exception {
        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        dto.setLocale(Locale.CANADA_FRENCH);
        dto.setName("test");
        dto.setType(NotificationDispatchType.SMS);
        dto.setTm("julb.me");
        dto.setParameters(Map.of("user", Map.of("displayName", "John")));
        LargeContentDTO content = notificationTemplatingService.render(dto);
        Assertions.assertEquals(MediaType.TEXT_PLAIN_VALUE, content.getMimeType());
        Assertions.assertEquals("Bienvenue, John", content.getContent());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRenderingMailNotification_thenReturnContent()
        throws Exception {
        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        dto.setLocale(Locale.CANADA_FRENCH);
        dto.setName("test");
        dto.setType(NotificationDispatchType.MAIL);
        dto.setTm("julb.me");
        dto.setParameters(Map.of("user", Map.of("displayName", "John")));
        LargeContentWithSubjectDTO content = (LargeContentWithSubjectDTO) notificationTemplatingService.render(dto);
        Assertions.assertEquals(MediaType.TEXT_HTML_VALUE, content.getMimeType());
        Assertions.assertEquals("Bienvenue", content.getSubject());
        Assertions.assertEquals("<html>\n    <body>\n        Bienvenue, <span>John</span>\n    </body>\n</html>\n", content.getContent());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRenderingSmsNotificationWithoutExistingCountry_thenReturnContent()
        throws Exception {
        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        dto.setLocale(Locale.FRANCE);
        dto.setName("test");
        dto.setType(NotificationDispatchType.SMS);
        dto.setTm("julb.me");
        dto.setParameters(Map.of("user", Map.of("displayName", "John")));
        LargeContentDTO content = notificationTemplatingService.render(dto);
        Assertions.assertEquals(MediaType.TEXT_PLAIN_VALUE, content.getMimeType());
        Assertions.assertEquals("Bonjour, John", content.getContent());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenRenderingSmsNotificationWithoutExistingTrademark_thenReturnContent()
        throws Exception {
        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        dto.setLocale(Locale.FRANCE);
        dto.setName("test");
        dto.setType(NotificationDispatchType.SMS);
        dto.setTm("julb.io");
        dto.setParameters(Map.of("user", Map.of("displayName", "John")));
        LargeContentDTO content = notificationTemplatingService.render(dto);
        Assertions.assertEquals(MediaType.TEXT_PLAIN_VALUE, content.getMimeType());
        Assertions.assertEquals("Bonjour, John", content.getContent());
    }
}
