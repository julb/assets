package me.julb.functions;

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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.http.MediaType;

import me.julb.functions.dto.GenerateNotificationContentDTO;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.springbootstarter.templating.configurations.beans.TemplatingMode;
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
     * Unit test method.
     */
    @Test
    public void whenInvokingFunction_thenReturnTemplate()
        throws Exception {
        Function<GenerateNotificationContentDTO, Optional<LargeContentDTO>> function = functionCatalog.lookup("generateNotificationContentFunction");
        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        dto.setLocale("fr");
        dto.setName("test");
        dto.setTemplatingMode(TemplatingMode.TEXT);
        dto.setTrademark("julb.me");
        dto.setParameters(Map.of("user", Map.of("displayName", "John")));
        Optional<LargeContentDTO> contentOptional = function.apply(dto);
        Assertions.assertTrue(contentOptional.isPresent());
        LargeContentDTO content = contentOptional.get();
        Assertions.assertEquals(MediaType.TEXT_PLAIN_VALUE, content.getMimeType());
        Assertions.assertEquals("Bonjour, John", content.getContent());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunctionWithInvalidPayload_thenReturnEmpty()
        throws Exception {
        Function<GenerateNotificationContentDTO, Optional<LargeContentDTO>> function = functionCatalog.lookup("generateNotificationContentFunction");
        GenerateNotificationContentDTO dto = new GenerateNotificationContentDTO();
        Optional<LargeContentDTO> contentOptional = function.apply(dto);
        Assertions.assertFalse(contentOptional.isPresent());
    }
}
