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

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherCreationDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherPatchDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherUpdateDTO;
import me.julb.library.mapping.annotations.ObjectMappingFactory;
import me.julb.library.persistence.mongodb.entities.AbstractAuditedEntity;
import me.julb.library.persistence.mongodb.entities.user.UserRefEntity;
import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.interfaces.IIdentifiable;
import me.julb.library.utility.validator.constraints.DateTimeISO8601;
import me.julb.library.utility.validator.constraints.DateTimeInFuture;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.Tag;
import me.julb.library.utility.validator.constraints.Trademark;

/**
 * The announcement entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = MoneyVoucherCreationDTO.class, patch = MoneyVoucherPatchDTO.class, read = MoneyVoucherDTO.class, update = MoneyVoucherUpdateDTO.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = "id")
@NoArgsConstructor
@Document("money-vouchers")
public class MoneyVoucherEntity extends AbstractAuditedEntity implements IIdentifiable {

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
     * The securedCode attribute.
     * -- GETTER --
     * Getter for {@link #securedCode} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #securedCode} property.
     * @param securedCode the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Size(max = 128)
    private String securedCode;

    //@formatter:off
     /**
     * The code hash to prevent any duplication.
     * -- GETTER --
     * Getter for {@link #hash} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #hash} property.
     * @param hash the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Size(max = 128)
    private String hash;

    //@formatter:off
     /**
     * The expiryDateTime attribute.
     * -- GETTER --
     * Getter for {@link #expiryDateTime} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #expiryDateTime} property.
     * @param expiryDateTime the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    @DateTimeInFuture
    private String expiryDateTime;

    //@formatter:off
     /**
     * The enabled attribute.
     * -- GETTER --
     * Getter for {@link #enabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #enabled} property.
     * @param enabled the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean enabled;

    //@formatter:off
     /**
     * The redeemed attribute.
     * -- GETTER --
     * Getter for {@link #redeemed} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #redeemed} property.
     * @param redeemed the value to set.
     */
     //@formatter:on
    @NotNull
    private Boolean redeemed;

    //@formatter:off
     /**
     * The redeemedBy attribute.
     * -- GETTER --
     * Getter for {@link #redeemedBy} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #redeemedBy} property.
     * @param redeemedBy the value to set.
     */
     //@formatter:on
    @Valid
    private UserRefEntity redeemedBy;

    //@formatter:off
     /**
     * The redeemedAt attribute.
     * -- GETTER --
     * Getter for {@link #redeemedAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #redeemedAt} property.
     * @param redeemedAt the value to set.
     */
     //@formatter:on
    @DateTimeISO8601
    private String redeemedAt;

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
