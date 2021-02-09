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

package me.julb.springbootstarter.monitoring.prometheus.pushmetrics.consumers;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import feign.Client;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.utility.FeignClientUtility;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.annotations.ConditionalOnPrometheusPushMetricsEnabled;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.consumers.PrometheusPushGatewayFeignClient.LocalConsumerConfiguration;

/**
 * The Prometheus Push Gateway Feign client.
 * <P>
 * @author Julb.
 */
@ConditionalOnPrometheusPushMetricsEnabled
@FeignClient(name = "prometheus-pushmetrics", url = "${prometheus.pushmetrics.pushgateway.endpoint.url}", configuration = LocalConsumerConfiguration.class)
public interface PrometheusPushGatewayFeignClient {

    /**
     * Push metrics to Prometheus Push gateway.
     * @param job the job.
     * @param instance the instance.
     * @param body the body.
     */
    @PostMapping(path = "/metrics/job/{job}/instance/{instance}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void create(@PathVariable("job") String job, @PathVariable("instance") String instance, @RequestBody String body);

    /**
     * Delete metrics from Prometheus Push gateway.
     * @param job the job.
     * @param instance the instance.
     */
    @DeleteMapping(path = "/metrics/job/{job}/instance/{instance}")
    void delete(@PathVariable("job") String job, @PathVariable("instance") String instance);

    /**
     * The local consumer properties.
     * <P>
     * @author Julb.
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "prometheus.pushmetrics.pushgateway")
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
     * <P>
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
