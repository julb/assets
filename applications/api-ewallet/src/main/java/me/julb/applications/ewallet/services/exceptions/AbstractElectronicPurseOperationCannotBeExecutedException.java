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

package me.julb.applications.ewallet.services.exceptions;

import me.julb.library.utility.enums.ISO4217Currency;
import me.julb.library.utility.exceptions.BadRequestException;

/**
 * The exception is triggered when a electronic purse operation cannot be executed.
 * <br>
 * @author Julb.
 */
public abstract class AbstractElectronicPurseOperationCannotBeExecutedException extends BadRequestException {

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
     * @param electronicPurseId the electronic purse ID.
     * @param electronicPurseCurrency the electronic purse currency.
     */
    public AbstractElectronicPurseOperationCannotBeExecutedException(String electronicPurseId, ISO4217Currency electronicPurseCurrency) {
        super();
        this.electronicPurseId = electronicPurseId;
        this.electronicPurseCurrency = electronicPurseCurrency;
    }

    /**
     * Getter for property electronicPurseId.
     * @return Value of property electronicPurseId.
     */
    public String getElectronicPurseId() {
        return electronicPurseId;
    }

    /**
     * Getter for property electronicPurseCurrency.
     * @return Value of property electronicPurseCurrency.
     */
    public ISO4217Currency getElectronicPurseCurrency() {
        return electronicPurseCurrency;
    }
}
