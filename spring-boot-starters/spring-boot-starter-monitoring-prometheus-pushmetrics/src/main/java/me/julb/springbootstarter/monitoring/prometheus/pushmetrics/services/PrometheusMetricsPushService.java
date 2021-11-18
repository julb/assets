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

package me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import me.julb.library.utility.validator.constraints.PrometheusMetricsInstanceName;
import me.julb.library.utility.validator.constraints.PrometheusMetricsJobName;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;

/**
 * The service to push metrics to Prometheus PushGateway.
 * <br>
 * @author Julb.
 */
public interface PrometheusMetricsPushService {

    /**
     * Push the given metrics for given job and instance.
     * @param job the job name.
     * @param instance the job instance.
     * @param metrics the metrics.
     */
    void push(@NotNull @NotBlank @PrometheusMetricsJobName String job, @NotNull @NotBlank @PrometheusMetricsInstanceName String instance, @NotNull @Valid MetricsCreationDTO metrics);

    /**
     * Push the given collection of metrics for given job and instance.
     * @param metricsWrapper the wrapper containing job, instance and collections.
     */
    void pushAll(@NotNull @Valid MetricsCreationWrapperDTO metricsWrapper);

    /**
     * Push the given collection of metrics for given job and instance.
     * @param job the job name.
     * @param instance the job instance.
     * @param metrics the collection of metrics.
     */
    void pushAll(@NotNull @NotBlank @PrometheusMetricsJobName String job, @NotNull @NotBlank @PrometheusMetricsInstanceName String instance, @NotNull @Valid @NotEmpty Collection<MetricsCreationDTO> metrics);

    /**
     * Remove all the metrics of given job and instance.
     * @param job the job name.
     * @param instance the job instance.
     */
    void removeAll(@NotNull @NotBlank @PrometheusMetricsJobName String job, @NotNull @NotBlank @PrometheusMetricsInstanceName String instance);

}
