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

package io.julb.applications.authorizationserver.services.dto.profile;

import io.julb.applications.authorizationserver.constraints.UserFirstName;
import io.julb.applications.authorizationserver.constraints.UserLastName;
import io.julb.applications.authorizationserver.entities.profile.UserProfileDisplayNameMethod;
import io.julb.library.utility.validator.constraints.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to get a user profile.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class UserProfileUpdateDTO {

    //@formatter:off
     /**
     * The avatarDocumentLibraryPath attribute.
     * -- GETTER --
     * Getter for {@link #avatarDocumentLibraryPath} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #avatarDocumentLibraryPath} property.
     * @param avatarDocumentLibraryPath the value to set.
     */
     //@formatter:on
    @Schema(description = "Avatar of the user")
    @Size(max = 128)
    private String avatarDocumentLibraryPath;

    //@formatter:off
    /**
    * The firstName attribute.
    * -- GETTER --
    * Getter for {@link #firstName} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #firstName} property.
    * @param firstName the value to set.
    */
    //@formatter:on
    @Schema(description = "First name of the user", required = true)
    @NotNull
    @NotBlank
    @UserFirstName
    private String firstName;

   //@formatter:off
    /**
    * The lastName attribute.
    * -- GETTER --
    * Getter for {@link #lastName} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #lastName} property.
    * @param lastName the value to set.
    */
    //@formatter:on
    @Schema(description = "Last name of the user", required = true)
    @NotNull
    @NotBlank
    @UserLastName
    private String lastName;

    //@formatter:off
     /**
     * The displayNameMethod attribute.
     * -- GETTER --
     * Getter for {@link #displayNameMethod} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #displayNameMethod} property.
     * @param displayNameMethod the value to set.
     */
     //@formatter:on
    @Schema(description = "Display name method of the user", required = true)
    @NotNull
    private UserProfileDisplayNameMethod displayNameMethod;

    //@formatter:off
     /**
     * The jobTitle attribute.
     * -- GETTER --
     * Getter for {@link #jobTitle} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #jobTitle} property.
     * @param jobTitle the value to set.
     */
     //@formatter:on
    @Schema(description = "Job title of the user")
    @Size(max = 128)
    private String jobTitle;

    //@formatter:off
     /**
     * The organization attribute.
     * -- GETTER --
     * Getter for {@link #organization} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #organization} property.
     * @param organization the value to set.
     */
     //@formatter:on
    @Schema(description = "Organization of the user")
    @Size(max = 128)
    private String organization;

    //@formatter:off
     /**
     * The organizationUnit attribute.
     * -- GETTER --
     * Getter for {@link #organizationUnit} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #organizationUnit} property.
     * @param organizationUnit the value to set.
     */
     //@formatter:on
    @Schema(description = "Orgnizational unit of the user")
    @Size(max = 128)
    private String organizationUnit;

    //@formatter:off
     /**
     * The websiteUrl attribute.
     * -- GETTER --
     * Getter for {@link #websiteUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #websiteUrl} property.
     * @param websiteUrl the value to set.
     */
     //@formatter:on
    @Schema(description = "Website of the user")
    @Size(max = 128)
    private String websiteUrl;

    //@formatter:off
     /**
     * The location attribute.
     * -- GETTER --
     * Getter for {@link #location} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #location} property.
     * @param location the value to set.
     */
     //@formatter:on
    @Schema(description = "Location of the user")
    @Size(max = 128)
    private String location;

    //@formatter:off
     /**
     * The twitterAccount attribute.
     * -- GETTER --
     * Getter for {@link #twitterAccount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #twitterAccount} property.
     * @param twitterAccount the value to set.
     */
     //@formatter:on
    @Schema(description = "Twitter account of the user")
    @Size(max = 128)
    private String twitterAccount;

    //@formatter:off
     /**
     * The linkedInAccount attribute.
     * -- GETTER --
     * Getter for {@link #linkedInAccount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #linkedInAccount} property.
     * @param linkedInAccount the value to set.
     */
     //@formatter:on
    @Schema(description = "Linked-In account of the user")
    @Size(max = 128)
    private String linkedInAccount;

    //@formatter:off
     /**
     * The instagramAccount attribute.
     * -- GETTER --
     * Getter for {@link #instagramAccount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #instagramAccount} property.
     * @param instagramAccount the value to set.
     */
     //@formatter:on
    @Schema(description = "Instagram account of the user")
    @Size(max = 128)
    private String instagramAccount;

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
    @Schema(description = "Tags associated to the user")
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();
}
