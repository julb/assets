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
package me.julb.springbootstarter.consumer.feign.utility;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import me.julb.library.utility.enums.HttpProtocol;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.configurations.properties.ProxyConfigurationProperties;
import me.julb.springbootstarter.consumer.utility.ConsumerEndpointUtility;

import feign.Client;
import feign.httpclient.ApacheHttpClient;

/**
 * A generic configuration class for a SSL client.
 * <br>
 * @author Julb.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeignClientUtility {

    /**
     * Builds a feign client instance.
     * @param endpointProperties the TLS configuration.
     * @return the feign client instance.
     */
    public static Client feignClientUtil(ConsumerEndpointProperties endpointProperties) {
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            HttpProtocol httpProtocol = ConsumerEndpointUtility.buildHttpProtocol(endpointProperties);
            if (HttpProtocol.HTTPS.equals(httpProtocol)) {
                // TLS configuration
                SSLContext sslContext = ConsumerEndpointUtility.buildSSLContext(endpointProperties);

                // Protocols & cipher suites.
                String[] supportedProtocols = endpointProperties.getTls().getSupportedProtocols();
                String[] supportedCipherSuites = endpointProperties.getTls().getSupportedCipherSuites();

                // Hostname validation.
                HostnameVerifier hostnameVerifier = ConsumerEndpointUtility.buildHostnameVerifier(endpointProperties);

                // Builds a SSLSocketFactory.
                httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, supportedProtocols, supportedCipherSuites, hostnameVerifier));
            }

            // Disable cookie
            httpClientBuilder.disableCookieManagement();

            // Proxy
            ProxyConfigurationProperties proxy = endpointProperties.getProxy();
            if (proxy.getEnabled()) {
                HttpHost proxyHttpHost = new HttpHost(proxy.getHost(), proxy.getPort());
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHttpHost);
                httpClientBuilder.setRoutePlanner(routePlanner);

                if (StringUtils.isNotBlank(proxy.getUser()) && StringUtils.isNotBlank(proxy.getPassword())) {
                    AuthScope authscope = new AuthScope(proxyHttpHost);
                    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword());
                    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(authscope, credentials);
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            }

            return new ApacheHttpClient(httpClientBuilder.build());
        } catch (Exception e) {
            LOGGER.error("Unable to create Feign client.", e);
            throw new InternalServerErrorException(e);
        }
    }
}