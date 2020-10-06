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
package me.julb.applications.webanalytics.controllers.params;

import java.nio.charset.StandardCharsets;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.messaging.events.WebAnalyticsAsyncMessageLevel;

/**
 * The analytics request params
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class AnalyticsRequestParams {

    //@formatter:off
     /**
     * The application name.
     * -- GETTER --
     * Getter for {@link #an} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #an} property.
     * @param an the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String an;

    //@formatter:off
     /**
     * The application version.
     * -- GETTER --
     * Getter for {@link #av} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #av} property.
     * @param av the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String av;

    //@formatter:off
     /**
     * The hit type (pageview, ...).
     * -- GETTER --
     * Getter for {@link #t} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #t} property.
     * @param t the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String t;

    //@formatter:off
     /**
     * The data source (web, app)
     * -- GETTER --
     * Getter for {@link #ds} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ds} property.
     * @param ds the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String ds;

    //@formatter:off
     /**
     * The visitor ID.
     * -- GETTER --
     * Getter for {@link #uid} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #uid} property.
     * @param uid the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String uid;

    //@formatter:off
     /**
     * The level attribute.
     * -- GETTER --
     * Getter for {@link #l} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #l} property.
     * @param l the value to set.
     */
     //@formatter:on
    private WebAnalyticsAsyncMessageLevel l = WebAnalyticsAsyncMessageLevel.INFO;

    //@formatter:off
     /**
     * The Queue time.
     * -- GETTER --
     * Getter for {@link #qt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #qt} property.
     * @param qt the value to set.
     */
     //@formatter:on
    private Long qt = 0L;

    //@formatter:off
     /**
     * The user agent.
     * -- GETTER --
     * Getter for {@link #ua} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ua} property.
     * @param ua the value to set.
     */
     //@formatter:on
    private String ua;

    //@formatter:off
     /**
     * The Geographical indication.
     * -- GETTER --
     * Getter for {@link #geoid} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #geoid} property.
     * @param geoid the value to set.
     */
     //@formatter:on
    private String geoid;

    //@formatter:off
     /**
     * The document location.
     * -- GETTER --
     * Getter for {@link #dl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dl} property.
     * @param dl the value to set.
     */
     //@formatter:on
    private String dl;

    //@formatter:off
     /**
     * The document hostname.
     * -- GETTER --
     * Getter for {@link #dh} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dh} property.
     * @param dh the value to set.
     */
     //@formatter:on
    private String dh;

    //@formatter:off
     /**
     * The document path.
     * -- GETTER --
     * Getter for {@link #dp} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dp} property.
     * @param dp the value to set.
     */
     //@formatter:on
    private String dp;

    //@formatter:off
     /**
     * The document referer.
     * -- GETTER --
     * Getter for {@link #dr} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dr} property.
     * @param dr the value to set.
     */
     //@formatter:on
    private String dr;

    //@formatter:off
     /**
     * The document title.
     * -- GETTER --
     * Getter for {@link #dt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #dt} property.
     * @param dt the value to set.
     */
     //@formatter:on
    private String dt;

    //@formatter:off
     /**
     * The screen resolution (800x600).
     * -- GETTER --
     * Getter for {@link #sr} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #sr} property.
     * @param sr the value to set.
     */
     //@formatter:on
    private String sr;

    //@formatter:off
     /**
     * The viewport size.
     * -- GETTER --
     * Getter for {@link #vp} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #vp} property.
     * @param vp the value to set.
     */
     //@formatter:on
    private String vp;

    //@formatter:off
     /**
     * The document encoding.
     * -- GETTER --
     * Getter for {@link #de} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #de} property.
     * @param de the value to set.
     */
     //@formatter:on
    private String de = StandardCharsets.UTF_8.toString();

    //@formatter:off
     /**
     * The screen color.
     * -- GETTER --
     * Getter for {@link #sd} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #sd} property.
     * @param sd the value to set.
     */
     //@formatter:on
    private String sd;

    //@formatter:off
     /**
     * The user language.
     * -- GETTER --
     * Getter for {@link #ul} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ul} property.
     * @param ul the value to set.
     */
     //@formatter:on
    private String ul;

    //@formatter:off
     /**
     * The non-interaction hit (0-1).
     * -- GETTER --
     * Getter for {@link #ni} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ni} property.
     * @param ni the value to set.
     */
     //@formatter:on
    private Integer ni;
}
