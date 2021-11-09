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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

import me.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import me.julb.library.dto.messaging.events.JobResultStatus;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricType;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;

/**
 * The function to convert a job result async message to the metrics wrapper..
 * <br>
 * @author Julb.
 */
@Slf4j
public class JobResultAsyncMessageToMetricsCreationWrapperFunction implements Function<JobResultAsyncMessageDTO<?>, MetricsCreationWrapperDTO> {

    /**
     * The LAST_JOB_EXECUTION_RESULT_METRIC_NAME attribute.
     */
    private static final String LAST_JOB_EXECUTION_RESULT_METRIC_NAME = "lastJobExecutionSuccessful";

    /**
     * The LAST_JOB_EXECUTION_COMPLETED_AT_TIMESTAMP_METRIC_NAME attribute.
     */
    private static final String LAST_JOB_EXECUTION_COMPLETED_AT_TIMESTAMP_METRIC_NAME = "lastJobExecutionCompletedAtTimestamp";

    /**
     * The LAST_JOB_EXECUTION_DURATION_METRIC_NAME attribute.
     */
    private static final String LAST_JOB_EXECUTION_DURATION_IN_SECONDS_METRIC_NAME = "lastJobExecutionDurationInSeconds";

    /**
     * The gauges values by status.
     */
    //@formatter:off
    private final static EnumMap<JobResultStatus, Integer> GAUGE_VALUES_BY_JOB_RESULT_STATUS = new EnumMap<>(Map.of(
        JobResultStatus.SUCCESSFUL, 1,
        JobResultStatus.FAILURE, 0
    ));
    //@formatter:on

    /**
     * {@inheritDoc}
     */
    @Override
    public MetricsCreationWrapperDTO apply(JobResultAsyncMessageDTO<?> jobResultAsyncMessage) {
        // Create wrapper.
        MetricsCreationWrapperDTO wrapper = new MetricsCreationWrapperDTO();
        wrapper.setJob(jobResultAsyncMessage.getName());
        wrapper.setInstance(jobResultAsyncMessage.getInstance());
        wrapper.setMetrics(new HashSet<>());

        // Trace for debug.
        LOGGER.debug(" > Job name : {}.", wrapper.getJob());
        LOGGER.debug(" > Job instance : {}.", wrapper.getInstance());

        // Get job result metrics and add generic metrics.
        Map<String, Number> jobMetrics = new HashMap<>();
        jobMetrics.putAll(jobResultAsyncMessage.getMetrics());
        jobMetrics.put(LAST_JOB_EXECUTION_RESULT_METRIC_NAME, GAUGE_VALUES_BY_JOB_RESULT_STATUS.get(jobResultAsyncMessage.getResult()));
        jobMetrics.put(LAST_JOB_EXECUTION_COMPLETED_AT_TIMESTAMP_METRIC_NAME, DateUtility.epochSecond(jobResultAsyncMessage.getCompletedAtDateTime()));
        jobMetrics.put(LAST_JOB_EXECUTION_DURATION_IN_SECONDS_METRIC_NAME, jobResultAsyncMessage.getDurationInSeconds());

        // Build metrics
        for (Map.Entry<String, Number> jobMetric : jobMetrics.entrySet()) {
            MetricsCreationDTO metric = new MetricsCreationDTO();
            metric.setName(camelToSnake(jobMetric.getKey()));
            metric.setType(MetricType.GAUGE);
            metric.setValue(jobMetric.getValue().floatValue());
            wrapper.getMetrics().add(metric);

            // Trace for debug.
            LOGGER.debug(" >> Metric {} : {}.", metric.getName(), metric.getValue());
        }

        return wrapper;
    }

    /**
     * Converts a camelCase string to a snake_case string.
     * @param input the input.
     * @return the input in snake_case.
     */
    private static String camelToSnake(String input) {
        return input.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
