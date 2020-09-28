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

import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyPatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyUpdateDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The user authentication by API KEY entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = UserAuthenticationByApiKeyCreationDTO.class, patch = UserAuthenticationByApiKeyPatchDTO.class, read = UserAuthenticationByApiKeyDTO.class, update = UserAuthenticationByApiKeyUpdateDTO.class)
@Getter
@Setter
@ToString
@Document("users-authentications")
public class UserAuthenticationByApiKeyEntity extends AbstractUserAuthenticationEntity {

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
     * The securedKey attribute.
     * -- GETTER --
     * Getter for {@link #securedKey} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedKey} property.
     * @param securedKey the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Size(max = 128)
    private String securedKey;

    /**
     * Default constructor.
     */
    public UserAuthenticationByApiKeyEntity() {
        super();
        this.setType(UserAuthenticationType.API_KEY);
    }
}
