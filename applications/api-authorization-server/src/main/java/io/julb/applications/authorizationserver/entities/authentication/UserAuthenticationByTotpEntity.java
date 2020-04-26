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

import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpPatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpUpdateDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The user authentication by TOTP entity.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
@Document("users-authentications")
@ObjectMappingFactory(creation = UserAuthenticationByTotpCreationDTO.class, patch = UserAuthenticationByTotpPatchDTO.class, read = UserAuthenticationByTotpDTO.class, update = UserAuthenticationByTotpUpdateDTO.class)
public class UserAuthenticationByTotpEntity extends AbstractUserAuthenticationEntity {

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
    @NotNull
    @NotBlank
    @Size(max = 64)
    private String name;

    //@formatter:off
     /**
     * The secret attribute.
     * -- GETTER --
     * Getter for {@link #secret} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #secret} property.
     * @param secret the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]+$")
    @Size(min = 32, max = 32)
    private String secret;

    /**
     * Default constructor.
     */
    public UserAuthenticationByTotpEntity() {
        super();
        this.setType(UserAuthenticationType.TOTP);
    }
}
