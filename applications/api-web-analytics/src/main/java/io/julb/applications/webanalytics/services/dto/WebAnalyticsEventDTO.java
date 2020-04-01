/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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
package io.julb.applications.webanalytics.services.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The web analytics event.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class WebAnalyticsEventDTO {

    /**
     * The web-analytics type.
     */
    private static final String TYPE = "web-analytics";

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
     * The applicationName attribute.
     * -- GETTER --
     * Getter for {@link #applicationName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #applicationName} property.
     * @param applicationName the value to set.
     */
     //@formatter:on
    private String applicationName;

    //@formatter:off
     /**
     * The applicationVersion attribute.
     * -- GETTER --
     * Getter for {@link #applicationVersion} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #applicationVersion} property.
     * @param applicationVersion the value to set.
     */
     //@formatter:on
    private String applicationVersion;

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
    private String dataSource;

    //@formatter:off
     /**
      * The Queue time.
     * -- GETTER --
     * Getter for {@link #queueTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #queueTime} property.
     * @param queueTime the value to set.
     */
     //@formatter:on
    private Long queueTime;

    //@formatter:off
     /**
      * The visitor ID.
     * -- GETTER --
     * Getter for {@link #visitorId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #visitorId} property.
     * @param visitorId the value to set.
     */
     //@formatter:on
    private String visitorId;

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
      * The Geographical indication.
     * -- GETTER --
     * Getter for {@link #geographicalLocation} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #geographicalLocation} property.
     * @param geographicalLocation the value to set.
     */
     //@formatter:on
    private String geographicalLocation;

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
     * The document title.
     * -- GETTER --
     * Getter for {@link #documentTitle} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #documentTitle} property.
     * @param documentTitle the value to set.
     */
     //@formatter:on
    private String documentTitle;

    //@formatter:off
     /**
     * The document encoding.
     * -- GETTER --
     * Getter for {@link #documentEncoding} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #documentEncoding} property.
     * @param documentEncoding the value to set.
     */
     //@formatter:on
    private String documentEncoding;

    //@formatter:off
     /**
     * The screen resolution.
     * -- GETTER --
     * Getter for {@link #screenResolution} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #screenResolution} property.
     * @param screenResolution the value to set.
     */
     //@formatter:on
    private String screenResolution;

    //@formatter:off
     /**
     * The viewport size.
     * -- GETTER --
     * Getter for {@link #viewportSize} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #viewportSize} property.
     * @param viewportSize the value to set.
     */
     //@formatter:on
    private String viewportSize;

    //@formatter:off
     /**
     * The screen color.
     * -- GETTER --
     * Getter for {@link #screenColor} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #screenColor} property.
     * @param screenColor the value to set.
     */
     //@formatter:on
    private String screenColor;

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
     * The hit type (pageview, ...).
     * -- GETTER --
     * Getter for {@link #hitType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #hitType} property.
     * @param hitType the value to set.
     */
     //@formatter:on
    private String hitType;

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
    private Boolean nonInteractionHit;
}
