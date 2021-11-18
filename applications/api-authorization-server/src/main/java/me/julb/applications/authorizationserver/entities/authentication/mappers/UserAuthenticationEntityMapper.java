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

package me.julb.applications.authorizationserver.entities.authentication.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import me.julb.applications.authorizationserver.entities.authentication.AbstractUserAuthenticationEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByApiKeyEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import me.julb.applications.authorizationserver.services.dto.authentication.AbstractUserAuthenticationCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.AbstractUserAuthenticationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.AbstractUserAuthenticationPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.AbstractUserAuthenticationUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyWithRawKeyDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpWithRawSecretDTO;

/**
 * The {@link AbstractUserAuthenticationEntity} and subclasses mapper.
 * <br>
 * @author Julb.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAuthenticationEntityMapper {

    // ------------------------------------------ Generic authentication.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    default AbstractUserAuthenticationDTO map(AbstractUserAuthenticationEntity entity) {
        switch(entity.getType()) {
            case API_KEY:
                return map((UserAuthenticationByApiKeyEntity) entity);
            case PASSWORD:
                return map((UserAuthenticationByPasswordEntity) entity);
            case PINCODE:
                return map((UserAuthenticationByPincodeEntity) entity);
            case TOTP:
                return map((UserAuthenticationByTotpEntity) entity);
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    default AbstractUserAuthenticationEntity map(AbstractUserAuthenticationCreationDTO creationDTO) {
        switch(creationDTO.getType()) {
            case API_KEY:
                return map((UserAuthenticationByApiKeyCreationDTO) creationDTO);
            case PASSWORD:
                return map((UserAuthenticationByPasswordCreationDTO) creationDTO);
            case PINCODE:
                return map((UserAuthenticationByPincodeCreationDTO) creationDTO);
            case TOTP:
                return map((UserAuthenticationByTotpCreationDTO) creationDTO);
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    default void map(AbstractUserAuthenticationUpdateDTO updateDto, @MappingTarget AbstractUserAuthenticationEntity entity) {
        switch(entity.getType()) {
            case API_KEY:
                map((UserAuthenticationByApiKeyUpdateDTO) updateDto, (UserAuthenticationByApiKeyEntity) entity);
                break;
            case PASSWORD:
                map((UserAuthenticationByPasswordUpdateDTO) updateDto, (UserAuthenticationByPasswordEntity) entity);
                break;
            case PINCODE:
                map((UserAuthenticationByPincodeUpdateDTO) updateDto, (UserAuthenticationByPincodeEntity) entity);
                break;
            case TOTP:
                map((UserAuthenticationByTotpUpdateDTO) updateDto, (UserAuthenticationByTotpEntity) entity);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    default void map(AbstractUserAuthenticationPatchDTO patchDto, @MappingTarget AbstractUserAuthenticationEntity entity) {
        switch(entity.getType()) {
            case API_KEY:
                map((UserAuthenticationByApiKeyPatchDTO) patchDto, (UserAuthenticationByApiKeyEntity) entity);
                break;
            case PASSWORD:
                map((UserAuthenticationByPasswordPatchDTO) patchDto, (UserAuthenticationByPasswordEntity) entity);
                break;
            case PINCODE:
                map((UserAuthenticationByPincodePatchDTO) patchDto, (UserAuthenticationByPincodeEntity) entity);
                break;
            case TOTP:
                map((UserAuthenticationByTotpPatchDTO) patchDto, (UserAuthenticationByTotpEntity) entity);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    // ------------------------------------------ API key authentication.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserAuthenticationByApiKeyDTO map(UserAuthenticationByApiKeyEntity entity);

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserAuthenticationByApiKeyWithRawKeyDTO mapWithRawKey(UserAuthenticationByApiKeyEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserAuthenticationByApiKeyEntity map(UserAuthenticationByApiKeyCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(UserAuthenticationByApiKeyUpdateDTO updateDto, @MappingTarget UserAuthenticationByApiKeyEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(UserAuthenticationByApiKeyPatchDTO patchDto, @MappingTarget UserAuthenticationByApiKeyEntity entity);

    // ------------------------------------------ Password authentication.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserAuthenticationByPasswordDTO map(UserAuthenticationByPasswordEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserAuthenticationByPasswordEntity map(UserAuthenticationByPasswordCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(UserAuthenticationByPasswordUpdateDTO updateDto, @MappingTarget UserAuthenticationByPasswordEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(UserAuthenticationByPasswordPatchDTO patchDto, @MappingTarget UserAuthenticationByPasswordEntity entity);
    
    // ------------------------------------------ Pincode authentication.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserAuthenticationByPincodeDTO map(UserAuthenticationByPincodeEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserAuthenticationByPincodeEntity map(UserAuthenticationByPincodeCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(UserAuthenticationByPincodeUpdateDTO updateDto, @MappingTarget UserAuthenticationByPincodeEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(UserAuthenticationByPincodePatchDTO patchDto, @MappingTarget UserAuthenticationByPincodeEntity entity);
    
    // ------------------------------------------ Totp authentication.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserAuthenticationByTotpDTO map(UserAuthenticationByTotpEntity entity);

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    UserAuthenticationByTotpWithRawSecretDTO mapWithRawSecret(UserAuthenticationByTotpEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    UserAuthenticationByTotpEntity map(UserAuthenticationByTotpCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(UserAuthenticationByTotpUpdateDTO updateDto, @MappingTarget UserAuthenticationByTotpEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(UserAuthenticationByTotpPatchDTO patchDto, @MappingTarget UserAuthenticationByTotpEntity entity);


}
