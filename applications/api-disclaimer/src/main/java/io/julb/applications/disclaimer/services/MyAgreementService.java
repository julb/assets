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

package io.julb.applications.disclaimer.services;

import io.julb.applications.disclaimer.services.dto.agreement.AgreementCreationDTO;
import io.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The connected user agreement service.
 * <P>
 * @author Julb.
 */
public interface MyAgreementService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available agreements (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of agreements.
     */
    Page<AgreementDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a agreement through its disclaimer ID.
     * @param disclaimerId the disclaimer identifier.
     * @return the agreement.
     */
    AgreementDTO findOne(@NotNull @Identifier String disclaimerId);

    // ------------------------------------------ Write methods.

    /**
     * Creates a agreement.
     * @param disclaimerId the disclaimer identifier.
     * @param agreementCreationDTO the DTO to create a agreement.
     * @return the created agreement.
     */
    AgreementDTO create(@NotNull @Identifier String disclaimerId, @NotNull @Valid AgreementCreationDTO agreementCreationDTO);

}
