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

package io.julb.applications.authorizationserver.services;

import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyPatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyWithRawKeyDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The API Key authentication service.
 * <P>
 * @author Julb.
 */
public interface MyAuthenticationByApiKeyService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available authentications (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of authentications.
     */
    Page<UserAuthenticationByApiKeyDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets an API Key authentication through its ID.
     * @param id the authentication identifier.
     * @return the authentication.
     */
    UserAuthenticationByApiKeyDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates an API Key authentication.
     * @param authenticationCreationDTO the DTO to create an API Key authentication.
     * @return the created authentication.
     */
    UserAuthenticationByApiKeyWithRawKeyDTO create(@NotNull @Valid UserAuthenticationByApiKeyCreationDTO authenticationCreationDTO);

    /**
     * Updates an API Key authentication.
     * @param id the authentication identifier.
     * @param authenticationUpdateDTO the DTO to update an API Key authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByApiKeyDTO update(@NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByApiKeyUpdateDTO authenticationUpdateDTO);

    /**
     * Patches an API Key authentication.
     * @param id the authentication identifier.
     * @param authenticationPatchDTO the DTO to update an API Key authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByApiKeyDTO patch(@NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByApiKeyPatchDTO authenticationPatchDTO);

    /**
     * Deletes an API Key authentication.
     * @param id the id of the authentication to delete.
     */
    void delete(@NotNull @Identifier String id);

}
