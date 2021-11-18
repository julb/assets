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

package me.julb.applications.disclaimer.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.message.ShortMessageEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.Code;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Tag;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The disclaimer entity.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Document("disclaimers")
public class DisclaimerEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The code attribute.
     * -- GETTER --
     * Getter for {@link #code} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #code} property.
     * @param code the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Code
    private String code;

    //@formatter:off
     /**
     * The version attribute.
     * -- GETTER --
     * Getter for {@link #version} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #version} property.
     * @param version the value to set.
     */
     //@formatter:on
    @NotNull
    @Min(1)
    private Integer version;

    //@formatter:off
     /**
     * The frozen attribute.
     * -- GETTER --
     * Getter for {@link #frozen} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #frozen} property.
     * @param frozen the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean frozen;

    //@formatter:off
     /**
     * The active attribute.
     * -- GETTER --
     * Getter for {@link #active} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #active} property.
     * @param active the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean active;

    //@formatter:off
     /**
     * The activatedAt attribute.
     * -- GETTER --
     * Getter for {@link #activatedAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #activatedAt} property.
     * @param activatedAt the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String activatedAt;

    //@formatter:off
     /**
     * The localizedTitle attribute.
     * -- GETTER --
     * Getter for {@link #localizedTitle} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedTitle} property.
     * @param localizedTitle the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private Map<String, ShortMessageEntity> localizedTitle = new HashMap<String, ShortMessageEntity>();

    //@formatter:off
     /**
     * The localizedDocumentLibraryPath attribute.
     * -- GETTER --
     * Getter for {@link #localizedDocumentLibraryPath} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedDocumentLibraryPath} property.
     * @param localizedDocumentLibraryPath the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private Map<String, String> localizedDocumentLibraryPath = new HashMap<String, String>();

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
