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
package me.julb.springbootstarter.web.configurations.beans;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;

/**
 * The OpenAPI SpringDoc configuration properties.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "springdoc.additional-properties")
public class OpenApiSpringDocConfigurationProperties {

    //@formatter:off
    /**
     * The OpenApi docket API title.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    private String name;

    //@formatter:off
    /**
     * The OpenApi docket API version.
     * -- GETTER --
     * Getter for {@link #version} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #version} property.
     * @param version the value to set.
     */
     //@formatter:on
    private String version;

    //@formatter:off
     /**
     * The OpenApi docket API description.
     * -- GETTER --
     * Getter for {@link #description} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #description} property.
     * @param description the value to set.
     */
     //@formatter:on
    private String description;

    //@formatter:off
     /**
     * The OpenApi docket API terms of service.
     * -- GETTER --
     * Getter for {@link #termsOfService} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #termsOfService} property.
     * @param termsOfService the value to set.
     */
     //@formatter:on
    private String termsOfService;

    //@formatter:off
     /**
     * The license attribute.
     * -- GETTER --
     * Getter for {@link #license} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #license} property.
     * @param license the value to set.
     */
     //@formatter:on
    private OpenApiSpringDocConfigurationLicenseProperties license;

    //@formatter:off
     /**
     * The contact attribute.
     * -- GETTER --
     * Getter for {@link #contact} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #contact} property.
     * @param contact the value to set.
     */
     //@formatter:on
    private OpenApiSpringDocConfigurationContactProperties contact;

    //@formatter:off
     /**
     * The extraInfo attribute.
     * -- GETTER --
     * Getter for {@link #extraInfo} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #extraInfo} property.
     * @param extraInfo the value to set.
     */
     //@formatter:on
    private OpenApiSpringDocConfigurationExtraInfoProperties extraInfo;

    //@formatter:off
     /**
     * The servers attribute.
     * -- GETTER --
     * Getter for {@link #servers} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #servers} property.
     * @param servers the value to set.
     */
     //@formatter:on
    private List<OpenApiSpringDocConfigurationServerProperties> servers;

    //@formatter:off
     /**
     * The authentication attribute.
     * -- GETTER --
     * Getter for {@link #authentication} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #authentication} property.
     * @param authentication the value to set.
     */
     //@formatter:on
    private OpenApiSpringDocConfigurationAuthenticationProperties authentication;

    //@formatter:off
     /**
     * The commonHttpErrorStatuses attribute.
     * -- GETTER --
     * Getter for {@link #commonHttpErrorStatuses} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #commonHttpErrorStatuses} property.
     * @param commonHttpErrorStatuses the value to set.
     */
    private HttpStatus[] commonHttpErrorStatuses = new HttpStatus[] {
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.METHOD_NOT_ALLOWED,
            HttpStatus.NOT_ACCEPTABLE,
            HttpStatus.CONFLICT,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.NOT_IMPLEMENTED,
            HttpStatus.BAD_GATEWAY,
            HttpStatus.SERVICE_UNAVAILABLE,
            HttpStatus.GATEWAY_TIMEOUT
    };
    //@formatter:on

}
