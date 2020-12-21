package me.julb.applications.helloworld.consumers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import feign.Client;
import feign.RequestInterceptor;
import me.julb.applications.helloworld.consumers.ApiHelloWorldFeignClient.LocalConsumerConfiguration;
import me.julb.library.dto.simple.message.MessageDTO;
import me.julb.springbootstarter.consumer.configurations.properties.ConsumerEndpointProperties;
import me.julb.springbootstarter.consumer.interceptors.AddLocaleRequestInterceptor;
import me.julb.springbootstarter.consumer.interceptors.AddRequestHeaderRequestInterceptor;
import me.julb.springbootstarter.consumer.interceptors.ApplyMultipleRequestInterceptors;
import me.julb.springbootstarter.consumer.utility.FeignClientUtility;
import me.julb.springbootstarter.consumer.utility.SearchableAndPageable;
import me.julb.springbootstarter.core.configurations.properties.SecurityInternalApiKeyProperties;

/**
 * A Feign client for VPN healthcheck.
 * <P>
 * @author Julb.
 */
@FeignClient(name = "api-hello-world", url = "${application.hello-world.endpoint.url}", configuration = LocalConsumerConfiguration.class)
public interface ApiHelloWorldFeignClient {

    /**
     * Says hello to everyone.
     * @param searchableAndPageable the searchableAndPageable information.
     * @return the messages.
     */
    @GetMapping("/hello")
    Page<MessageDTO> sayHello(SearchableAndPageable searchableAndPageable);

    /**
     * Says hello to someone.
     * @param name to guy to say hello to.
     * @return the message.
     */
    @GetMapping("/hello/{name}")
    MessageDTO sayHelloTo(@PathVariable("name") String name);

    /**
     * The local consumer properties.
     * <P>
     * @author Julb.
     */
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "application.hello-world")
    class LocalConsumerProperties {

        //@formatter:off
         /**
         * The endpoint attribute.
         * -- GETTER --
         * Getter for {@link #endpoint} property.
         * @return the value.
         * -- SETTER --
         * Setter for {@link #endpoint} property.
         * @param endpoint the value to set.
         */
         //@formatter:on
        private ConsumerEndpointProperties endpoint;
    }

    /**
     * The local consumer configuration.
     * <P>
     * @author Julb.
     */
    @EnableConfigurationProperties({LocalConsumerProperties.class, SecurityInternalApiKeyProperties.class})
    class LocalConsumerConfiguration {

        /**
         * The consumer properties.
         */
        @Autowired
        private LocalConsumerProperties properties;

        /**
         * The security internal API key properties.
         */
        @Autowired
        private SecurityInternalApiKeyProperties securityInternalApiKeyProperties;

        /**
         * Builds a feign client instance.
         * @return the feign client instance.
         */
        @Bean
        public Client feignClient() {
            return FeignClientUtility.feignClientUtil(properties.getEndpoint());
        }

        /**
         * Adds request interceptors.
         * @return the request interceptors to add.
         */
        @Bean
        public RequestInterceptor requestInterceptor() {
            List<RequestInterceptor> requestInterceptors = new ArrayList<RequestInterceptor>();
            requestInterceptors.add(new AddLocaleRequestInterceptor());
            if (securityInternalApiKeyProperties.isInternalApiKeyEnabled()) {
                requestInterceptors.add(new AddRequestHeaderRequestInterceptor(securityInternalApiKeyProperties.getHeaderName(), securityInternalApiKeyProperties.getHeaderValue()));
            }
            return new ApplyMultipleRequestInterceptors(requestInterceptors);
        }
    }

}
