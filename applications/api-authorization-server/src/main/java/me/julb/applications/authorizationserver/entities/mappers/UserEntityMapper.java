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

package me.julb.applications.authorizationserver.entities.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.services.dto.signup.AbstractSignupCreationDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithInviteDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;

/**
 * The  {@link UserEntity} mapper.
 * <br>
 * @author Julb.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {
    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserDTO map(UserEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    default UserEntity map(AbstractSignupCreationDTO creationDTO) {
        if (creationDTO instanceof SignupWithInviteDTO s) {
            return map(s);
        } else if(creationDTO instanceof SignupWithPasswordCreationDTO s) {
            return map(s);
        } else if(creationDTO instanceof SignupWithPincodeCreationDTO s) {
            return map(s);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserEntity map(SignupWithInviteDTO creationDTO);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserEntity map(SignupWithPasswordCreationDTO creationDTO);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserEntity map(SignupWithPincodeCreationDTO creationDTO);
}
