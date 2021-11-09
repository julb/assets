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

package me.julb.applications.urlshortener.entities;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.julb.applications.urlshortener.annotations.ShortURLURI;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.enums.HttpProtocol;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DNS;
import me.julb.library.utility.validator.constraints.HTTPLink;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Tag;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The link entity.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Document("links")
public class LinkEntity extends AbstractAuditedEntity implements IIdentifiable {

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
    @Id
    @Identifier
    private String id;

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
    @NotNull
    @NotBlank
    @Trademark
    private String tm;

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
    private String targetUrl;

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
    @NotNull
    @Min(0)
    private Integer hits;

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
    @NotNull
    private Boolean enabled;

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
    @NotNull
    @Valid
    private UserRefEntity user;

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
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();

    /**
     * Gets the URL of this link.
     * @return the URL of this link.
     */
    public String getUrl() {
        return StringUtils.join(HttpProtocol.HTTPS.urlPrefix(), host, Chars.SLASH, uri);
    }

    /**
     * Increments the number of hits by 1.
     */
    public void incrementNumberOfHits() {
        if (this.hits == null) {
            this.hits = 0;
        }
        this.hits++;
    }

}
