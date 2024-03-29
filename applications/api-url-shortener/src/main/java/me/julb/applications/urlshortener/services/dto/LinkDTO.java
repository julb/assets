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

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.audit.AbstractAuditedDTO;
import me.julb.library.dto.simple.user.UserRefDTO;

/**
 * The DTO used to return a link.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class LinkDTO extends AbstractAuditedDTO {

    //@formatter:off
     /**
     * The id attribute.
     * -- GETTER --
     * Getter for {@link #id} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #id} property.
     * @param id the value to set.
     */
     //@formatter:on
    @Schema(description = "Unique ID for the link")
    private String id;

    //@formatter:off
    /**
    * The host attribute.
    * -- GETTER --
    * Getter for {@link #host} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #host} property.
    * @param host the value to set.
    */
    //@formatter:on
    @Schema(description = "Host with which the link will be routed")
    private String host;

   //@formatter:off
    /**
    * The uri attribute.
    * -- GETTER --
    * Getter for {@link #uri} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #uri} property.
    * @param uri the value to set.
    */
    //@formatter:on
    @Schema(description = "URI for the link")
    private String uri;

    //@formatter:off
     /**
     * The url attribute.
     * -- GETTER --
     * Getter for {@link #url} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #url} property.
     * @param url the value to set.
     */
     //@formatter:on
    @Schema(description = "URL for the link")
    private String url;

   //@formatter:off
    /**
    * The targetUrl attribute.
    * -- GETTER --
    * Getter for {@link #targetUrl} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #targetUrl} property.
    * @param targetUrl the value to set.
    */
    //@formatter:on
    @Schema(description = "Target URL for the link")
    private String targetUrl;

    //@formatter:off
     /**
     * The enabled attribute.
     * -- GETTER --
     * Getter for {@link #enabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #enabled} property.
     * @param enabled the value to set.
     */
     //@formatter:on
    @Schema(description = "Flag to indicate if the link is active or not")
    private Boolean enabled;

    //@formatter:off
     /**
     * The hits attribute.
     * -- GETTER --
     * Getter for {@link #hits} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #hits} property.
     * @param hits the value to set.
     */
     //@formatter:on
    @Schema(description = "Number of hits for this link")
    private Integer hits;

    //@formatter:off
     /**
     * The user attribute.
     * -- GETTER --
     * Getter for {@link #user} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #user} property.
     * @param user the value to set.
     */
     //@formatter:on
    @Schema(description = "Author of the link")
    private UserRefDTO user;

    //@formatter:off
     /**
     * The tags attribute.
     * -- GETTER --
     * Getter for {@link #tags} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tags} property.
     * @param tags the value to set.
     */
     //@formatter:on
    @Schema(description = "Tags to associate to the link")
    private SortedSet<String> tags = new TreeSet<String>();
}
