/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package me.julb.library.dto.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The authenticated user DTO.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AuthenticatedUserDTO {

    //@formatter:off
     /**
     * The sessionId attribute.
     * -- GETTER --
     * Getter for {@link #sessionId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #sessionId} property.
     * @param sessionId the value to set.
     */
     //@formatter:on
    private String sessionId;

    //@formatter:off
     /**
     * The userId attribute.
     * -- GETTER --
     * Getter for {@link #userId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #userId} property.
     * @param userId the value to set.
     */
     //@formatter:on
    private String userId;

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    private String name;

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
     * The firstName attribute.
     * -- GETTER --
     * Getter for {@link #firstName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #firstName} property.
     * @param firstName the value to set.
     */
     //@formatter:on
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
    private String lastName;

    //@formatter:off
     /**
     * The pictureUrl attribute.
     * -- GETTER --
     * Getter for {@link #pictureUrl} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #pictureUrl} property.
     * @param pictureUrl the value to set.
     */
     //@formatter:on
    private String pictureUrl;

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
    private String websiteUrl;

    //@formatter:off
     /**
     * The mail attribute.
     * -- GETTER --
     * Getter for {@link #mail} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mail} property.
     * @param mail the value to set.
     */
     //@formatter:on
    private String mail;

    //@formatter:off
     /**
     * The mailVerified attribute.
     * -- GETTER --
     * Getter for {@link #mailVerified} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mailVerified} property.
     * @param mailVerified the value to set.
     */
     //@formatter:on
    private Boolean mailVerified;

    //@formatter:off
     /**
     * The mobilePhone attribute.
     * -- GETTER --
     * Getter for {@link #mobilePhone} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mobilePhone} property.
     * @param mobilePhone the value to set.
     */
     //@formatter:on
    private String mobilePhone;

    //@formatter:off
     /**
     * The mobilePhoneVerified attribute.
     * -- GETTER --
     * Getter for {@link #mobilePhoneVerified} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mobilePhoneVerified} property.
     * @param mobilePhoneVerified the value to set.
     */
     //@formatter:on
    private Boolean mobilePhoneVerified;

    //@formatter:off
     /**
     * The locale attribute.
     * -- GETTER --
     * Getter for {@link #locale} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #locale} property.
     * @param locale the value to set.
     */
     //@formatter:on
    private Locale locale;

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
    private String organizationUnit;

    //@formatter:off
     /**
     * The mfaVerified attribute.
     * -- GETTER --
     * Getter for {@link #mfaVerified} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mfaVerified} property.
     * @param mfaVerified the value to set.
     */
     //@formatter:on
    private Boolean mfaVerified;

    //@formatter:off
     /**
     * The roles attribute.
     * -- GETTER --
     * Getter for {@link #roles} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #roles} property.
     * @param roles the value to set.
     */
     //@formatter:on
    private Collection<UserRole> roles = new HashSet<>();

    //@formatter:off
     /**
     * The permissions attribute.
     * -- GETTER --
     * Getter for {@link #permissions} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #permissions} property.
     * @param permissions the value to set.
     */
     //@formatter:on
    private Collection<String> permissions = new HashSet<>();

}
