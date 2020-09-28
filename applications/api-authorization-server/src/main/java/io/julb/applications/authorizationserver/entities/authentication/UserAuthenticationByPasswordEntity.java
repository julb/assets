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

package io.julb.applications.authorizationserver.entities.authentication;

import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordUpdateDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.utility.validator.constraints.DateTimeISO8601;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The user authentication by password entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = UserAuthenticationByPasswordCreationDTO.class, patch = UserAuthenticationByPasswordPatchDTO.class, read = UserAuthenticationByPasswordDTO.class, update = UserAuthenticationByPasswordUpdateDTO.class)
@Getter
@Setter
@ToString
@Document("users-authentications")
public class UserAuthenticationByPasswordEntity extends AbstractUserAuthenticationEntity {

    //@formatter:off
     /**
     * The securedPassword attribute.
     * -- GETTER --
     * Getter for {@link #securedPassword} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedPassword} property.
     * @param securedPassword the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Size(max = 128)
    private String securedPassword;

    //@formatter:off
     /**
     * The passwordExpiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #passwordExpiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #passwordExpiryDateTime} property.
     * @param passwordExpiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String passwordExpiryDateTime;

    //@formatter:off
     /**
     * The mfaEnabled attribute.
     * -- GETTER --
     * Getter for {@link #mfaEnabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mfaEnabled} property.
     * @param mfaEnabled the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean mfaEnabled;

    //@formatter:off
     /**
     * The securedPasswordResetToken attribute.
     * -- GETTER --
     * Getter for {@link #securedPasswordResetToken} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedPasswordResetToken} property.
     * @param securedPasswordResetToken the value to set.
     */
     //@formatter:on
    @Size(max = 128)
    private String securedPasswordResetToken;

    //@formatter:off
     /**
     * The passwordResetTokenExpiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #passwordResetTokenExpiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #passwordResetTokenExpiryDateTime} property.
     * @param passwordResetTokenExpiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String passwordResetTokenExpiryDateTime;

    //@formatter:off
     /**
     * The lastUsedSecuredPasswords attribute.
     * -- GETTER --
     * Getter for {@link #lastUsedSecuredPasswords} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #lastUsedSecuredPasswords} property.
     * @param lastUsedSecuredPasswords the value to set.
     */
     //@formatter:on
    @Size(max = 5)
    private List<@NotNull @NotBlank @Size(max = 128) String> lastUsedSecuredPasswords;

    /**
     * Default constructor.
     */
    public UserAuthenticationByPasswordEntity() {
        super();
        this.setType(UserAuthenticationType.PASSWORD);
    }
}
