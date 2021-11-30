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

package me.julb.springbootstarter.consumer.utility;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;

import me.julb.library.utility.enums.HttpProtocol;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.configurations.properties.ProxyConfigurationProperties;

/**
 * A utility class to build {@link java.net.HttpURLConnection} from {@link ConsumerEndpointProperties}.
 * <br>
 *
 * @author Julb.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUrlConnectionUtility {

    /**
     * Builds a {@link HttpURLConnection} from the given {@link ConsumerEndpointProperties}.
     * @param endpointProperties the endpoint properties.
     * @return the HttpURLConnection to connect to this endpoint.
     */
    public static HttpURLConnection build(ConsumerEndpointProperties endpointProperties) {
        return buildForUrl(endpointProperties.getUrl(), endpointProperties);
    }

    /**
     * Builds a {@link HttpURLConnection} from the given {@link ConsumerEndpointProperties}.
     * @param url the URL to create the connection to.
     * @param endpointProperties the endpoint properties.
     * @return the HttpURLConnection to connect to this endpoint.
     */
    public static HttpURLConnection buildForUrl(String url, ConsumerEndpointProperties endpointProperties) {
        try {
            HttpURLConnection connection = null;

            HttpProtocol httpProtocol = ConsumerEndpointUtility.buildHttpProtocol(endpointProperties);
            if (HttpProtocol.HTTPS.equals(httpProtocol)) {
                SSLContext sslContext = ConsumerEndpointUtility.buildSSLContext(endpointProperties);

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
                httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());

                // Protocols & cipher suites.
                String[] supportedProtocols = endpointProperties.getTls().getSupportedProtocols();
                String[] supportedCipherSuites = endpointProperties.getTls().getSupportedCipherSuites();
                if (ArrayUtils.isNotEmpty(supportedProtocols) || ArrayUtils.isNotEmpty(supportedCipherSuites)) {
                    throw new UnsupportedOperationException("tls.supportedProtocols, tls.supportedCipherSuites and proxy properties are not supported with this implementation.");
                }

                // Hostname validation.
                HostnameVerifier hostnameVerifier = ConsumerEndpointUtility.buildHostnameVerifier(endpointProperties);
                httpsURLConnection.setHostnameVerifier(hostnameVerifier);

                // Switch it back to main value.
                connection = httpsURLConnection;
            } else {
                connection = (HttpURLConnection) new URL(url).openConnection();
            }

            // Proxy
            ProxyConfigurationProperties proxy = endpointProperties.getProxy();
            if (proxy.getEnabled()) {
                throw new UnsupportedOperationException("proxy is not supported with this implementation.");
            }

            return connection;
        } catch (Exception e) {
            LOGGER.error("Unable to create HTTP client.", e);
            throw new InternalServerErrorException(e);
        }
    }
}
