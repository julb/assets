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
package me.julb.library.utility.constants;

/**
 * The SSL system properties.
 * <br>
 * @author Julb.
 */
public class SSLSystemProperties {

    /**
     * The property name for truststore type.
     */
    public static final String JAVAX_NET_SSL_TRUSTSTORE_TYPE_PROPERTY = "javax.net.ssl.trustStoreType";

    /**
     * The default value for truststore type.
     */
    public static final String JAVAX_NET_SSL_TRUSTSTORE_TYPE_DEFAULT_VALUE = "JKS";

    /**
     * The X509 value for truststore type.
     */
    public static final String JAVAX_NET_SSL_TRUSTSTORE_TYPE_X509_VALUE = "X.509";

    /**
     * The property name for truststore path.
     */
    public static final String JAVAX_NET_SSL_TRUSTSTORE_PATH_PROPERTY = "javax.net.ssl.trustStore";

    /**
     * The property name for truststore password.
     */
    public static final String JAVAX_NET_SSL_TRUSTSTORE_PASSWORD_PROPERTY = "javax.net.ssl.trustStorePassword";

    /**
     * The property name for keystore type.
     */
    public static final String SERVER_SSL_KEYSTORE_TYPE_PROPERTY = "server.ssl.key-store-type";

    /**
     * The default value for keystore type.
     */
    public static final String SERVER_SSL_KEYSTORE_TYPE_DEFAULT_VALUE = "JKS";

    /**
     * The property name for keystore path.
     */
    public static final String SERVER_SSL_KEYSTORE_PATH_PROPERTY = "server.ssl.key-store";

    /**
     * The property name for keystore password.
     */
    public static final String SERVER_SSL_KEYSTORE_PASSWORD_PROPERTY = "server.ssl.key-store-password";

    /**
     * The property name for key alias.
     */
    public static final String SERVER_SSL_KEY_ALIAS_PROPERTY = "server.ssl.key-alias";

    /**
     * The property name for key password.
     */
    public static final String SERVER_SSL_KEY_PASSWORD_PROPERTY = "server.ssl.key-password";

    /**
     * The property name for protocol.
     */
    public static final String SERVER_SSL_PROTOCOL = "server.ssl.protocol";

    /**
     * The default protocol value
     */
    public static final String SERVER_SSL_PROTOCOL_DEFAULT_VALUE = "TLS";

    /**
     * The property name for enabled protocols.
     */
    public static final String SERVER_SSL_ENABLED_PROTOCOLS = "server.ssl.enabled-protocols";
}
