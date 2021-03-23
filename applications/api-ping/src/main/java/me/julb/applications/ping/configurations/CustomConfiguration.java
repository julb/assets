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

package me.julb.applications.ping.configurations;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.sleuth.instrument.web.client.feign.SleuthFeignBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import feign.Client;
import feign.Contract;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import me.julb.applications.ping.configurations.properties.ApplicationProperties;
import me.julb.applications.ping.configurations.properties.TargetProperties;
import me.julb.applications.ping.consumers.ApiPingTargetFeignClient;
import me.julb.springbootstarter.consumer.utility.FeignClientUtility;

/**
 * The local configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
@Import(FeignClientsConfiguration.class)
public class CustomConfiguration {

    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Builds a list of remote ping feign clients.
     * @param beanFactory the bean factory.
     * @param decoder the default feign decoder.
     * @param encoder the default feign encoder.
     * @param contract the default feign contract.
     * @param retryer the retryer.
     * @param errorDecoder the error decoder.
     * @return the list of remote ping feign clients.
     */
    @Bean
    public Map<String, ApiPingTargetFeignClient> apiPingTargetFeignClients(BeanFactory beanFactory, Decoder decoder, Encoder encoder, Contract contract, Retryer retryer, ErrorDecoder errorDecoder) {
        Map<String, ApiPingTargetFeignClient> clients = new HashMap<>();

        // Targets to ping.
        for (TargetProperties targetProperties : applicationProperties.getTargets()) {
            // Remote properties.
            Client customClient = FeignClientUtility.feignClientUtil(targetProperties.getEndpoint());

            //@formatter:off
            ApiPingTargetFeignClient client = SleuthFeignBuilder
                .builder(beanFactory, customClient)
                .encoder(encoder)
                .decoder(decoder)
                .contract(contract)
                .retryer(retryer)
                .errorDecoder(errorDecoder)
                .target(ApiPingTargetFeignClient.class, targetProperties.getEndpoint().getUrl());
            //@formatter:on

            clients.put(targetProperties.getId(), client);
        }

        return clients;
    }

}
