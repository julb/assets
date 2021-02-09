/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.PrometheusMetricsInstanceName;
import me.julb.library.utility.validator.constraints.PrometheusMetricsJobName;

/**
 * The DTO to push metrics through a serverless function.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class MetricsCreationWrapperDTO {
    //@formatter:off
     /**
     * The job attribute.
     * -- GETTER --
     * Getter for {@link #job} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #job} property.
     * @param job the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @PrometheusMetricsJobName
    private String job;

    //@formatter:off
     /**
     * The instance attribute.
     * -- GETTER --
     * Getter for {@link #instance} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #instance} property.
     * @param instance the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @PrometheusMetricsInstanceName
    private String instance;

    //@formatter:off
     /**
     * The metrics attribute.
     * -- GETTER --
     * Getter for {@link #metrics} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #metrics} property.
     * @param metrics the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    @NotEmpty
    private Collection<MetricsCreationDTO> metrics;
}
