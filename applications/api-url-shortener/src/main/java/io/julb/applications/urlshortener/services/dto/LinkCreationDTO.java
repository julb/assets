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

package io.julb.applications.urlshortener.services.dto;

import io.julb.applications.urlshortener.annotations.ShortURLURI;
import io.julb.library.utility.validator.constraints.DNS;
import io.julb.library.utility.validator.constraints.HTTPLink;
import io.julb.library.utility.validator.constraints.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to create a link.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class LinkCreationDTO {

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
    @NotNull
    @NotBlank
    @DNS
    @Schema(description = "Host with which the link will be routed", required = true)
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
    @NotNull
    @NotBlank
    @ShortURLURI
    @Schema(description = "URI for the link", required = true)
    private String uri;

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
    @NotNull
    @NotBlank
    @HTTPLink
    @Schema(description = "Target URL for the link", required = true)
    private String targetUrl;

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
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();
}
