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
package me.julb.springbootstarter.consumer.configurations.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * The properties to configure the endpoint for a consumer.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ConsumerEndpointProperties {

    //@formatter:off
     /**
     * The url attribute.
     * -- GETTER --
     * Getter for {@link #url} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #url} property.
     * @param url the value to set.
     */
     //@formatter:on
    private String url;

    //@formatter:off
     /**
     * The tls attribute.
     * -- GETTER --
     * Getter for {@link #tls} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tls} property.
     * @param tls the value to set.
     */
     //@formatter:on
    private TlsConfigurationProperties tls = new TlsConfigurationProperties();

    //@formatter:off
     /**
     * The proxy attribute.
     * -- GETTER --
     * Getter for {@link #proxy} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #proxy} property.
     * @param proxy the value to set.
     */
     //@formatter:on
    private ProxyConfigurationProperties proxy = new ProxyConfigurationProperties();

}
