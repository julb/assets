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

package me.julb.applications.ewallet.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationCreationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationType;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.library.mapping.annotations.ObjectMappingFactory;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.message.LargeMessageEntity;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Tag;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The electronic purse operation.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = ElectronicPurseOperationCreationDTO.class, patch = ElectronicPurseOperationPatchDTO.class, read = ElectronicPurseOperationDTO.class, update = ElectronicPurseOperationUpdateDTO.class)
@Document("electronic-purses-operations")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
public class ElectronicPurseOperationEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The electronicPurse attribute.
     * -- GETTER --
     * Getter for {@link #electronicPurse} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #electronicPurse} property.
     * @param electronicPurse the value to set.
     */
     //@formatter:on
    @NotNull
    @DBRef
    private ElectronicPurseEntity electronicPurse;

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
    private UserRefEntity user;

    //@formatter:off
     /**
     * The amountInCts attribute.
     * -- GETTER --
     * Getter for {@link #amountInCts} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #amountInCts} property.
     * @param amountInCts the value to set.
     */
     //@formatter:on
    @NotNull
    @Min(0)
    private Long amountInCts;

    //@formatter:off
     /**
     * The currency attribute.
     * -- GETTER --
     * Getter for {@link #currency} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #currency} property.
     * @param currency the value to set.
     */
     //@formatter:on
    @NotNull
    private ISO4217Currency currency;

    //@formatter:off
     /**
     * The executionDateTime attribute.
     * -- GETTER --
     * Getter for {@link #executionDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #executionDateTime} property.
     * @param executionDateTime the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    @DateTimeISO8601
    private String executionDateTime;

    //@formatter:off
     /**
     * The type attribute.
     * -- GETTER --
     * Getter for {@link #type} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #type} property.
     * @param type the value to set.
     */
     //@formatter:on
    @NotNull
    private ElectronicPurseOperationType type;

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
