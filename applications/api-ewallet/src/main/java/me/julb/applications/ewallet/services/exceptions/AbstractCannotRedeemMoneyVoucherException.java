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

import me.julb.library.utility.exceptions.BadRequestException;

/**
 * The exception is triggered when a voucher cannot be redeemed because the voucher has already been redeemed.
 * <P>
 * @author Julb.
 */
public abstract class AbstractCannotRedeemMoneyVoucherException extends BadRequestException {

    /**
     * The money voucher ID.
     */
    private String moneyVoucherId;

    /**
     * Default constructor.
     * @param moneyVoucherId the money voucher ID.
     */
    public AbstractCannotRedeemMoneyVoucherException(String moneyVoucherId) {
        super();
        this.moneyVoucherId = moneyVoucherId;
    }

    /**
     * Getter for property moneyVoucherId.
     * @return Value of property moneyVoucherId.
     */
    public String getMoneyVoucherId() {
        return moneyVoucherId;
    }
}
