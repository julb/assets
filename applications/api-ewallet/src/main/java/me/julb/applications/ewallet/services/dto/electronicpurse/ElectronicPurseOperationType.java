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

package me.julb.applications.ewallet.services.dto.electronicpurse;

/**
 * The electronic purse operation type.
 * <br>
 * @author Julb.
 */
public enum ElectronicPurseOperationType {

    /**
     * Credits the purse with a money voucher
     */
    CREDIT_MONEY_VOUCHER_REDEMPTION(ElectronicPurseOperationKind.CREDIT),

    /**
     * Credits the purse by cancelling a debit operation.
     */
    CREDIT_DEBIT_OPERATION_CANCELLED(ElectronicPurseOperationKind.CREDIT),

    /**
     * Debits the purse by cancelling a credit operation.
     */
    DEBIT_CREDIT_OPERATION_CANCELLED(ElectronicPurseOperationKind.DEBIT);

    /**
     * The operation kind.
     */
    private ElectronicPurseOperationKind kind;

    /**
     * Default constructor.
     * @param kind the operation kind.
     */
    private ElectronicPurseOperationType(ElectronicPurseOperationKind kind) {
        this.kind = kind;
    }

    /**
     * The operation kind.
     * @return the corresponding operation kind.
     */
    public ElectronicPurseOperationKind kind() {
        return this.kind;
    }

}
