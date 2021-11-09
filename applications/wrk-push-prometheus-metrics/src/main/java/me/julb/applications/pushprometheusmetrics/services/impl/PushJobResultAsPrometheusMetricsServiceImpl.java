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

package me.julb.applications.pushprometheusmetrics.services.impl;

import java.util.function.Function;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.pushprometheusmetrics.services.PushJobResultAsPrometheusMetricsService;
import me.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;

/**
 * A service that push job results to Prometheus as metrics.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
public class PushJobResultAsPrometheusMetricsServiceImpl implements PushJobResultAsPrometheusMetricsService {
    /**
     * The service to push metrics to prometheus.
     */
    @Autowired
    private PrometheusMetricsPushService prometheusMetricsPushService;

    /**
     * The mapper funnction.
     */
    private Function<JobResultAsyncMessageDTO<?>, MetricsCreationWrapperDTO> mapper = new JobResultAsyncMessageToMetricsCreationWrapperFunction();

    /**
     * {@inheritDoc}
     */
    @Override
    public void push(@NotNull @Valid JobResultAsyncMessageDTO<?> jobResult) {
        LOGGER.debug("Processing job result and converting it to prometheus metrics.");

        // Map job result to metrics wrapper.
        MetricsCreationWrapperDTO metricsWrapper = mapper.apply(jobResult);

        // Push metrics to prometheus.
        prometheusMetricsPushService.pushAll(metricsWrapper);
    }

}
