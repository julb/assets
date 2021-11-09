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

import lombok.Getter;
import lombok.Setter;

/**
 * The OpenAPI SpringDoc extra-info configuration properties.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class OpenApiSpringDocConfigurationExtraInfoProperties {

    //@formatter:off
     /**
     * The displayName attribute.
     * -- GETTER --
     * Getter for {@link #displayName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #displayName} property.
     * @param displayName the value to set.
     */
     //@formatter:on
    private String displayName;

    //@formatter:off
     /**
     * The businessCategory attribute.
     * -- GETTER --
     * Getter for {@link #businessCategory} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #businessCategory} property.
     * @param businessCategory the value to set.
     */
     //@formatter:on
    private String businessCategory;

    //@formatter:off
     /**
     * The iconUrl attribute.
     * -- GETTER --
     * Getter for {@link #iconUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #iconUrl} property.
     * @param iconUrl the value to set.
     */
     //@formatter:on
    private String iconUrl;

    //@formatter:off
     /**
     * The thumbnailUrl attribute.
     * -- GETTER --
     * Getter for {@link #thumbnailUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #thumbnailUrl} property.
     * @param thumbnailUrl the value to set.
     */
     //@formatter:on
    private String thumbnailUrl;

    //@formatter:off
     /**
     * The logoUrl attribute.
     * -- GETTER --
     * Getter for {@link #logoUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #logoUrl} property.
     * @param logoUrl the value to set.
     */
     //@formatter:on
    private String logoUrl;

    //@formatter:off
     /**
     * The longDescription attribute.
     * -- GETTER --
     * Getter for {@link #longDescription} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #longDescription} property.
     * @param longDescription the value to set.
     */
     //@formatter:on
    private String longDescription;

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
    private String[] tags;

    //@formatter:off
     /**
     * The keywords attribute.
     * -- GETTER --
     * Getter for {@link #keywords} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #keywords} property.
     * @param keywords the value to set.
     */
     //@formatter:on
    private String[] keywords;

    //@formatter:off
     /**
     * The starred attribute.
     * -- GETTER --
     * Getter for {@link #starred} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #starred} property.
     * @param starred the value to set.
     */
     //@formatter:on
    private Boolean starred = Boolean.FALSE;

    //@formatter:off
     /**
     * The vcsGitUrl attribute.
     * -- GETTER --
     * Getter for {@link #vcsGitUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #vcsGitUrl} property.
     * @param vcsGitUrl the value to set.
     */
     //@formatter:on
    private String vcsGitUrl;

    //@formatter:off
     /**
     * The vcsGitRevision attribute.
     * -- GETTER --
     * Getter for {@link #vcsGitRevision} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #vcsGitRevision} property.
     * @param vcsGitRevision the value to set.
     */
     //@formatter:on
    private String vcsGitRevision;
}
