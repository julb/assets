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

package me.julb.applications.ewallet.services.exceptions;

import me.julb.library.utility.enums.ISO4217Currency;

/**
 * The exception is triggered when a voucher cannot be redeemed because of currency mismatch.
 * <P>
 * @author Julb.
 */
public class MoneyVoucherCannotBeRedeemedCurrencyMismatch extends AbstractMoneyVoucherCannotBeRedeemedException {

    /**
     * The expected currency.
     */
    private ISO4217Currency moneyVoucherCurrency;

    /**
     * The electronic purse ID.
     */
    private String electronicPurseId;

    /**
     * The actual currency
     */
    private ISO4217Currency electronicPurseCurrency;

    /**
     * Default constructor.
     * @param moneyVoucherId the money voucher ID.
     * @param moneyVoucherCurrency the money voucher currency.
     * @param electronicPurseId the electronic purse identifier.
     * @param electronicPurseCurrency the electronic purse currency.
     */
    public MoneyVoucherCannotBeRedeemedCurrencyMismatch(String moneyVoucherId, ISO4217Currency moneyVoucherCurrency, String electronicPurseId, ISO4217Currency electronicPurseCurrency) {
        super(moneyVoucherId);
        this.moneyVoucherCurrency = moneyVoucherCurrency;
        this.electronicPurseId = electronicPurseId;
        this.electronicPurseCurrency = electronicPurseCurrency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getMessageArgs() {
        return new Object[] {this.getMoneyVoucherId(), this.moneyVoucherCurrency, this.electronicPurseId, this.electronicPurseCurrency};
    }
}
