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

import org.springframework.core.io.Resource;

/**
 * The configuration needed for a SSL connection.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class TlsKeystoreConfigurationProperties {

    //@formatter:off
     /**
     * The keystore format.
     * -- GETTER --
     * Getter for {@link #type} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #type} property.
     * @param type the value to set.
     */
     //@formatter:on
    private String type = "PKCS12";

    //@formatter:off
     /**
     * The keystore path.
     * -- GETTER --
     * Getter for {@link #path} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #path} property.
     * @param path the value to set.
     */
     //@formatter:on
    private Resource path;

    //@formatter:off
     /**
     * The alias attribute.
     * -- GETTER --
     * Getter for {@link #alias} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #alias} property.
     * @param alias the value to set.
     */
     //@formatter:on
    private String alias;

    //@formatter:off
     /**
     * The password attribute.
     * -- GETTER --
     * Getter for {@link #password} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #password} property.
     * @param password the value to set.
     */
     //@formatter:on
    private String password;

    /**
     * Returns <code>true</code> if the keystore configuration is enabled.
     * @return <code>true</code> if the keystore configuration is enabled, <code>false</code> otherwise.
     */
    public boolean isEnabled() {
        return this.path != null;
    }

}
