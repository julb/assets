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
package me.julb.springbootstarter.web.configurations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import me.julb.springbootstarter.web.aspects.captcha.CaptchaValidAspect;
import me.julb.springbootstarter.web.configurations.beans.GoogleReCaptchaProperties;
import me.julb.springbootstarter.web.services.CaptchaService;
import me.julb.springbootstarter.web.services.impl.GoogleReCaptchaV3ServiceImpl;

/**
 * The Google ReCaptcha configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(GoogleReCaptchaProperties.class)
@ConditionalOnProperty(prefix = "google.recaptcha", name = "enabled", matchIfMissing = false)
public class GoogleReCaptchaConfiguration {

    /**
     * Builds a rest template for Google ReCaptcha.
     * @return the rest template.
     */
    @Bean
    public RestTemplate googleReCaptchaRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Builds a captcha valid aspect.
     * @return the captcha valid aspect.
     */
    @Bean
    public CaptchaValidAspect captchaValidAspect() {
        return new CaptchaValidAspect();
    }

    /**
     * Builds a captcha service.
     * @return the captcha service.
     */
    @Bean
    public CaptchaService captchaService() {
        return new GoogleReCaptchaV3ServiceImpl();
    }
}
