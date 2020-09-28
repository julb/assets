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

package io.julb.applications.announcement.entities;

import io.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import io.julb.applications.announcement.services.dto.AnnouncementDTO;
import io.julb.applications.announcement.services.dto.AnnouncementLevel;
import io.julb.applications.announcement.services.dto.AnnouncementPatchDTO;
import io.julb.applications.announcement.services.dto.AnnouncementUpdateDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import io.julb.library.persistence.mongodb.entities.date.DateTimeIntervalEntity;
import io.julb.library.persistence.mongodb.entities.message.LargeMessageEntity;
import io.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import io.julb.library.utility.interfaces.IIdentifiable;
import io.julb.library.utility.validator.constraints.DateTimeInFuture;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.library.utility.validator.constraints.Tag;
import io.julb.library.utility.validator.constraints.Trademark;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The announcement entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = AnnouncementCreationDTO.class, patch = AnnouncementPatchDTO.class, read = AnnouncementDTO.class, update = AnnouncementUpdateDTO.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Document("announcements")
public class AnnouncementEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The visibilityDateTime attribute.
     * -- GETTER --
     * Getter for {@link #visibilityDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #visibilityDateTime} property.
     * @param visibilityDateTime the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    @DateTimeInFuture
    private DateTimeIntervalEntity visibilityDateTime;

    //@formatter:off
     /**
     * The level attribute.
     * -- GETTER --
     * Getter for {@link #level} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #level} property.
     * @param level the value to set.
     */
     //@formatter:on
    @NotNull
    private AnnouncementLevel level;

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
    private UserRefEntity user;

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
     * The tags attribute.
     * -- GETTER --
     * Getter for {@link #tags} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tags} property.
     * @param tags the value to set.
     */
     //@formatter:on
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();
}
