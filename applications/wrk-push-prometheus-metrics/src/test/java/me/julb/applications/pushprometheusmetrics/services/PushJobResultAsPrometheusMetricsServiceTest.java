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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import me.julb.library.dto.messaging.events.JobResultAsyncMessageDTO;
import me.julb.library.dto.messaging.events.JobResultStatus;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.messaging.builders.JobResultAsyncMessageBuilder;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;
import me.julb.springbootstarter.test.messaging.base.AbstractMessagingBaseTest;

/**
 * Unit test for class {@link AsyncPosterMessageService}.
 * <br>
 * @author Julb.
 */
public class PushJobResultAsPrometheusMetricsServiceTest extends AbstractMessagingBaseTest {

    /**
     * The service to push metrics.
     */
    @Autowired
    private PushJobResultAsPrometheusMetricsService pushJobResultAsPrometheusMetricsService;

    /**
     * The prometheusMetricsPushService mock.
     */
    @MockBean
    private PrometheusMetricsPushService prometheusMetricsPushService;

    /**
     * Unit test method.
     */
    @Test
    public void whenPushingJobResult_thenMetricsSent()
        throws Exception {
        //@formatter:off
        JobResultAsyncMessageDTO<Void> dto = new JobResultAsyncMessageBuilder<Void>()
            .name("job-name")
            .instance("job-instance")
            .metric("customMetric", 1)
            .result(JobResultStatus.SUCCESSFUL)
            .completedAtDateTime(DateUtility.dateTimeNow())
            .durationInSeconds(50L)
            .build();
        //@formatter:on

        pushJobResultAsPrometheusMetricsService.push(dto);

        // Verify.
        Mockito.verify(prometheusMetricsPushService).pushAll(Mockito.isA(MetricsCreationWrapperDTO.class));
    }
}
