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
package io.julb.applications.webanalytics.controllers.params;

import io.julb.library.dto.messaging.events.WebAnalyticsAsyncMessageLevel;

import java.nio.charset.StandardCharsets;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The analytics request params
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class AnalyticsRequestParams {

    /**
     * The application name.
     */
    @NotNull
    @NotBlank
    private String an;

    /**
     * The application version.
     */
    @NotNull
    @NotBlank
    private String av;

    /**
     * The hit type (pageview, ...).
     */
    @NotNull
    @NotBlank
    private String t;

    /**
     * The data source (web, app)
     */
    @NotNull
    @NotBlank
    private String ds;

    /**
     * The visitor ID.
     */
    @NotNull
    @NotBlank
    private String uid;

    /**
     * The level.
     */
    private WebAnalyticsAsyncMessageLevel l = WebAnalyticsAsyncMessageLevel.INFO;

    /**
     * The Queue time.
     */
    private Long qt = 0L;

    /**
     * The user agent.
     */
    private String ua;

    /**
     * The Geographical indication.
     */
    private String geoid;

    /**
     * The document location.
     */
    private String dl;

    /**
     * The document hostname.
     */
    private String dh;

    /**
     * The document path.
     */
    private String dp;

    /**
     * The document referer.
     */
    private String dr;

    /**
     * The document title.
     */
    private String dt;

    /**
     * The screen resolution (800x600).
     */
    private String sr;

    /**
     * The viewport size.
     */
    private String vp;

    /**
     * The document encoding.
     */
    private String de = StandardCharsets.UTF_8.toString();

    /**
     * The screen color.
     */
    private String sd;

    /**
     * The user language.
     */
    private String ul;

    /**
     * The non-interaction hit (0-1).
     */
    private Integer ni;
}
