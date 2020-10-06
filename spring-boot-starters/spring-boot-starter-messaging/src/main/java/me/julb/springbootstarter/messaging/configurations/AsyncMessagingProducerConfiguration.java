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

package me.julb.springbootstarter.messaging.configurations;

import com.rabbitmq.client.DefaultSaslConfig;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

import me.julb.springbootstarter.messaging.processors.AsyncMessageProducerProcessor;

/**
 * MQ binding configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableBinding({AsyncMessageProducerProcessor.class})
public class AsyncMessagingProducerConfiguration {

    /**
     * The rabbit properties.
     */
    @Autowired
    private RabbitProperties rabbitProperties;

    /**
     * The rabbit connection factory.
     */
    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    /**
     * Initializes the saslConfig to SASLConfig.EXTERNAL if MTLS used.<br>
     * 
     * <pre>
     * See https://github.com/spring-projects/spring-boot/issues/6719
     * </pre>
     */
    @PostConstruct
    public void initSaslConfig() {
        if (rabbitProperties.getSsl().determineEnabled() && rabbitProperties.getSsl().getKeyStore() != null) {
            cachingConnectionFactory.getRabbitConnectionFactory().setSaslConfig(DefaultSaslConfig.EXTERNAL);
        }
    }
}
