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
package me.julb;

import java.util.HashSet;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeHint;

import me.julb.library.utility.validator.constraints.PrometheusMetricsInstanceName;
import me.julb.library.utility.validator.constraints.PrometheusMetricsJobName;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricHelp;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricLabelKey;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricLabelValue;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricName;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationWrapperDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsLabelCreationDTO;

/**
 * Main class to launch the application.
 * <br>
 * @author Julb.
 */
@NativeHint(
    jdkProxies = {
        @JdkProxyHint(types = {Pattern.class}),
        @JdkProxyHint(types = {Size.class})
    },
    types = @TypeHint(types = {
        PrometheusMetricsJobName.class,
        PrometheusMetricsInstanceName.class,
        PrometheusMetricsMetricName.class,
        PrometheusMetricsMetricHelp.class,
        PrometheusMetricsMetricLabelKey.class,
        PrometheusMetricsMetricLabelValue.class
    }),
    resources = @ResourceHint(patterns = "me/julb/springbootstarter/monitoring/prometheus/pushmetrics/default.properties"),
    serializables = @SerializationHint(types = {MetricsCreationWrapperDTO.class, MetricsCreationDTO.class, MetricsLabelCreationDTO.class, HashSet.class})
)
@SpringBootApplication
public class Application {

    /**
     * Method to launch the application.
     * @param args the arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
