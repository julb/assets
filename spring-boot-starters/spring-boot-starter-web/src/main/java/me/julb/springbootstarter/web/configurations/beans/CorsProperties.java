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
package me.julb.springbootstarter.web.configurations.beans;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The CORS properties configuration.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "web.cors")
public class CorsProperties {

    //@formatter:off
    /**
     * Comma-separated list of origins to allow. '*' allows all origins. When not set, CORS support is disabled.
     * -- GETTER --
     * Getter for {@link #allowedOrigins} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #allowedOrigins} property.
     * @param allowedOrigins the value to set.
     */
     //@formatter:on
    private List<String> allowedOrigins = new ArrayList<>();

    //@formatter:off
    /**
     * Comma-separated list of methods to allow. '*' allows all methods. When not set, defaults to GET.
     * -- GETTER --
     * Getter for {@link #allowedMethods} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #allowedMethods} property.
     * @param allowedMethods the value to set.
     */
     //@formatter:on
    private List<String> allowedMethods = new ArrayList<>();

    //@formatter:off
     /**
     * Comma-separated list of headers to allow in a request. '*' allows all headers.
     * -- GETTER --
     * Getter for {@link #allowedHeaders} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #allowedHeaders} property.
     * @param allowedHeaders the value to set.
     */
     //@formatter:on
    private List<String> allowedHeaders = new ArrayList<>();

    //@formatter:off
     /**
     * Comma-separated list of headers to include in a response.
     * -- GETTER --
     * Getter for {@link #exposedHeaders} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #exposedHeaders} property.
     * @param exposedHeaders the value to set.
     */
     //@formatter:on
    private List<String> exposedHeaders = new ArrayList<>();

    //@formatter:off
     /**
     * Set whether credentials are supported. When not set, credentials are not supported.
     * -- GETTER --
     * Getter for {@link #allowCredentials} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #allowCredentials} property.
     * @param allowCredentials the value to set.
     */
     //@formatter:on
    private boolean allowCredentials;

    //@formatter:off
     /**
     * How long, in seconds, the response from a pre-flight request can be cached by clients.
     * -- GETTER --
     * Getter for {@link #maxAge} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #maxAge} property.
     * @param maxAge the value to set.
     */
     //@formatter:on
    private Long maxAge = 1800L;

    // ------------------------------------------ Class methods.
}
