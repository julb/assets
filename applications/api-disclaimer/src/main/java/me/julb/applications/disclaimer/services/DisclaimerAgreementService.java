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

package me.julb.applications.disclaimer.services;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The disclaimer agreement service.
 * <br>
 * @author Julb.
 */
public interface DisclaimerAgreementService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available agreements (paged).
     * @param disclaimerId the disclaimer ID.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of agreements.
     */
    Page<AgreementDTO> findAll(@NotNull @Identifier String disclaimerId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a agreement through its disclaimer ID.
     * @param disclaimerId the disclaimer identifier.
     * @param userId the user ID.
     * @return the agreement.
     */
    AgreementDTO findOne(@NotNull @Identifier String disclaimerId, @NotNull @Identifier String userId);

    // ------------------------------------------ Write methods.

}
