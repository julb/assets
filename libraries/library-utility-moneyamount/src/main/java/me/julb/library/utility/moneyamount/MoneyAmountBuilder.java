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

package me.julb.library.utility.moneyamount;

import java.util.Objects;

import me.julb.library.dto.simple.moneyamount.MoneyAmountDTO;
import me.julb.library.utility.enums.ISO4217Currency;

/**
 * The money amount builder.
 * <P>
 * @author Julb.
 */
public class MoneyAmountBuilder {

    /**
     * The money amount.
     */
    private MoneyAmountDTO total;

    /**
     * Default constructor.
     * @param currency the currency.
     */
    public MoneyAmountBuilder(ISO4217Currency currency) {
        super();
        this.total = new MoneyAmountDTO(0L, currency);
    }

    // ------------------------------------------ Utility methods.

    /**
     * Adds the value to the total.
     * @param moneyAmount the money amount to add.
     */
    public void add(MoneyAmountDTO moneyAmount) {
        assertCurrencyValid(moneyAmount);
        this.total.setValue(this.total.getValue() + moneyAmount.getValue());
    }

    /**
     * Subtracts the value to the total.
     * @param moneyAmount the money amount to subtract.
     */
    public void subtract(MoneyAmountDTO moneyAmount) {
        assertCurrencyValid(moneyAmount);
        this.total.setValue(this.total.getValue() - moneyAmount.getValue());
    }

    /**
     * Gets the total.
     * @return the total.
     */
    public MoneyAmountDTO build() {
        return this.total;
    }

    /**
     * Asserts that the currency of the money amount matcches the total one.
     * @param moneyAmount the money amount.
     */
    private void assertCurrencyValid(MoneyAmountDTO moneyAmount) {
        if (!Objects.equals(this.total.getCurrency(), moneyAmount.getCurrency())) {
            throw new IllegalArgumentException(String.format("Currency : %s", moneyAmount.getCurrency()));
        }
    }
}
