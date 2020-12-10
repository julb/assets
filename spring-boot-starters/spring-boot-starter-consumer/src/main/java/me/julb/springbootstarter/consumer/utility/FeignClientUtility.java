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
package me.julb.springbootstarter.consumer.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import feign.Client;
import feign.httpclient.ApacheHttpClient;
import me.julb.library.utility.constants.SSLSystemProperties;
import me.julb.library.utility.enums.HttpProtocol;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.configurations.properties.ProxyConfigurationProperties;
import me.julb.springbootstarter.consumer.configurations.properties.TlsKeystoreConfigurationProperties;
import me.julb.springbootstarter.consumer.configurations.properties.TlsTruststoreConfigurationProperties;

/**
 * A generic configuration class for a SSL client.
 * <P>
 * @author Julb.
 */
@Slf4j
public final class FeignClientUtility {

    /**
     * Builds a feign client instance.
     * @param endpointProperties the TLS configuration.
     * @return the feign client instance.
     */
    public static Client feignClientUtil(ConsumerEndpointProperties endpointProperties) {
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            HttpProtocol httpProtocol = getHttpProtocol(endpointProperties.getUrl());
            if (HttpProtocol.HTTPS.equals(httpProtocol)) {
                // TLS configuration
                TlsKeystoreConfigurationProperties keystore = endpointProperties.getTls().getKeystore();
                TlsTruststoreConfigurationProperties truststore = endpointProperties.getTls().getTruststore();
                SSLContextBuilder sslContextBuilder = SSLContexts.custom();

                if (endpointProperties.getTls().getInsecure()) {
                    TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
                    sslContextBuilder.loadTrustMaterial(null, acceptingTrustStrategy);
                } else {
                    // Keystore.
                    if (keystore.isEnabled()) {
                        KeyStore keyStore = KeyStore.getInstance(keystore.getType());
                        keyStore.load(keystore.getPath().getInputStream(), keystore.getPassword().toCharArray());
                        sslContextBuilder.loadKeyMaterial(keyStore, keystore.getPassword().toCharArray(), new PrivateKeyStrategy() {

                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
                                return keystore.getAlias();
                            }
                        });
                    }

                    // Truststore.
                    if (truststore.isEnabled()) {
                        String keystoreType = null;
                        InputStream truststoreInputStream = null;
                        String truststorePassword = null;

                        if (truststore.getUseSystem()) {
                            keystoreType = System.getProperty(SSLSystemProperties.JAVAX_NET_SSL_TRUSTSTORE_TYPE_PROPERTY, SSLSystemProperties.JAVAX_NET_SSL_TRUSTSTORE_TYPE_DEFAULT_VALUE);
                            String truststorePath = System.getProperty(SSLSystemProperties.JAVAX_NET_SSL_TRUSTSTORE_PATH_PROPERTY, null);
                            if (truststorePath != null) {
                                truststoreInputStream = new FileInputStream(new File(truststorePath));
                            }
                            truststorePassword = System.getProperty(SSLSystemProperties.JAVAX_NET_SSL_TRUSTSTORE_PASSWORD_PROPERTY, null);
                        } else {
                            keystoreType = truststore.getType();
                            truststoreInputStream = truststore.getPath().getInputStream();
                            truststorePassword = truststore.getPassword();
                        }
                        KeyStore truststoreKeystore = null;
                        if (SSLSystemProperties.JAVAX_NET_SSL_TRUSTSTORE_TYPE_X509_VALUE.equals(keystoreType)) {
                            truststoreKeystore = KeyStore.getInstance(SSLSystemProperties.JAVAX_NET_SSL_TRUSTSTORE_TYPE_DEFAULT_VALUE);
                            truststoreKeystore.load(null);
                            Collection<? extends Certificate> certificates = CertificateFactory.getInstance("X.509").generateCertificates(truststoreInputStream);
                            for (Certificate certificate : certificates) {
                                truststoreKeystore.setCertificateEntry(UUID.randomUUID().toString(), certificate);
                            }
                        } else {
                            truststoreKeystore = KeyStore.getInstance(keystoreType);
                            if (truststorePassword != null) {
                                truststoreKeystore.load(truststoreInputStream, truststorePassword.toCharArray());
                            } else {
                                truststoreKeystore.load(truststoreInputStream, null);
                            }
                        }
                        sslContextBuilder.loadTrustMaterial(truststoreKeystore, null);
                    }
                }
                SSLContext sslContext = sslContextBuilder.build();

                // Protocols & cipher suites.
                String[] supportedProtocols = endpointProperties.getTls().getSupportedProtocols();
                String[] supportedCipherSuites = endpointProperties.getTls().getSupportedCipherSuites();

                // Hostname validation.
                HostnameVerifier hostnameVerifier = null;
                if (endpointProperties.getTls().getDisableHostnameValidation() || endpointProperties.getTls().getInsecure()) {
                    hostnameVerifier = new NoopHostnameVerifier();
                } else {
                    hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
                }

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
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the HTTP protocol of this URL.
     * @param endpointProperties the TLS configuration.
     * @return the feign client instance.
     */
    private static HttpProtocol getHttpProtocol(String value) {
        try {
            if (StringUtils.isBlank(value)) {
                return null;
            }

            String protocol = new URL(value).getProtocol();
            for (HttpProtocol httpProtocol : HttpProtocol.values()) {
                if (StringUtils.equalsIgnoreCase(protocol, httpProtocol.protocol())) {
                    return httpProtocol;
                }
            }

            throw new UnsupportedOperationException();
        } catch (MalformedURLException e) {
            throw new UnsupportedOperationException(e);
        }
    }

}