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

package me.julb.applications.ewallet.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationKind;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationType;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.message.LargeMessageEntity;
import me.julb.library.persistence.mongodb.entities.moneyamount.MoneyAmountEntity;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Tag;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The electronic purse operation.
 * <br>
 * @author Julb.
 */
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
     * The amount attribute.
     * -- GETTER --
     * Getter for {@link #amount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #amount} property.
     * @param amount the value to set.
     */
     //@formatter:on
    @NotNull
    @Valid
    private MoneyAmountEntity amount;

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
     * Indicates the original operation in case the current refers to a previous one, such as a cancellation for instance.
     * -- GETTER --
     * Getter for {@link #originalOperation} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #originalOperation} property.
     * @param originalOperation the value to set.
     */
     //@formatter:on
    @DBRef
    private ElectronicPurseOperationEntity originalOperation;

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

    /**
     * Gets the operation kind.
     * @return the operation kind.
     */
    public ElectronicPurseOperationKind getKind() {
        if (type != null) {
            return type.kind();
        } else {
            return null;
        }
    }

    /**
     * Gets the amount as signed value.
     * <P>
     * Will be positive if the operation kind is {@link ElectronicPurseOperationKind#CREDIT}.
     * <P>
     * Will be negative if the operation kind is {@link ElectronicPurseOperationKind#DEBIT}.
     * <P>
     * @return the signed amount in cents.
     */
    public MoneyAmountEntity getSignedAmount() {
        if (type != null) {
            switch (type.kind()) {
                case DEBIT:
                    return this.amount.toNegative();
                case CREDIT:
                    return this.amount;
                default:
                    throw new UnsupportedOperationException();
            }
        } else {
            return null;
        }
    }
}
