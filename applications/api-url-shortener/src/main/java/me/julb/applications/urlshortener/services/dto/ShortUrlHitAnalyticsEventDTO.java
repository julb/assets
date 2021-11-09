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
package me.julb.applications.urlshortener.services.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The short URL hit analytics event.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class ShortUrlHitAnalyticsEventDTO {

    /**
     * The short-url-hit-analytics type.
     */
    private static final String TYPE = "short-url-hit-analytics";

    //@formatter:off
     /**
     * The type attribute.
     * -- GETTER --
     * Getter for {@link #type} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #type} property.
     * @param type the value to set.
     */
     //@formatter:on
    private String type = TYPE;

    //@formatter:off
     /**
     * The tm attribute.
     * -- GETTER --
     * Getter for {@link #tm} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tm} property.
     * @param tm the value to set.
     */
     //@formatter:on
    private String tm;

    //@formatter:off
     /**
     * The data source (web, app)
     * -- GETTER --
     * Getter for {@link #dataSource} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dataSource} property.
     * @param dataSource the value to set.
     */
     //@formatter:on
    private String dataSource = "web";

    //@formatter:off
    /**
     * The document location.
     * -- GETTER --
     * Getter for {@link #documentLocation} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #documentLocation} property.
     * @param documentLocation the value to set.
     */
     //@formatter:on
    private String documentLocation;

    //@formatter:off
     /**
     * The document hostname.
     * -- GETTER --
     * Getter for {@link #documentHostname} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #documentHostname} property.
     * @param documentHostname the value to set.
     */
     //@formatter:on
    private String documentHostname;

    //@formatter:off
     /**
     * The document path.
     * -- GETTER --
     * Getter for {@link #documentPath} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #documentPath} property.
     * @param documentPath the value to set.
     */
     //@formatter:on
    private String documentPath;

    //@formatter:off
     /**
     * The document referer.
     * -- GETTER --
     * Getter for {@link #documentReferer} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #documentReferer} property.
     * @param documentReferer the value to set.
     */
     //@formatter:on
    private String documentReferer;

    //@formatter:off
     /**
     * The linkId attribute.
     * -- GETTER --
     * Getter for {@link #linkId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #linkId} property.
     * @param linkId the value to set.
     */
     //@formatter:on
    private String linkId;

    //@formatter:off
     /**
      * The user agent.
     * -- GETTER --
     * Getter for {@link #userAgent} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #userAgent} property.
     * @param userAgent the value to set.
     */
     //@formatter:on
    private String userAgent;

    //@formatter:off
     /**
     * The user language.
     * -- GETTER --
     * Getter for {@link #userLanguage} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #userLanguage} property.
     * @param userLanguage the value to set.
     */
     //@formatter:on
    private String userLanguage;

    //@formatter:off
     /**
     * The userIpv4Address attribute.
     * -- GETTER --
     * Getter for {@link #userIpv4Address} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #userIpv4Address} property.
     * @param userIpv4Address the value to set.
     */
     //@formatter:on
    private String userIpv4Address;

    //@formatter:off
    /**
     * The hit type (pageview, ...).
     * -- GETTER --
     * Getter for {@link #hitType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #hitType} property.
     * @param hitType the value to set.
     */
     //@formatter:on
    private String hitType = "click";

    //@formatter:off
     /**
     * The non-interaction hit.
     * -- GETTER --
     * Getter for {@link #nonInteractionHit} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #nonInteractionHit} property.
     * @param nonInteractionHit the value to set.
     */
     //@formatter:on
    private Boolean nonInteractionHit = Boolean.FALSE;

}
