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

package me.julb.applications.ewallet.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.ewallet.entities.MoneyVoucherEntity;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The money voucher repository.
 * <br>
 * @author Julb.
 */
public interface MoneyVoucherRepository extends MongoRepository<MoneyVoucherEntity, String>, MongoSpecificationExecutor<MoneyVoucherEntity> {

    /**
     * Finds a money voucher by trademark and id.
     * @param tm the trademark.
     * @param id the id.
     * @return the money voucher, or <code>null</code> if not exists.
     */
    MoneyVoucherEntity findByTmAndId(String tm, String id);

    /**
     * Finds a money voucher by its hash.
     * @param tm the trademark.
     * @param hash the hash.
     * @return the money voucher, or <code>null</code> if not exists.
     */
    MoneyVoucherEntity findByTmAndHashIgnoreCase(String tm, String hash);

    /**
     * Checks if a money voucher hash already exists.
     * @param tm the trademark.
     * @param hash the hash.
     * @return <code>true</code> if a money voucher exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndHashIgnoreCase(String tm, String hash);

    /**
     * Finds the money vouchers which expiry date is less or equal than the given date.
     * @param dateTime the given date time.
     * @return the money vouchers.
     */
    List<MoneyVoucherEntity> findByExpiryDateTimeLessThanEqualOrderByTmAsc(String dateTime);

}
