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

import io.julb.applications.platformhealth.services.dto.incident.IncidentHistoryCreationDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentHistoryDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentHistoryPatchDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentHistoryUpdateDTO;
import io.julb.applications.platformhealth.services.dto.incident.IncidentStatus;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import io.julb.library.persistence.mongodb.entities.message.LargeMessageEntity;
import io.julb.library.persistence.mongodb.entities.user.UserEntity;
import io.julb.library.utility.interfaces.IIdentifiable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.library.utility.validator.constraints.Trademark;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The incident history entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = IncidentHistoryCreationDTO.class, patch = IncidentHistoryPatchDTO.class, read = IncidentHistoryDTO.class, update = IncidentHistoryUpdateDTO.class)
@Document("incidents-history")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
public class IncidentHistoryEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The localizedMessage attribute.
     * -- GETTER --
     * Getter for {@link #localizedMessage} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedMessage} property.
     * @param localizedMessage the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private Map<String, LargeMessageEntity> localizedMessage = new HashMap<String, LargeMessageEntity>();

    //@formatter:off
     /**
     * The user attribute.
     * -- GETTER --
     * Getter for {@link #user} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #user} property.
     * @param user the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private UserEntity user;

    //@formatter:off
     /**
     * The previousStatus attribute.
     * -- GETTER --
     * Getter for {@link #previousStatus} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #previousStatus} property.
     * @param previousStatus the value to set.
     */
     //@formatter:on
    @NotNull
    private IncidentStatus previousStatus;

    //@formatter:off
     /**
     * The status attribute.
     * -- GETTER --
     * Getter for {@link #status} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #status} property.
     * @param status the value to set.
     */
     //@formatter:on
    @NotNull
    private IncidentStatus status;

    //@formatter:off
     /**
     * The sendNotification attribute.
     * -- GETTER --
     * Getter for {@link #sendNotification} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #sendNotification} property.
     * @param sendNotification the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean sendNotification;
}
