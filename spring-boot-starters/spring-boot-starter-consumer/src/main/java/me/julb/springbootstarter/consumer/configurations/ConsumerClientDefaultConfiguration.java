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
package me.julb.springbootstarter.consumer.configurations;

import com.fasterxml.jackson.databind.Module;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.PageableSpringEncoder;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;
import me.julb.springbootstarter.consumer.decoders.DefaultClientErrorDecoder;
import me.julb.springbootstarter.consumer.decoders.DefaultRetryer;
import me.julb.springbootstarter.consumer.decoders.JsonStringDecoder;
import me.julb.springbootstarter.consumer.encoders.JsonStringEncoder;
import me.julb.springbootstarter.consumer.encoders.SearchableAndPageableSpringEncoder;
import me.julb.springbootstarter.consumer.encoders.SearchableSpringEncoder;

/**
 * The consumer configuration.
 * <P>
 * @author Julb.
 */
@Configuration
public class ConsumerClientDefaultConfiguration {

    /**
     * The default encoder.
     * @param messageConverters the message converters.
     * @return the encoder.
     */
    @Bean
    @Primary
    @ConditionalOnBean(HttpMessageConverters.class)
    public Encoder defaultEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FormEncoder(new SearchableAndPageableSpringEncoder(new SearchableSpringEncoder(new PageableSpringEncoder(new SpringEncoder(messageConverters)))));
    }

    /**
     * Defines the encoder to use.
     * @return the encoder to use.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(HttpMessageConverters.class)
    public Encoder encoder() {
        return new FormEncoder(new SearchableAndPageableSpringEncoder(new SearchableSpringEncoder(new PageableSpringEncoder(new JsonStringEncoder()))));
    }

    /**
     * Defines the decoder to use.
     * @return the decoder to use.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(HttpMessageConverters.class)
    public Decoder decoder() {
        return new JsonStringDecoder();
    }

    /**
     * The default error decoder.
     * @return the default error decoder.
     */
    @Bean
    @Primary
    public ErrorDecoder defaultErrorDecoder() {
        return new DefaultClientErrorDecoder();
    }

    /**
     * The default retryer.
     * @return the default retryer.
     */
    @Bean
    @Primary
    public DefaultRetryer defaultRetryer() {
        return new DefaultRetryer();
    }

    /**
     * The page jackson module.
     * @return the page jackson module.
     */
    @Bean
    @ConditionalOnMissingBean(PageJacksonModule.class)
    @ConditionalOnClass(name = "org.springframework.data.domain.Page")
    public Module defaultPageJacksonModule() {
        return new PageJacksonModule();
    }

    /**
     * The sort jackson module.
     * @return the sort jackson module.
     */
    @Bean
    @ConditionalOnMissingBean(SortJacksonModule.class)
    @ConditionalOnClass(name = "org.springframework.data.domain.Sort")
    public Module defaultSortJacksonModule() {
        return new SortJacksonModule();
    }
}
