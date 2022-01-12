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

package me.julb.applications.authorizationserver.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpWithRawSecretDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The TOTP authentication service.
 * <br>
 * @author Julb.
 */
public interface MyAuthenticationByTotpService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available authentications (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of authentications.
     */
    Flux<UserAuthenticationByTotpDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a totp authentication through its ID.
     * @param id the authentication identifier.
     * @return the authentication.
     */
    Mono<UserAuthenticationByTotpDTO> findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a totp authentication.
     * @param authenticationCreationDTO the DTO to create a totp authentication.
     * @return the created authentication.
     */
    Mono<UserAuthenticationByTotpWithRawSecretDTO> create(@NotNull @Valid UserAuthenticationByTotpCreationDTO authenticationCreationDTO);

    /**
     * Updates a totp authentication.
     * @param id the authentication identifier.
     * @param authenticationUpdateDTO the DTO to update a totp authentication.
     * @return the updated authentication.
     */
    Mono<UserAuthenticationByTotpDTO> update(@NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpUpdateDTO authenticationUpdateDTO);

    /**
     * Patches a totp authentication.
     * @param id the authentication identifier.
     * @param authenticationPatchDTO the DTO to update a totp authentication.
     * @return the updated authentication.
     */
    Mono<UserAuthenticationByTotpDTO> patch(@NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpPatchDTO authenticationPatchDTO);

    /**
     * Deletes a totp authentication.
     * @param id the id of the authentication to delete.
     */
    Mono<Void> delete(@NotNull @Identifier String id);

}
