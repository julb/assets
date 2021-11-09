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

package me.julb.functions;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

import lombok.extern.slf4j.Slf4j;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;

/**
 * The function to send mail.
 * <br>
 * @author Julb. O
 */
@Slf4j
public class PushPrometheusMetricsFunction implements Consumer<Message<MetricsCreationWrapperDTO>> {

    /**
     * The mail service.
     */
    @Autowired
    private PrometheusMetricsPushService prometheusMetricsPushService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(Message<MetricsCreationWrapperDTO> metricsCreationWrapper) {
        try {
            LOGGER.debug("Received invokation to send metrics.");
            prometheusMetricsPushService.pushAll(metricsCreationWrapper.getPayload());
            LOGGER.debug("Method invoked successfully.");
        } catch (Exception e) {
            LOGGER.error("Fail to invoke function due to the following exception: {}.", e.getMessage());
            LOGGER.debug("Stracktrace output:", e);
        }
    }
}
