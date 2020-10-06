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

package me.julb.applications.authorizationserver.entities.mail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailPatchDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailUpdateDTO;
import me.julb.library.mapping.annotations.ObjectMappingFactory;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The user mail entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = UserMailCreationDTO.class, patch = UserMailPatchDTO.class, read = UserMailDTO.class, update = UserMailUpdateDTO.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "mail")
@NoArgsConstructor
@Document("users-mails")
public class UserMailEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The mail attribute.
     * -- GETTER --
     * Getter for {@link #mail} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mail} property.
     * @param mail the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Email
    private String mail;

    //@formatter:off
     /**
     * The primary attribute.
     * -- GETTER --
     * Getter for {@link #primary} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #primary} property.
     * @param primary the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean primary;

    //@formatter:off
     /**
     * The verified attribute.
     * -- GETTER --
     * Getter for {@link #verified} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #verified} property.
     * @param verified the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean verified;

    //@formatter:off
     /**
     * The securedMailVerifyToken attribute.
     * -- GETTER --
     * Getter for {@link #securedMailVerifyToken} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedMailVerifyToken} property.
     * @param securedMailVerifyToken the value to set.
     */
     //@formatter:on
    @Size(max = 128)
    private String securedMailVerifyToken;

    //@formatter:off
     /**
     * The mailVerifyTokenExpiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #mailVerifyTokenExpiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mailVerifyTokenExpiryDateTime} property.
     * @param mailVerifyTokenExpiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String mailVerifyTokenExpiryDateTime;

}
