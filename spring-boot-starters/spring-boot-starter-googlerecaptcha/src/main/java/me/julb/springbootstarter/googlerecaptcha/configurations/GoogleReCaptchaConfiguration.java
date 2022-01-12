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
package me.julb.springbootstarter.googlerecaptcha.configurations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import me.julb.springbootstarter.consumer.reactive.utility.NettyClientUtility;
import me.julb.springbootstarter.googlerecaptcha.annotations.ConditionalOnGoogleReCaptchaEnabled;
import me.julb.springbootstarter.googlerecaptcha.configurations.beans.GoogleReCaptchaProperties;

/**
 * The Google ReCaptcha configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(GoogleReCaptchaProperties.class)
@ConditionalOnGoogleReCaptchaEnabled
@PropertySource("classpath:/me/julb/springbootstarter/googlerecaptcha/default.properties")
public class GoogleReCaptchaConfiguration {

    /**
     * Builds a rest template for Google ReCaptcha.
     * @return the rest template.
     */
    @Bean
    public WebClient googleReCaptchaV3WebClient(GoogleReCaptchaProperties googleReCaptchaProperties) {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(NettyClientUtility.build(googleReCaptchaProperties.getEndpoint())))
            .baseUrl(googleReCaptchaProperties.getEndpoint().getUrl())
            .build();
    }
}
