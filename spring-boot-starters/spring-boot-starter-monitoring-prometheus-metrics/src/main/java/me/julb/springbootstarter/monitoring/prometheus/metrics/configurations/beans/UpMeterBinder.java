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

package me.julb.springbootstarter.monitoring.prometheus.metrics.configurations.beans;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.stereotype.Component;

/**
 * The up meter binder.
 * <br>
 * @author Julb.
 */
@Component
public class UpMeterBinder implements MeterBinder {

    /**
     * The metrics name.
     */
    private static final String UP_METRICS_NAME = "up";

    /**
     * The gauges values by status.
     */
    //@formatter:off
    private final static Map<String, Integer> GAUGE_VALUES_BY_STATUS = Map.<String, Integer> of(
        Status.UP.getCode(), 1,
        Status.DOWN.getCode(), 0,
        Status.OUT_OF_SERVICE.getCode(), -1,
        Status.UNKNOWN.getCode(), -2);
    //@formatter:on

    /**
     * The status aggregator.
     */
    @Autowired
    private StatusAggregator statusAggregator;

    /**
     * The health indicators.
     */
    @Autowired
    private List<HealthIndicator> healthIndicators;

    /**
     * The composite health indicator.
     */
    private CompositeHealthContributor compositeHealthContributor;

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindTo(MeterRegistry registry) {
        // Register all health indicators in the composite contributor.
        Map<String, HealthIndicator> map = new HashMap<>();
        for (Integer i = 0; i < healthIndicators.size(); i++) {
            map.put(i.toString(), healthIndicators.get(i));
        }
        compositeHealthContributor = CompositeHealthContributor.fromMap(map);

        // Put a gauge named "health" in the registry.
        registry.gauge(UP_METRICS_NAME, new ArrayList<>(), compositeHealthContributor, health -> {
            return convertStatusToMetrics(statusAggregator, health);
        });
    }

    /**
     * Converts health status to a metric.
     * @param statusAggregator the status aggregator.
     * @param health the composite health contributor.
     * @return the metrics value.
     */
    private double convertStatusToMetrics(StatusAggregator statusAggregator, CompositeHealthContributor health) {
        Set<Status> statuses = health.stream().map((namedContributor -> {
            return ((HealthIndicator) namedContributor.getContributor()).health().getStatus();
        })).collect(Collectors.toSet());
        Status status = statusAggregator.getAggregateStatus(statuses);
        return GAUGE_VALUES_BY_STATUS.get(status.getCode()).doubleValue();
    }
}
