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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import me.julb.library.utility.constants.SSLSystemProperties;
import me.julb.library.utility.enums.HttpProtocol;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.configurations.properties.TlsKeystoreConfigurationProperties;
import me.julb.springbootstarter.consumer.configurations.properties.TlsTruststoreConfigurationProperties;

/**
 * A utility class to build elements used to deal with a {@link ConsumerEndpointProperties}.
 * <br>
 *
 * @author Julb.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsumerEndpointUtility {
    
    /**
     * Builds a SSL context from the consumer endpoint properties.
     * @param endpointProperties the endpoint properties.
     * @return a SSL context.
     */
    public static SSLContext buildSSLContext(ConsumerEndpointProperties endpointProperties) {
        try {
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

            return sslContextBuilder.build();
        } catch (Exception e) {
            LOGGER.error("Unable to create Feign client.", e);
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Gets the {@link HostnameVerifier} to use.
     * @param endpointProperties the endpoint properties.
     * @return the hostname verifier.
     */
    public static HostnameVerifier buildHostnameVerifier(ConsumerEndpointProperties endpointProperties) {
        if (endpointProperties.getTls().getDisableHostnameValidation() || endpointProperties.getTls().getInsecure()) {
            return new NoopHostnameVerifier();
        } else {
            return SSLConnectionSocketFactory.getDefaultHostnameVerifier();
        }
    }

    /**
     * Builds a insecure trust manager.
     * @return a trust manager that accepts all certificates.
     */
    public static TrustManager buildInsecureTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String s) {
                // NOOP
            }
    
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String s) {
                // NOOP
            }
    
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
    }

    /**
     * Builds a key manager factory.
     * @param consumerEndpointProperties the endpoint properties.
     * @return a key manager factory.
     * @throws Exception if an error occurs.
     */
    public static KeyManagerFactory buildKeyManagerFactory(ConsumerEndpointProperties consumerEndpointProperties) throws Exception {
        TlsKeystoreConfigurationProperties keystoreProperties = consumerEndpointProperties.getTls().getKeystore();
        
        KeyStore keyStore = KeyStore.getInstance(keystoreProperties.getType());
        keyStore.load(keystoreProperties.getPath().getInputStream(), keystoreProperties.getPassword().toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystoreProperties.getPassword().toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        for(int i = 0; i < keyManagers.length; i++) {
            if (keyManagers[i] instanceof X509KeyManager x509KeyManager) {
                keyManagers[i] = new X509KeyManager() {
                    @Override
                    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
                        // check if aliases exists.
                        for(String keyType : keyTypes) {
                            String[] clientAliases = x509KeyManager.getClientAliases(keyType, issuers);
                            if(ArrayUtils.contains(clientAliases, keystoreProperties.getAlias())) {
                                return keystoreProperties.getAlias();
                            }
                        }
                        return null;
                    }

                    @Override
                    public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
                        return x509KeyManager.chooseServerAlias(arg0, arg1, arg2);
                    }

                    @Override
                    public X509Certificate[] getCertificateChain(String arg0) {
                        return x509KeyManager.getCertificateChain(arg0);
                    }

                    @Override
                    public String[] getClientAliases(String arg0, Principal[] arg1) {
                        return x509KeyManager.getClientAliases(arg0, arg1);
                    }

                    @Override
                    public PrivateKey getPrivateKey(String arg0) {
                        return x509KeyManager.getPrivateKey(arg0);
                    }

                    @Override
                    public String[] getServerAliases(String arg0, Principal[] arg1) {
                        return x509KeyManager.getServerAliases(arg0, arg1);
                    }
                };
            }
        }

        return keyManagerFactory;
    }

    /**
     * Builds a trust manager factory.
     * @param consumerEndpointProperties the endpoint properties.
     * @return a trust manager factory.
     * @throws Exception if an error occurs.
     */
    public static TrustManagerFactory buildTrustManagerFactory(ConsumerEndpointProperties consumerEndpointProperties) throws Exception {
        TlsTruststoreConfigurationProperties truststore = consumerEndpointProperties.getTls().getTruststore();

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

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststoreKeystore);
        return trustManagerFactory;
    }

    /**
     * Gets the HTTP protocol of this URL.
     * @param endpointProperties the TLS configuration.
     * @return the feign client instance.
     */
    public static HttpProtocol buildHttpProtocol(ConsumerEndpointProperties endpointProperties) {
        try {
            if (StringUtils.isBlank(endpointProperties.getUrl())) {
                return null;
            }

            String protocol = new URL(endpointProperties.getUrl()).getProtocol();
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
