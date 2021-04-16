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
import me.julb.library.dto.simple.moneyamount.MoneyAmountDTO;
import me.julb.library.dto.simple.user.UserRefDTO;
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
     * The amount attribute.
     * -- GETTER --
     * Getter for {@link #amount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #amount} property.
     * @param amount the value to set.
     */
     //@formatter:on
    @Schema(description = "The amount of the operation")
    private MoneyAmountDTO amount;

    //@formatter:off
     /**
     * The signedAmount attribute.
     * -- GETTER --
     * Getter for {@link #signedAmount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #signedAmount} property.
     * @param signedAmount the value to set.
     */
     //@formatter:on
    @Schema(description = "The signed amount of the operation")
    private MoneyAmountDTO signedAmount;

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
     * The kind attribute.
     * -- GETTER --
     * Getter for {@link #kind} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #kind} property.
     * @param kind the value to set.
     */
     //@formatter:on
    @Schema(description = "Kind of the operation")
    private ElectronicPurseOperationKind kind;

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
}
