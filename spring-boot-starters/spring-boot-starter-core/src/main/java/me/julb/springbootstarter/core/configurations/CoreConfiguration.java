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

package me.julb.springbootstarter.core.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import me.julb.springbootstarter.core.configs.ConfigSourceService;
import me.julb.springbootstarter.core.localization.LocaleService;
import me.julb.springbootstarter.core.messages.MessageSourceService;
import me.julb.springbootstarter.core.rendering.ContentRenderService;

/**
 * The configuration for trademark property source configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@PropertySource("classpath:tm.properties")
public class CoreConfiguration {

    /**
     * The message source service.
     * @return the message source service.
     */
    @Bean
    public ConfigSourceService configSourceService() {
        return new ConfigSourceService();
    }

    /**
     * The message source service.
     * @return the message source service.
     */
    @Bean
    public MessageSourceService messageSourceService() {
        return new MessageSourceService();
    }

    /**
     * The content render service.
     * @return the content render service.
     */
    @Bean
    public ContentRenderService contentRenderService() {
        return new ContentRenderService();
    }

    /**
     * The locale service.
     * @return the locale service.
     */
    @Bean
    public LocaleService localeService() {
        return new LocaleService();
    }
}
