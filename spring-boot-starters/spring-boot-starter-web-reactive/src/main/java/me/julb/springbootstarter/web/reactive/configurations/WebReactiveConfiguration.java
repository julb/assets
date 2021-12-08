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
package me.julb.springbootstarter.web.reactive.configurations;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.i18n.LocaleContextResolver;

import me.julb.springbootstarter.security.reactive.configurations.SecurityReactiveConfiguration;
import me.julb.springbootstarter.web.configurations.beans.CorsProperties;
import me.julb.springbootstarter.web.reactive.filters.RequestLoggingWebFilter;
import me.julb.springbootstarter.web.reactive.filters.TrademarkWebFilter;
import me.julb.springbootstarter.web.reactive.resolvers.locale.CustomLocaleContextResolver;
import me.julb.springbootstarter.web.reactive.resolvers.page.CustomPageableHandlerMethodArgumentResolver;
import me.julb.springbootstarter.web.reactive.resolvers.page.CustomSortHandlerMethodArgumentResolver;
import me.julb.springbootstarter.web.reactive.resolvers.search.CustomSearchableHandlerMethodArgumentResolver;

/**
 * The Reactive API configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@Import(SecurityReactiveConfiguration.class)
@EnableConfigurationProperties(CorsProperties.class)
@PropertySource(value = "classpath:META-INF/build-info.properties", ignoreResourceNotFound = true)
public class WebReactiveConfiguration  extends DelegatingWebFluxConfiguration {

    /**
     * The CORS properties.
     */
    @Autowired
    private CorsProperties cors;

    /**
     * Builds a filter to intercept trademark.
     * @return the generic filter bean.
     */
    @Bean
    @Order(1)
    public WebFilter trademarkWebFilter() {
        return new TrademarkWebFilter();
    }

    /**
     * Builds a filter to log requests.
     * @return the generic filter bean.
     */
    @Bean
    @Order(2)
    public WebFilter requestLoggingWebFilter() {
        return new RequestLoggingWebFilter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LocaleContextResolver createLocaleContextResolver() {
        return new CustomLocaleContextResolver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        if (CollectionUtils.isNotEmpty(this.cors.getAllowedOrigins())) {
            // @formatter:off
            registry.addMapping("/**")
                .allowedOrigins(this.cors.getAllowedOrigins().toArray(new String[0]))
                .allowedHeaders(this.cors.getAllowedHeaders().toArray(new String[0]))
                .allowedMethods(this.cors.getAllowedMethods().toArray(new String[0]))
                .exposedHeaders(this.cors.getExposedHeaders().toArray(new String[0]))
                .allowCredentials(this.cors.isAllowCredentials())
                .maxAge(this.cors.getMaxAge());
            // @formatter:on
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addFormatters(FormatterRegistry registry) {
        ApplicationConversionService.configure(registry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new CustomPageableHandlerMethodArgumentResolver());
        configurer.addCustomResolver(new CustomSortHandlerMethodArgumentResolver());
        configurer.addCustomResolver(new CustomSearchableHandlerMethodArgumentResolver());
    }
}
