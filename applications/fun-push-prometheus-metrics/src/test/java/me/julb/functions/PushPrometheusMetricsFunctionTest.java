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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.test.context.TestPropertySource;

import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricType;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsLabelCreationDTO;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The unit test class for the serverless function.
 * <br>
 * @author Julb.
 */
@TestPropertySource(properties = { "spring.sleuth.function.enabled=false" })
public class PushPrometheusMetricsFunctionTest extends AbstractBaseTest {

    /**
     * The service mocked.
     */
    @MockBean
    private PrometheusMetricsPushService prometheusMetricsPushService;

    /**
     * The function catalog.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunction_thenSendMetrics()
        throws Exception {
        Consumer<MetricsCreationWrapperDTO> function = functionCatalog.lookup("pushPrometheusMetricsFunction");

        MetricsCreationWrapperDTO dto = new MetricsCreationWrapperDTO();
        dto.setJob("some-job");
        dto.setInstance("some-instance");

        MetricsCreationDTO metrics = new MetricsCreationDTO();
        metrics.setName("custom_metric_with_labels");
        metrics.setHelp("A custom gauge with labels");
        metrics.setType(MetricType.GAUGE);
        metrics.setValue(1f);
        metrics.setAdditionalLabels(new HashSet<>());
        metrics.getAdditionalLabels().add(new MetricsLabelCreationDTO("billing", "123"));
        metrics.getAdditionalLabels().add(new MetricsLabelCreationDTO("env", "prod"));
        dto.setMetrics(new ArrayList<>());
        dto.getMetrics().add(metrics);
        function.accept(dto);

        Mockito.verify(prometheusMetricsPushService).pushAll(dto);

    }

}
