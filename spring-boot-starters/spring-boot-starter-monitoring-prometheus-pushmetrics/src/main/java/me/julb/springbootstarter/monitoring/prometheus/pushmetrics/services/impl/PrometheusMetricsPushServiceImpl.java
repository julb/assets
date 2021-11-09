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

import java.util.Arrays;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import me.julb.library.utility.validator.constraints.PrometheusMetricsInstanceName;
import me.julb.library.utility.validator.constraints.PrometheusMetricsJobName;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.annotations.ConditionalOnPrometheusPushMetricsEnabled;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.consumers.PrometheusPushGatewayFeignClient;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;

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
     * The PROMETHEUS_PUSHMETRICS_PAYLOAD_TEMPLATE_NAME attribute.
     */
    private static final String PROMETHEUS_PUSHMETRICS_PAYLOAD_TEMPLATE_NAME = "prometheus-pushmetrics-payload";

    /**
     * The METRICS_VARIABLE_NAME attribute.
     */
    private static final String METRICS_VARIABLE_NAME = "metrics";

    /**
     * The feign client to send data to Prometheus Push gateway.
     */
    @Autowired
    private PrometheusPushGatewayFeignClient prometheusPushGatewayFeignClient;

    /**
     * The template engine.
     */
    @Autowired
    private TemplateEngine prometheusPushmetricTemplateEngine;

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

        // Render template.
        Context ctx = new Context();
        ctx.setVariable(METRICS_VARIABLE_NAME, metrics);
        String prometheusMetricsPayload = this.prometheusPushmetricTemplateEngine.process(PROMETHEUS_PUSHMETRICS_PAYLOAD_TEMPLATE_NAME, ctx);
        prometheusPushGatewayFeignClient.create(job, instance, prometheusMetricsPayload);

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

        // Delete metrics
        prometheusPushGatewayFeignClient.delete(job, instance);

        // Trace successful.
        LOGGER.info("Metrics for job <{}> and instance <{}> removed successfully.", job, instance);
    }
}
