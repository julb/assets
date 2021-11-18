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
package me.julb.springbootstarter.consumer.configurations.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * The configuration needed for a TLS connection.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class TlsConfigurationProperties {

    //@formatter:off
    /**
     * Disable all TLS checks.
     * -- GETTER --
     * Getter for {@link #insecure} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #insecure} property.
     * @param insecure the value to set.
     */
     //@formatter:on
    private Boolean insecure = false;

    //@formatter:off
     /**
     * Disables the hostname validation.
     * -- GETTER --
     * Getter for {@link #disableHostnameValidation} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #disableHostnameValidation} property.
     * @param disableHostnameValidation the value to set.
     */
     //@formatter:on
    private Boolean disableHostnameValidation = false;

    //@formatter:off
     /**
     * The supported protocols.
     * -- GETTER --
     * Getter for {@link #supportedProtocols} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #supportedProtocols} property.
     * @param supportedProtocols the value to set.
     */
     //@formatter:on
    private String[] supportedProtocols = new String[] {"TLSv1.1", "TLSv1.2"};

    //@formatter:off
     /**
     * The supported cipher suites.
     * -- GETTER --
     * Getter for {@link #supportedCipherSuites} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #supportedCipherSuites} property.
     * @param supportedCipherSuites the value to set.
     */
     //@formatter:on
    private String[] supportedCipherSuites = null;

    //@formatter:off
     /**
     * The truststore configuration.
     * -- GETTER --
     * Getter for {@link #truststore} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #truststore} property.
     * @param truststore the value to set.
     */
     //@formatter:on
    private TlsTruststoreConfigurationProperties truststore = new TlsTruststoreConfigurationProperties();

    //@formatter:off
     /**
     * The keystore configuration.
     * -- GETTER --
     * Getter for {@link #keystore} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #keystore} property.
     * @param keystore the value to set.
     */
     //@formatter:on
    private TlsKeystoreConfigurationProperties keystore = new TlsKeystoreConfigurationProperties();

}
