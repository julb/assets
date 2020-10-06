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

package me.julb.applications.authorizationserver.entities.profile;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import me.julb.applications.authorizationserver.constraints.UserFirstName;
import me.julb.applications.authorizationserver.constraints.UserLastName;
import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfilePatchDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileUpdateDTO;
import me.julb.library.mapping.annotations.ObjectMappingFactory;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Tag;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The user profile entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = UserProfileCreationDTO.class, patch = UserProfilePatchDTO.class, read = UserProfileDTO.class, update = UserProfileUpdateDTO.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document("users-profile")
public class UserProfileEntity extends AbstractAuditedEntity implements IIdentifiable {

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
    @DBRef
    private UserEntity user;

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
    @NotNull
    private UserProfileDisplayNameMethod displayNameMethod = UserProfileDisplayNameMethod.LASTNAME_FIRSTNAME;

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
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();

    /**
     * Gets the user's display name.
     * @return the user's display name.
     */
    public String getDisplayName() {
        switch (displayNameMethod) {
            case FIRSTNAME_LASTNAME:
                return StringUtils.join(this.firstName, Chars.SPACE, this.lastName);
            case LASTNAME_FIRSTNAME:
            default:
                return StringUtils.join(this.lastName, Chars.COMMA, Chars.SPACE, this.firstName);
        }
    }
}
