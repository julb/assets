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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.dto.simple.content.LargeContentCreationDTO;
import me.julb.library.dto.simple.moneyamount.MoneyAmountDTO;
import me.julb.library.utility.validator.constraints.Tag;

/**
 * The DTO used to create an electronic purse operation.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ElectronicPurseOperationCreationDTO {

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
    @Schema(description = "Operation type on the electronic purse", required = true)
    @NotNull
    private ElectronicPurseOperationType type;

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
    * The localizedMessage attribute.
    * -- GETTER --
    * Getter for {@link #localizedMessage} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #localizedMessage} property.
    * @param localizedMessage the value to set.
    */
    //@formatter:on
    @Schema(description = "Localized message for the electronic purse operation")
    @Valid
    private Map<String, LargeContentCreationDTO> localizedMessage;

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
    @Schema(description = "Flag to send notification related to this operation", required = true)
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
    @Schema(description = "Tags to associate to the operation")
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();
}
