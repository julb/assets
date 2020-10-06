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

package me.julb.applications.platformhealth.entities;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import me.julb.applications.platformhealth.services.dto.incident.IncidentComponentImpactLevel;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentCreationDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentPatchDTO;
import me.julb.applications.platformhealth.services.dto.incidentcomponent.IncidentComponentUpdateDTO;
import me.julb.library.mapping.annotations.ObjectMappingFactory;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The incident component entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = IncidentComponentCreationDTO.class, patch = IncidentComponentPatchDTO.class, read = IncidentComponentDTO.class, update = IncidentComponentUpdateDTO.class)
@Document("incidents-components")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
public class IncidentComponentEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The incident attribute.
     * -- GETTER --
     * Getter for {@link #incident} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #incident} property.
     * @param incident the value to set.
     */
     //@formatter:on
    @NotNull
    @DBRef
    private IncidentEntity incident;

    //@formatter:off
     /**
     * The component attribute.
     * -- GETTER --
     * Getter for {@link #component} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #component} property.
     * @param component the value to set.
     */
     //@formatter:on
    @NotNull
    @DBRef
    private ComponentEntity component;

    //@formatter:off
     /**
     * The impactLevel attribute.
     * -- GETTER --
     * Getter for {@link #impactLevel} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #impactLevel} property.
     * @param impactLevel the value to set.
     */
     //@formatter:on
    @NotNull
    private IncidentComponentImpactLevel impactLevel;

}
