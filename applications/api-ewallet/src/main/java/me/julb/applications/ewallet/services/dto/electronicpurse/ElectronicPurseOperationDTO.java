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

package me.julb.applications.ewallet.services.dto.electronicpurse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.audit.AbstractAuditedDTO;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.validator.constraints.Tag;

/**
 * The DTO used to return an electronic purse operation.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ElectronicPurseOperationDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Unique ID for the operation")
    private String id;

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
    @Schema(description = "Localized message for the operation")
    private Map<String, LargeContentDTO> localizedMessage;

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
    @Schema(description = "Author of the operation")
    private UserRefDTO user;

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
    @Schema(description = "Amount in cents of the operation")
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
    @Schema(description = "Currency of the operation")
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
    @Schema(description = "Execution date time of the operation")
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
    @Schema(description = "Type of the operation")
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
    @Schema(description = "Flag to send notification related to this operation")
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
     * Gets the amount in cents as signed value.
     * <P>
     * Will be positive if the operation kind is {@link ElectronicPurseOperationKind#CREDIT}.
     * <P>
     * Will be negative if the operation kind is {@link ElectronicPurseOperationKind#DEBIT}.
     * <P>
     * @return the signed amount in cents.
     */
    public Long getSignedAmountInCts() {
        switch (type.kind()) {
            case DEBIT:
                return -this.amountInCts;
            case CREDIT:
                return this.amountInCts;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
