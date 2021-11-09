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

package me.julb.springbootstarter.monitoring.prometheus.pushmetrics.services.dto;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricHelp;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricLabelKey;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricLabelValue;
import me.julb.library.utility.validator.constraints.PrometheusMetricsMetricName;

/**
 * The DTO to define a metric.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
public class MetricsCreationDTO {

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @PrometheusMetricsMetricName
    private String name;

    //@formatter:off
     /**
     * The help attribute.
     * -- GETTER --
     * Getter for {@link #help} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #help} property.
     * @param help the value to set.
     */
     //@formatter:on
    @PrometheusMetricsMetricHelp
    private String help;

    //@formatter:off
     /**
     * The type attribute.
     * -- GETTER --
     * Getter for {@link #type} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #type} property.
     * @param type the value to set.
     */
     //@formatter:on
    @NotNull
    private MetricType type;

    //@formatter:off
     /**
     * The value attribute.
     * -- GETTER --
     * Getter for {@link #value} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #value} property.
     * @param value the value to set.
     */
     //@formatter:on
    @NotNull
    private Float value;

    //@formatter:off
     /**
     * The additionalLabels attribute.
     * -- GETTER --
     * Getter for {@link #additionalLabels} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #additionalLabels} property.
     * @param additionalLabels the value to set.
     */
     //@formatter:on
    private Map<@PrometheusMetricsMetricLabelKey String, @PrometheusMetricsMetricLabelValue String> additionalLabels = new HashMap<>();

}
