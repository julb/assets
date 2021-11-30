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

package me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.library.utility.validator.constraints.PrometheusMetricsInstanceName;
import me.julb.library.utility.validator.constraints.PrometheusMetricsJobName;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.annotations.ConditionalOnPrometheusPushMetricsEnabled;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsLabelCreationDTO;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.PushGateway;

/**
 * The push metrics service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
@ConditionalOnPrometheusPushMetricsEnabled
public class PrometheusMetricsPushServiceImpl implements PrometheusMetricsPushService {

    /**
     * The INSTANCE_GROUPING_KEY_NAME attribute.
     */
    private static final String INSTANCE_GROUPING_KEY_NAME = "instance";

    /**
     * The feign client to send data to Prometheus Push gateway.
     */
    @Autowired
    private PushGateway prometheusPushGatewayClientBean;

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public void push(@NotNull @NotBlank @PrometheusMetricsJobName String job, @NotNull @NotBlank @PrometheusMetricsInstanceName String instance, @NotNull @Valid MetricsCreationDTO metrics) {
        pushAll(job, instance, Arrays.asList(metrics));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushAll(@NotNull @Valid MetricsCreationWrapperDTO metricsWrapper) {
        pushAll(metricsWrapper.getJob(), metricsWrapper.getInstance(), metricsWrapper.getMetrics());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushAll(@NotNull @NotBlank @PrometheusMetricsJobName String job, @NotNull @NotBlank @PrometheusMetricsInstanceName String instance, @NotNull @Valid @NotEmpty Collection<MetricsCreationDTO> metrics) {
        // Trace input.
        LOGGER.debug("Adding metrics to job <{}> and instance <{}>.", job, instance);

        try {
            // Create a registry to collect all metric.            
            var collectorRegistry = new CollectorRegistry();

            for(MetricsCreationDTO metric : metrics) {
                // Get label names and values.
                List<String> labelNames = new ArrayList<>();
                List<String> labelValues  = new ArrayList<>();
                
                if(CollectionUtils.isNotEmpty(metric.getAdditionalLabels())) {
                    for(MetricsLabelCreationDTO additionalLabel : metric.getAdditionalLabels()) {
                        labelNames.add(additionalLabel.getKey());
                        labelValues.add(additionalLabel.getValue());
                    }
                }
                
                // Register the custom metric in the registry.
                switch (metric.getType()) {
                    case GAUGE:
                        Gauge.build(metric.getName(), metric.getHelp())
                            .labelNames(labelNames.toArray(new String[0]))
                            .register(collectorRegistry)
                            .labels(labelValues.toArray(new String[0]))
                            .set(metric.getValue());
                        break;
                    case COUNTER:
                        Counter.build(metric.getName(), metric.getHelp())
                            .labelNames(labelNames.toArray(new String[0]))
                            .register(collectorRegistry)
                            .labels(labelValues.toArray(new String[0]))
                            .inc(metric.getValue());
                        break;
                    case SUMMARY:
                        Summary.build(metric.getName(), metric.getHelp())
                            .labelNames(labelNames.toArray(new String[0]))
                            .register(collectorRegistry)
                            .labels(labelValues.toArray(new String[0]))
                            .observe(metric.getValue());
                        break;
                    case HISTOGRAM:
                        Histogram.build(metric.getName(), metric.getHelp())
                            .labelNames(labelNames.toArray(new String[0]))
                            .register(collectorRegistry)
                            .labels(labelValues.toArray(new String[0]))
                            .observe(metric.getValue());
                        break;
                }
            }
            
            // Push the metrics.
            prometheusPushGatewayClientBean.pushAdd(collectorRegistry, job, Map.of(INSTANCE_GROUPING_KEY_NAME, instance));
        } catch(IOException e) {
            LOGGER.error("Unable to push metric for job <{}> and instance <{}>.", job, instance, e);
            throw new InternalServerErrorException(e);
        }

        // Trace successful.
        LOGGER.info("Metrics for job <{}> and instance <{}> created successfully.", job, instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAll(@NotNull @NotBlank @PrometheusMetricsJobName String job, @NotNull @NotBlank @PrometheusMetricsInstanceName String instance) {
        // Trace input.
        LOGGER.debug("Removing all metrics from job <{}> and instance <{}>.", job, instance);

        try {
            // Delete metrics
            prometheusPushGatewayClientBean.delete(job, Map.of(INSTANCE_GROUPING_KEY_NAME, instance));

            // Trace successful.
            LOGGER.info("Metrics for job <{}> and instance <{}> removed successfully.", job, instance);
        } catch(IOException e) {
            LOGGER.error("Unable to delete metric for job <{}> and instance <{}>.", job, instance, e);
            throw new InternalServerErrorException(e);
        }
    }
}
