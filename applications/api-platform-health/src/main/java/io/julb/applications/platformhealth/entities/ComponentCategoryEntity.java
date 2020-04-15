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

package io.julb.applications.platformhealth.entities;

import io.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryCreationDTO;
import io.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryDTO;
import io.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryPatchDTO;
import io.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryUpdateDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import io.julb.library.persistence.mongodb.entities.message.LargeMessageEntity;
import io.julb.library.persistence.mongodb.entities.message.ShortMessageEntity;
import io.julb.library.utility.interfaces.IIdentifiable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.library.utility.validator.constraints.Trademark;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The component category entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = ComponentCategoryCreationDTO.class, patch = ComponentCategoryPatchDTO.class, read = ComponentCategoryDTO.class, update = ComponentCategoryUpdateDTO.class)
@Document("component-categories")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
public class ComponentCategoryEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The localizedName attribute.
     * -- GETTER --
     * Getter for {@link #localizedName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedName} property.
     * @param localizedName the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private Map<String, ShortMessageEntity> localizedName = new HashMap<String, ShortMessageEntity>();

    //@formatter:off
     /**
     * The localizedDescription attribute.
     * -- GETTER --
     * Getter for {@link #localizedDescription} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedDescription} property.
     * @param localizedDescription the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private Map<String, LargeMessageEntity> localizedDescription = new HashMap<String, LargeMessageEntity>();

    //@formatter:off
     /**
     * The position attribute.
     * -- GETTER --
     * Getter for {@link #position} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #position} property.
     * @param position the value to set.
     */
     //@formatter:on
    @NotNull
    @Min(0)
    private Integer position;

}
