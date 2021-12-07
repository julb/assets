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

package me.julb.springbootstarter.consumer.reactive.utility;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import me.julb.library.utility.enums.HttpProtocol;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.configurations.properties.ProxyConfigurationProperties;
import me.julb.springbootstarter.consumer.configurations.properties.TlsKeystoreConfigurationProperties;
import me.julb.springbootstarter.consumer.configurations.properties.TlsTruststoreConfigurationProperties;
import me.julb.springbootstarter.consumer.utility.ConsumerEndpointUtility;

import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

/**
 * This class builds a Netty HTTP client from a {@link ConsumerEndpointProperties}.
 * <br>
 *
 * @author Julb.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NettyClientUtility {

    /**
     * Builds a Netty {@link HttpClient} from a {@link ConsumerEndpointProperties}.
     * @param endpointProperties the endpoint properties.
     * @return the http client.
     */
    public static HttpClient build(ConsumerEndpointProperties endpointProperties) {
        try {
            HttpClient httpClient = HttpClient.create();

            HttpProtocol httpProtocol = ConsumerEndpointUtility.buildHttpProtocol(endpointProperties);
            if (HttpProtocol.HTTPS.equals(httpProtocol)) {
                httpClient.secure(sslContextSpec -> {
                    try {
                        TlsKeystoreConfigurationProperties keystore = endpointProperties.getTls().getKeystore();
                        TlsTruststoreConfigurationProperties truststore = endpointProperties.getTls().getTruststore();
                        SslContextBuilder builder =  SslContextBuilder.forClient();

                        if (BooleanUtils.isTrue(endpointProperties.getTls().getInsecure())) {
                            builder.trustManager(ConsumerEndpointUtility.buildInsecureTrustManager());
                        } else {
                            // Keystore.
                            if (keystore.isEnabled()) {
                                builder.keyManager(ConsumerEndpointUtility.buildKeyManagerFactory(endpointProperties));
                            }
            
                            // Truststore.
                            if (truststore.isEnabled()) {
                                builder.trustManager(ConsumerEndpointUtility.buildTrustManagerFactory(endpointProperties));
                            }
                        }

                        // TLS protocols
                        if (ArrayUtils.isNotEmpty(endpointProperties.getTls().getSupportedProtocols())) {
                            builder.protocols(endpointProperties.getTls().getSupportedProtocols());
                        }

                        // TLS cipher suites
                        if (ArrayUtils.isNotEmpty(endpointProperties.getTls().getSupportedCipherSuites())) {
                            builder.ciphers(Arrays.asList(endpointProperties.getTls().getSupportedCipherSuites()));
                        }
                        
                        if (endpointProperties.getTls().getDisableHostnameValidation() || endpointProperties.getTls().getInsecure()) {
                            sslContextSpec.sslContext(builder.build()).handlerConfigurator(sslHandler -> {
                                SSLEngine engine = sslHandler.engine();
                                SSLParameters params = new SSLParameters();
                                List<SNIMatcher> matchers = new LinkedList<>();
                                SNIMatcher matcher = new SNIMatcher(0) {

                                    @Override
                                    public boolean matches(SNIServerName serverName) {
                                        return true;
                                    }
                                };
                                matchers.add(matcher);
                                params.setSNIMatchers(matchers);
                                engine.setSSLParameters(params);
                            });
                        } else {
                            sslContextSpec.sslContext(builder.build());
                        }

                    } catch (Exception e) {
                        throw new InternalServerErrorException(e);
                    }
                });
            } else {
                httpClient.noSSL();
            }

            // Proxy
            ProxyConfigurationProperties proxy = endpointProperties.getProxy();
            if (BooleanUtils.isTrue(proxy.getEnabled())) {
                httpClient.proxy(typeSpec -> {
                    ProxyProvider.Builder builder = typeSpec
                        .type(ProxyProvider.Proxy.HTTP)
                            .host(proxy.getHost())
                            .port(proxy.getPort());
                    if (StringUtils.isNotBlank(proxy.getUser()) && StringUtils.isNotBlank(proxy.getPassword())) {
                        builder.username(proxy.getUser())
                            .password(userName -> proxy.getPassword());
                    }
                });
            } else {
                httpClient.noProxy();
            }

            return httpClient;
        } catch (Exception e) {
            LOGGER.error("Unable to create Netty client.", e);
            throw new InternalServerErrorException(e);
        }
    }
}
