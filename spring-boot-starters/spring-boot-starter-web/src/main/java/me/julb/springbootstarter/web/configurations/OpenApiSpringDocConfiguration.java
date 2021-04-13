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
package me.julb.springbootstarter.web.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import me.julb.library.utility.data.search.Searchable;
import me.julb.springbootstarter.web.configurations.beans.OpenApiSpringDocConfigurationAuthenticationHeaderProperties;
import me.julb.springbootstarter.web.configurations.beans.OpenApiSpringDocConfigurationProperties;
import me.julb.springbootstarter.web.configurations.beans.OpenApiSpringDocConfigurationServerProperties;

/**
 * The OpenAPI configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(OpenApiSpringDocConfigurationProperties.class)
public class OpenApiSpringDocConfiguration {

    static {
        //@formatter:off
        SpringDocUtils.getConfig()
                .addRequestWrapperToIgnore(Pageable.class)
                .addRequestWrapperToIgnore(Searchable.class);
        //@formatter:on
    }

    /**
     * The OpenAPI configuration properties.
     */
    @Autowired
    private OpenApiSpringDocConfigurationProperties openApiConfigurationProperties;

    /**
     * Customize the OpenAPI generation.
     * @return the OpenAPI bean.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        //@formatter:off
        return new OpenAPI()
             .components(components())
             .info(info())
             .servers(servers())
             .addSecurityItem(securityRequirement());
        //@formatter:on
    }

    /**
     * Builds a info bean.
     * @return the info bean.
     */
    private Info info() {
        //@formatter:off
        Info info = new Info()
            .title(openApiConfigurationProperties.getName())
            .version(openApiConfigurationProperties.getVersion())
            .description(openApiConfigurationProperties.getDescription())
            .termsOfService(openApiConfigurationProperties.getTermsOfService())
            .contact(null)
            .license(null);
        //@formatter:on

        if (openApiConfigurationProperties.getLicense() != null) {
            //@formatter:off
            info.license(
                new License()
                    .name(openApiConfigurationProperties.getLicense().getName())
                    .url(openApiConfigurationProperties.getLicense().getUrl())
            );
            //@formatter:on
        }

        if (openApiConfigurationProperties.getContact() != null) {
            //@formatter:off
            info.contact(
                new Contact()
                    .name(openApiConfigurationProperties.getContact().getName())
                    .email(openApiConfigurationProperties.getContact().getMail())
                    .url(openApiConfigurationProperties.getContact().getUrl())
            );
            //@formatter:on
        }

        if (openApiConfigurationProperties.getExtraInfo() != null) {
            info.extensions(Map.of("x-julb-oas3-info", openApiConfigurationProperties.getExtraInfo()));
        }

        return info;
    }

    /**
     * Builds a servers bean.
     * @return the servers bean.
     */
    private List<Server> servers() {
        List<Server> servers = new ArrayList<Server>();
        if (CollectionUtils.isNotEmpty(openApiConfigurationProperties.getServers())) {
            for (OpenApiSpringDocConfigurationServerProperties serverProperties : openApiConfigurationProperties.getServers()) {
                servers.add(new Server().url(serverProperties.getUrl()).description(serverProperties.getDescription()));
            }
        }
        return servers;
    }

    /**
     * Builds a components bean.
     * @return the components bean.
     */
    private Components components() {
        Components components = new Components();
        if (openApiConfigurationProperties.getAuthentication() != null) {
            for (OpenApiSpringDocConfigurationAuthenticationHeaderProperties header : openApiConfigurationProperties.getAuthentication().getAuthenticationHeaders()) {
                components.addSecuritySchemes(header.getDisplayName(), new SecurityScheme().type(SecurityScheme.Type.APIKEY).name(header.getHeaderName()).in(In.HEADER));
            }
        }
        return components;
    }

    /**
     * Builds a security requirement.
     * @return the security requirement.
     */
    private SecurityRequirement securityRequirement() {
        SecurityRequirement securityRequirement = new SecurityRequirement();
        if (openApiConfigurationProperties.getAuthentication() != null) {
            for (OpenApiSpringDocConfigurationAuthenticationHeaderProperties header : openApiConfigurationProperties.getAuthentication().getAuthenticationHeaders()) {
                securityRequirement.addList(header.getDisplayName());
            }
        }
        return securityRequirement;
    }
}
