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
package io.julb.springbootstarter.consumer.configurations.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * The configuration needed for a TLS connection.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ProxyConfigurationProperties {

    //@formatter:off
    /**
     * Check if the proxy is enabled or not
     * -- GETTER --
     * Getter for {@link #enabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #enabled} property.
     * @param enabled the value to set.
     */
     //@formatter:on
    private Boolean enabled = false;

    //@formatter:off
    /**
     * The proxy host.
     * -- GETTER --
     * Getter for {@link #host} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #host} property.
     * @param host the value to set.
     */
     //@formatter:on
    private String host;

    //@formatter:off
    /**
     * The proxy port.
     * -- GETTER --
     * Getter for {@link #port} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #port} property.
     * @param port the value to set.
     */
     //@formatter:on
    private Integer port;

    //@formatter:off
    /**
     * The proxy user
     * -- GETTER --
     * Getter for {@link #user} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #user} property.
     * @param user the value to set.
     */
     //@formatter:on
    private String user;

    //@formatter:off
    /**
     * The proxy password.
     * -- GETTER --
     * Getter for {@link #password} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #password} property.
     * @param password the value to set.
     */
    //@formatter:on
    private String password;

}
