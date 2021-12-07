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

package me.julb.springbootstarter.monitoring.prometheus.pushmetrics.configurations.beans;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;

/**
 * The properties for Prometheus push metrics.
 * <br>
 *
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "prometheus.pushmetrics.pushgateway")
public class PrometheusPushMetricsProperties {
    
    //@formatter:off
    /**
    * The endpoint attribute.
    * -- GETTER --
    * Getter for {@link #endpoint} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #endpoint} property.
    * @param endpoint the value to set.
    */
    //@formatter:on
    @NotNull
   private ConsumerEndpointProperties endpoint;
}