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

package me.julb.applications.ewallet.services;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherCreationDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherPatchDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherUpdateDTO;
import me.julb.applications.ewallet.services.dto.moneyvoucher.MoneyVoucherWithRawCodeDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.MoneyVoucherCode;

/**
 * The money voucher service.
 * <P>
 * @author Julb.
 */
public interface MoneyVoucherService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available money vouchers (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of money vouchers.
     */
    Page<MoneyVoucherDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a money voucher through its code.
     * @param code the money voucher code.
     * @return the money voucher.
     */
    MoneyVoucherDTO findByCode(@NotNull @NotBlank @MoneyVoucherCode String code);

    /**
     * Gets a money voucher through its ID.
     * @param id the money voucher identifier.
     * @return the money voucher.
     */
    MoneyVoucherDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a money voucher.
     * @param moneyVoucherCreationDTO the DTO to create a money voucher.
     * @return the created money voucher.
     */
    MoneyVoucherWithRawCodeDTO create(@NotNull @Valid MoneyVoucherCreationDTO moneyVoucherCreationDTO);

    /**
     * Redeems the money voucher.
     * @param id the money voucher identifier.
     * @return the updated money voucher.
     */
    MoneyVoucherDTO redeem(@NotNull @Identifier String id);

    /**
     * Updates a money voucher.
     * @param id the money voucher identifier.
     * @param moneyVoucherUpdateDTO the DTO to update a money voucher.
     * @return the updated money voucher.
     */
    MoneyVoucherDTO update(@NotNull @Identifier String id, @NotNull @Valid MoneyVoucherUpdateDTO moneyVoucherUpdateDTO);

    /**
     * Patches a money voucher.
     * @param id the money voucher identifier.
     * @param moneyVoucherPatchDTO the DTO to update a money voucher.
     * @return the updated money voucher.
     */
    MoneyVoucherDTO patch(@NotNull @Identifier String id, @NotNull @Valid MoneyVoucherPatchDTO moneyVoucherPatchDTO);

    /**
     * Deletes a money voucher.
     * @param id the id of the money voucher to delete.
     */
    void delete(@NotNull @Identifier String id);

}
