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

    /**
     * The type.
     */
    private String type = TYPE;

    /**
     * The application name.
     */
    private String applicationName;

    /**
     * The application version.
     */
    private String applicationVersion;

    /**
     * The data source (web, app)
     */
    private String dataSource;

    /**
     * The Queue time.
     */
    private Long queueTime;

    /**
     * The visitor ID.
     */
    private String visitorId;

    /**
     * The user agent.
     */
    private String userAgent;

    /**
     * The Geographical indication.
     */
    private String geographicalLocation;

    /**
     * The document location.
     */
    private String documentLocation;

    /**
     * The document hostname.
     */
    private String documentHostname;

    /**
     * The document path.
     */
    private String documentPath;

    /**
     * The document referer.
     */
    private String documentReferer;

    /**
     * The document title.
     */
    private String documentTitle;

    /**
     * The document encoding.
     */
    private String documentEncoding;

    /**
     * The screen resolution (800x600).
     */
    private String screenResolution;

    /**
     * The viewport size.
     */
    private String viewportSize;

    /**
     * The screen color.
     */
    private String screenColor;

    /**
     * The user language.
     */
    private String userLanguage;

    /**
     * The hit type (pageview, ...).
     */
    private String hitType;

    /**
     * The non-interaction hit.
     */
    private Boolean nonInteractionHit;
}
