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

package me.julb.applications.ewallet.services.dto.moneyvoucher;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.BooleanUtils;

import me.julb.library.dto.simple.audit.AbstractAuditedDTO;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.enums.ISO4217Currency;

/**
 * The DTO used to return a money voucher.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class MoneyVoucherDTO extends AbstractAuditedDTO {

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
    @Schema(description = "Unique ID for the money voucher")
    private String id;

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
    @Schema(description = "The amount in cents of the money voucher")
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
    @Schema(description = "The currency of the money voucher")
    private ISO4217Currency currency;

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
    @Schema(description = "The expiration of the money voucher")
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
    @Schema(description = "The flag indicating if the money voucher is enabled or not")
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
    @Schema(description = "The flag indicating if the money voucher is redeemed or not")
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
    @Schema(description = "User who has redeemed the money voucher")
    private UserRefDTO redeemedBy;

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
    @Schema(description = "Datetime at which the money voucher has been redeemed")
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
    @Schema(description = "Author of the money voucher")
    private UserRefDTO user;

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
    @Schema(description = "Tags to associate to the money voucher")
    private SortedSet<String> tags = new TreeSet<String>();

    /**
     * Returns <code>true</code> if the voucher can be redeemed, <code>false</code> otherwise.
     * @return <code>true</code> if the voucher can be redeemed, <code>false</code> otherwise.
     */
    public Boolean getRedeemable() {
        return BooleanUtils.isTrue(enabled) && BooleanUtils.isFalse(redeemed) && (expiryDateTime == null || DateUtility.dateTimeAfterNow(expiryDateTime));
    }
}
