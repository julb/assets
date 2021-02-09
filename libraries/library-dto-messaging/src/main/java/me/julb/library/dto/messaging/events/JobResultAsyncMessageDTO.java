/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package me.julb.library.dto.messaging.events;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import me.julb.library.utility.validator.constraints.DateTimeISO8601;

/**
 * DTO designed to describe a job result.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class JobResultAsyncMessageDTO<T> extends EventCollectorAsyncMessageDTO<T> {

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
    private String name;

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
    private String instance;

    //@formatter:off
     /**
     * The result attribute.
     * -- GETTER --
     * Getter for {@link #result} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #result} property.
     * @param result the value to set.
     */
     //@formatter:on
    @NotNull
    private JobResultStatus result;

    //@formatter:off
     /**
     * The completedAtDateTime attribute.
     * -- GETTER --
     * Getter for {@link #completedAtDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #completedAtDateTime} property.
     * @param completedAtDateTime the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @DateTimeISO8601
    private String completedAtDateTime;

    //@formatter:off
     /**
     * The durationInSeconds attribute.
     * -- GETTER --
     * Getter for {@link #durationInSeconds} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #durationInSeconds} property.
     * @param durationInSeconds the value to set.
     */
     //@formatter:on
    @NotNull
    @Min(0)
    private Long durationInSeconds;

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
    private Map<String, Number> metrics = new HashMap<>();

    /**
     * Constructor.
     */
    public JobResultAsyncMessageDTO() {
        super();
    }

    /**
     * Gets the value of a metric.
     * @param metricName the name of the metric.
     * @return the value.
     */
    public Number getMetric(String metricName) {
        return metrics.get(metricName);
    }
}
