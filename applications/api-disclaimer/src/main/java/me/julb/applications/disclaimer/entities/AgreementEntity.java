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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.Identifier;
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
@Document("agreements")
public class AgreementEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The disclaimer attribute.
     * -- GETTER --
     * Getter for {@link #disclaimer} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #disclaimer} property.
     * @param disclaimer the value to set.
     */
     //@formatter:on
    @NotNull
    @DBRef
    private DisclaimerEntity disclaimer;

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
     * The agreedAt attribute.
     * -- GETTER --
     * Getter for {@link #agreedAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #agreedAt} property.
     * @param agreedAt the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @DateTimeISO8601
    private String agreedAt;

    //@formatter:off
     /**
     * The ipv4Address attribute.
     * -- GETTER --
     * Getter for {@link #ipv4Address} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #ipv4Address} property.
     * @param ipv4Address the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String ipv4Address;
}
