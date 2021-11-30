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

package me.julb.springbootstarter.googlechat.consumers;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.feign.utility.FeignClientUtility;
import me.julb.springbootstarter.googlechat.annotations.ConditionalOnGoogleChatEnabled;
import me.julb.springbootstarter.googlechat.consumers.GoogleChatFeignClient.LocalConsumerConfiguration;

import feign.Client;

/**
 * The Google Chat Feign client.
 * <br>
 * @author Julb.
 */
@ConditionalOnGoogleChatEnabled
@FeignClient(name = "google-chat", url = "${google.chat.endpoint.url}", configuration = LocalConsumerConfiguration.class)
public interface GoogleChatFeignClient {

    /**
     * Post text message to Google Chat.
     * @param spaceId the Google chat space ID.
     * @param key the key to be able to post to the space.
     * @param token the token to be able to post to the space.
     * @param threadKey the thread key, or <code>null</code> if not needed.
     * @param body the text message wrapped in a body.
     */
    @PostMapping(path = "/v1/spaces/{spaceId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createTextMessage(@PathVariable("spaceId") String spaceId, @RequestParam("key") String key, @RequestParam("token") String token, @RequestParam(required = false, value = "threadKey") String threadKey, @RequestBody GoogleChatTextBodyDTO body);

    /**
     * The local consumer properties.
     * <br>
     * @author Julb.
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "google.chat")
    class LocalConsumerProperties {

        //@formatter:off
         /**
         * The endpoint attribute.
         * -- GETTER --
         * Getter for {@link #endpoint} property.
         * @return the value.
         * -- SETTER --
         * Setter for {@link #endpoint} property.
         * @param endpoint the value to set.
         */
         //@formatter:on
        private ConsumerEndpointProperties endpoint;
    }

    /**
     * The local consumer configuration.
     * <br>
     * @author Julb.
     */
    @EnableConfigurationProperties({LocalConsumerProperties.class})
    class LocalConsumerConfiguration {

        /**
         * The consumer properties.
         */
        @Autowired
        private LocalConsumerProperties properties;

        /**
         * Builds a feign client instance.
         * @return the feign client instance.
         */
        @Bean
        public Client feignClient() {
            return FeignClientUtility.feignClientUtil(properties.getEndpoint());
        }
    }
}
