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

import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;
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
 * The user authentication by pincode entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = UserAuthenticationByPincodeCreationDTO.class, patch = UserAuthenticationByPincodePatchDTO.class, read = UserAuthenticationByPincodeDTO.class, update = UserAuthenticationByPincodeUpdateDTO.class)
@Getter
@Setter
@ToString
@Document("users-authentications")
public class UserAuthenticationByPincodeEntity extends AbstractUserAuthenticationEntity {

    //@formatter:off
     /**
     * The securedPincode attribute.
     * -- GETTER --
     * Getter for {@link #securedPincode} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedPincode} property.
     * @param securedPincode the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Size(max = 128)
    private String securedPincode;

    //@formatter:off
     /**
     * The pincodeExpiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #pincodeExpiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #pincodeExpiryDateTime} property.
     * @param pincodeExpiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String pincodeExpiryDateTime;

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
     * The securedPincodeResetToken attribute.
     * -- GETTER --
     * Getter for {@link #securedPincodeResetToken} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedPincodeResetToken} property.
     * @param securedPincodeResetToken the value to set.
     */
     //@formatter:on
    @Size(max = 128)
    private String securedPincodeResetToken;

    //@formatter:off
     /**
     * The pincodeResetTokenExpiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #pincodeResetTokenExpiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #pincodeResetTokenExpiryDateTime} property.
     * @param pincodeResetTokenExpiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String pincodeResetTokenExpiryDateTime;

    //@formatter:off
     /**
     * The lastUsedSecuredPincodes attribute.
     * -- GETTER --
     * Getter for {@link #lastUsedSecuredPincodes} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #lastUsedSecuredPincodes} property.
     * @param lastUsedSecuredPincodes the value to set.
     */
     //@formatter:on
    @Size(max = 5)
    private List<@NotNull @NotBlank @Size(max = 128) String> lastUsedSecuredPincodes;

    /**
     * Default constructor.
     */
    public UserAuthenticationByPincodeEntity() {
        super();
        this.setType(UserAuthenticationType.PINCODE);
    }
}
