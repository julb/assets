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

package me.julb.springbootstarter.templating.services;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.springbootstarter.templating.configurations.TemplatingConfiguration;
import me.julb.springbootstarter.templating.services.impl.TemplatingServiceImpl;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The templating service unit test.
 * <P>
 * @author Julb.
 */
@ContextConfiguration(classes = {TemplatingServiceImpl.class, TemplatingConfiguration.class})
public class TemplatingServiceTest extends AbstractBaseTest {

    /**
     * The mail service.
     */
    @Autowired
    private TemplatingService templatingService;

    /**
     * Test method.
     */
    @Test
    public void whenRenderingTextTemplate_thenOK()
        throws Exception {
        LargeContentDTO render = templatingService.render("test.txt", Map.of("value", "John"));
        Assertions.assertEquals(MediaType.TEXT_PLAIN_VALUE, render.getMimeType());
        Assertions.assertEquals("Test: John", render.getContent());
    }

    /**
     * Test method.
     */
    @Test
    public void whenRenderingHtmlTemplate_thenOK()
        throws Exception {
        LargeContentDTO render = templatingService.render("test.html", Map.of("value", "John"));
        Assertions.assertEquals(MediaType.TEXT_HTML_VALUE, render.getMimeType());
        Assertions.assertEquals("Hello: <span>John</span>", render.getContent());
    }

    /**
     * Test method.
     */
    @Test
    public void whenRenderingUnknownTemplate_thenRaiseResourceNotFound()
        throws Exception {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            templatingService.render("none.html", Map.of("value", "John"));
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenRenderingTemplateWithMissingValue_thenReturnEmptyString()
        throws Exception {
        LargeContentDTO render = templatingService.render("test.html", Map.of());
        Assertions.assertEquals(MediaType.TEXT_HTML_VALUE, render.getMimeType());
        Assertions.assertEquals("Hello: <span></span>", render.getContent());
    }

}
