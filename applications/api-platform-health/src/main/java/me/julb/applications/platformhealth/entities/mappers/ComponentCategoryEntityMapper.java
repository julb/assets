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

package me.julb.applications.platformhealth.entities.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import me.julb.applications.platformhealth.entities.ComponentCategoryEntity;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryCreationDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryPatchDTO;
import me.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryUpdateDTO;
import me.julb.springbootstarter.mapping.entities.message.mappers.LargeMessageEntityMapper;
import me.julb.springbootstarter.mapping.entities.message.mappers.ShortMessageEntityMapper;

/**
 * The component category entity mapper.
 * <br>
 * @author Julb.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ShortMessageEntityMapper.class, LargeMessageEntityMapper.class})
public interface ComponentCategoryEntityMapper {
    /**
     * Maps the entity to its DTO.
     * @param entity the entity.
     * @return the corresponding DTO.
     */
    ComponentCategoryDTO map(ComponentCategoryEntity entity);

    /**
     * Maps the creation DTO to its entity.
     * @param creationDTO the creation DTO.
     * @return the entity.
     */
    ComponentCategoryEntity map(ComponentCategoryCreationDTO creationDTO);

    /**
     * Maps the update DTO on an existing entity instance.
     * @param updateDto the update DTO.
     * @param entity the entity to update.
     */
    void map(ComponentCategoryUpdateDTO updateDto, @MappingTarget ComponentCategoryEntity entity);

    /**
     * Maps the patch DTO on an existing entity instance.
     * <P>Ignores <code>null</code> values</P>
     * @param patchDto the patch DTO.
     * @param entity the entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void map(ComponentCategoryPatchDTO patchDto, @MappingTarget ComponentCategoryEntity entity);
}
