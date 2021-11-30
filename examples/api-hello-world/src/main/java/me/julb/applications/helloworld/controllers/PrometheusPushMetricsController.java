package me.julb.applications.helloworld.controllers;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.PrometheusMetricsPushService;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricType;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsCreationDTO;
import me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto.MetricsLabelCreationDTO;

/**
 * The captcha controller.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/prometheus/push", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrometheusPushMetricsController {

    /**
     * The prometheusPushMetricsService attribute.
     */
    @Autowired
    private PrometheusMetricsPushService prometheusPushMetricsService;

    // ------------------------------------------ Read methods.

    /**
     * Performs a call.
     */
    @GetMapping
    @Operation(summary = "Metrics push")
    public void test() {
        MetricsCreationDTO metrics = new MetricsCreationDTO();
        metrics.setName("custom_metric_with_labels");
        metrics.setHelp("A custom gauge with labels");
        metrics.setType(MetricType.GAUGE);
        metrics.setValue(1f);
        metrics.getAdditionalLabels().add(new MetricsLabelCreationDTO("billing", "123"));
        metrics.getAdditionalLabels().add(new MetricsLabelCreationDTO("env", "prod"));

        MetricsCreationDTO metrics2 = new MetricsCreationDTO();
        metrics2.setName("custom_metric_without_labels");
        metrics2.setHelp("A custom counter without labels");
        metrics2.setType(MetricType.COUNTER);
        metrics2.setValue(2f);

        // Push these metrics.
        String jobName = "job-test";
        String instanceName = "job-instance-abcdef";
        prometheusPushMetricsService.removeAll(jobName, instanceName);
        prometheusPushMetricsService.pushAll(jobName, instanceName, Arrays.asList(metrics, metrics2));
    }

    // ------------------------------------------ Write methods.
}
