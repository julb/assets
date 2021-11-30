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
package me.julb.springbootstarter.monitoring.prometheus.pushmetrics.configurations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.consumer.utility.HttpUrlConnectionUtility;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.annotations.ConditionalOnPrometheusPushMetricsEnabled;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.configurations.beans.PrometheusPushMetricsProperties;

import io.prometheus.client.exporter.HttpConnectionFactory;
import io.prometheus.client.exporter.PushGateway;

/**
 * A configuration class to push metrics to Prometheus gateway.
 * <br>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(PrometheusPushMetricsProperties.class)
@ConditionalOnPrometheusPushMetricsEnabled
@PropertySource(value = "classpath:/me/julb/springbootstarter/monitoring/prometheus/pushmetrics/default.properties")
public class PrometheusPushMetricsConfiguration {

    /**
     * The Prometheus push gateway client bean.
     * @return the Prometheus push gateway client bean.
     */
    @Bean
    public PushGateway prometheusPushGatewayClientBean(PrometheusPushMetricsProperties prometheusPushMetricsProperties) {
        try {
            PushGateway pushGateway = new PushGateway(new URL(prometheusPushMetricsProperties.getEndpoint().getUrl()));
            pushGateway.setConnectionFactory(new HttpConnectionFactory() {
                @Override
                public HttpURLConnection create(String url)
                    throws IOException {
                    return HttpUrlConnectionUtility.buildForUrl(url, prometheusPushMetricsProperties.getEndpoint());
                }
            });
            return pushGateway;
        } catch(MalformedURLException e) {
            throw new InternalServerErrorException(e);
        }
    }
}