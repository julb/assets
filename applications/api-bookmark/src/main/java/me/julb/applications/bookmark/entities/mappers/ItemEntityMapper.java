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

package me.julb.applications.bookmark.entities.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import me.julb.applications.bookmark.entities.AbstractItemEntity;
import me.julb.applications.bookmark.entities.ExternalLinkEntity;
import me.julb.applications.bookmark.entities.FolderEntity;
import me.julb.applications.bookmark.entities.ObjectLinkEntity;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkCreationDTO;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkDTO;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkPatchDTO;
import me.julb.applications.bookmark.services.dto.externallink.ExternalLinkUpdateDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderCreationDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderPatchDTO;
import me.julb.applications.bookmark.services.dto.folder.FolderUpdateDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemCreationDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemPatchDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemUpdateDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkCreationDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkPatchDTO;
import me.julb.applications.bookmark.services.dto.object.ObjectLinkUpdateDTO;
import me.julb.library.dto.simple.identifier.IdentifierDTO;
import me.julb.springbootstarter.mapping.entities.message.mappers.ShortMessageEntityMapper;

/**
 * The {@link AbstractItemEntity} and subclasses mapper.
 * <br>
 * @author Julb.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ShortMessageEntityMapper.class})
public interface ItemEntityMapper {

    // ------------------------------------------ Generic.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    default AbstractItemDTO map(AbstractItemEntity entity) {
        switch(entity.getType()) {
            case EXTERNAL_LINK:
                return map((ExternalLinkEntity) entity);
            case FOLDER:
                return map((FolderEntity) entity);
            case OBJECT_LINK:
                return map((ObjectLinkEntity) entity);
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    default AbstractItemEntity map(AbstractItemCreationDTO creationDTO) {
        switch(creationDTO.getType()) {
            case EXTERNAL_LINK:
                return map((ExternalLinkCreationDTO) creationDTO);
            case FOLDER:
                return map((FolderCreationDTO) creationDTO);
            case OBJECT_LINK:
                return map((ObjectLinkCreationDTO) creationDTO);
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    default void map(AbstractItemUpdateDTO updateDto, @MappingTarget AbstractItemEntity entity) {
        switch(entity.getType()) {
            case EXTERNAL_LINK:
                map((ExternalLinkUpdateDTO) updateDto, (ExternalLinkEntity) entity);
                break;
            case FOLDER:
                map((FolderUpdateDTO) updateDto, (FolderEntity) entity);
                break;
            case OBJECT_LINK:
                map((ObjectLinkUpdateDTO) updateDto, (ObjectLinkEntity) entity);
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
    default void map(AbstractItemPatchDTO patchDto, @MappingTarget AbstractItemEntity entity) {
        switch(entity.getType()) {
            case EXTERNAL_LINK:
                map((ExternalLinkPatchDTO) patchDto, (ExternalLinkEntity) entity);
                break;
            case FOLDER:
                map((FolderPatchDTO) patchDto, (FolderEntity) entity);
                break;
            case OBJECT_LINK:
                map((ObjectLinkPatchDTO) patchDto, (ObjectLinkEntity) entity);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    // ------------------------------------------ External links.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    ExternalLinkDTO map(ExternalLinkEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    ExternalLinkEntity map(ExternalLinkCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(ExternalLinkUpdateDTO updateDto, @MappingTarget ExternalLinkEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(ExternalLinkPatchDTO patchDto, @MappingTarget ExternalLinkEntity entity);

    // ------------------------------------------ Folders.

    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */ 
    FolderDTO map(FolderEntity entity);

    /**
     * Maps the entity ref to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    IdentifierDTO mapId(FolderEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    FolderEntity map(FolderCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(FolderUpdateDTO updateDto, @MappingTarget FolderEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(FolderPatchDTO patchDto, @MappingTarget FolderEntity entity);

    // ------------------------------------------ Object links.
    
    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    ObjectLinkDTO map(ObjectLinkEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    ObjectLinkEntity map(ObjectLinkCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(ObjectLinkUpdateDTO updateDto, @MappingTarget ObjectLinkEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(ObjectLinkPatchDTO patchDto, @MappingTarget ObjectLinkEntity entity);
}
