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

package me.julb.applications.ping.configurations;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.instrument.web.client.TraceExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import me.julb.applications.ping.configurations.properties.ApplicationProperties;
import me.julb.applications.ping.configurations.properties.TargetProperties;
import me.julb.springbootstarter.consumer.reactive.utility.NettyClientUtility;

/**
 * The local configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class CustomConfiguration {

    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Builds a list of remote ping web clients.
     * @param applicationContext the application context.
     * @return the list of remote ping web clients.
     */
    @Bean
    public Map<String, WebClient> apiPingWebClients(GenericApplicationContext applicationContext) {
        Map<String, WebClient> clients = new HashMap<>();

        // Targets to ping.
        for (TargetProperties targetProperties : applicationProperties.getTargets()) {
            //@formatter:off
            WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(NettyClientUtility.build(targetProperties.getEndpoint())))
                .baseUrl(targetProperties.getEndpoint().getUrl())
                .filter(TraceExchangeFilterFunction.create(applicationContext))
                .build();
            //@formatter:on

            clients.put(targetProperties.getId(), client);
        }

        return clients;
    }

}
