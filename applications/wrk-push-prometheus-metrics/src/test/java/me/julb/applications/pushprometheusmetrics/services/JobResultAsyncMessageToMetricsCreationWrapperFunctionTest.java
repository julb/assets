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

package me.julb.applications.pushprometheusmetrics.services;

import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.julb.applications.pushprometheusmetrics.services.impl.JobResultAsyncMessageToMetricsCreationWrapperFunction;
import me.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import me.julb.library.dto.messaging.events.JobResultStatus;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.messaging.builders.JobResultAsyncMessageBuilder;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;

/**
 * Unit test class for {@link JobResultAsyncMessageToMetricsCreationWrapperFunction}.
 * <br>
 * @author Julb.
 */
public class JobResultAsyncMessageToMetricsCreationWrapperFunctionTest {

    /**
     * The mapper funnction.
     */
    private Function<JobResultAsyncMessageDTO<?>, MetricsCreationWrapperDTO> mapper;

    /**
     * Creates the instance.
     */
    @BeforeEach
    public void setUp() {
        mapper = new JobResultAsyncMessageToMetricsCreationWrapperFunction();
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenMappingJobResultSuccessful_thenReturnMetrics()
        throws Exception {
        //@formatter:off
        JobResultAsyncMessageDTO<Void> dto = new JobResultAsyncMessageBuilder<Void>()
            .name("job-name")
            .instance("job-instance")
            .result(JobResultStatus.SUCCESSFUL)
            .completedAtDateTime(DateUtility.dateTimeNow())
            .durationInSeconds(50L)
            .metric("customMetric", 1)
            .build();
        //@formatter:on

        MetricsCreationWrapperDTO result = mapper.apply(dto);

        // Perform the tests.
        Assertions.assertEquals(dto.getName(), result.getJob());
        Assertions.assertEquals(dto.getInstance(), result.getInstance());
        Assertions.assertEquals(1f, result.getMetric("last_job_execution_successful").getValue());
        Assertions.assertEquals(DateUtility.epochSecond(dto.getCompletedAtDateTime()).floatValue(), result.getMetric("last_job_execution_completed_at_timestamp").getValue());
        Assertions.assertEquals(dto.getDurationInSeconds().floatValue(), result.getMetric("last_job_execution_duration_in_seconds").getValue());
        Assertions.assertEquals(dto.getMetric("customMetric").floatValue(), result.getMetric("custom_metric").getValue());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenMappingJobResultFailure_thenReturnMetrics()
        throws Exception {
        //@formatter:off
        JobResultAsyncMessageDTO<Void> dto = new JobResultAsyncMessageBuilder<Void>()
            .name("job-name")
            .instance("job-instance")
            .result(JobResultStatus.FAILURE)
            .completedAtDateTime(DateUtility.dateTimeNow())
            .durationInSeconds(50L)
            .metric("customMetric", 1)
            .build();
        //@formatter:on

        MetricsCreationWrapperDTO result = mapper.apply(dto);

        // Perform the tests.
        Assertions.assertEquals(dto.getName(), result.getJob());
        Assertions.assertEquals(dto.getInstance(), result.getInstance());
        Assertions.assertEquals(0f, result.getMetric("last_job_execution_successful").getValue());
        Assertions.assertEquals(DateUtility.epochSecond(dto.getCompletedAtDateTime()).floatValue(), result.getMetric("last_job_execution_completed_at_timestamp").getValue());
        Assertions.assertEquals(dto.getDurationInSeconds().floatValue(), result.getMetric("last_job_execution_duration_in_seconds").getValue());
        Assertions.assertEquals(dto.getMetric("customMetric").floatValue(), result.getMetric("custom_metric").getValue());
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenMappingJobResultWithoutMetrics_thenReturnMetrics()
        throws Exception {
        //@formatter:off
        JobResultAsyncMessageDTO<Void> dto = new JobResultAsyncMessageBuilder<Void>()
            .name("job-name")
            .instance("job-instance")
            .result(JobResultStatus.SUCCESSFUL)
            .completedAtDateTime(DateUtility.dateTimeNow())
            .durationInSeconds(50L)
            .build();
        //@formatter:on

        MetricsCreationWrapperDTO result = mapper.apply(dto);

        // Perform the tests.
        Assertions.assertEquals(dto.getName(), result.getJob());
        Assertions.assertEquals(dto.getInstance(), result.getInstance());
        Assertions.assertEquals(1f, result.getMetric("last_job_execution_successful").getValue());
        Assertions.assertEquals(DateUtility.epochSecond(dto.getCompletedAtDateTime()).floatValue(), result.getMetric("last_job_execution_completed_at_timestamp").getValue());
        Assertions.assertEquals(dto.getDurationInSeconds().floatValue(), result.getMetric("last_job_execution_duration_in_seconds").getValue());
        Assertions.assertEquals(3, result.getMetrics().size());
    }
}
