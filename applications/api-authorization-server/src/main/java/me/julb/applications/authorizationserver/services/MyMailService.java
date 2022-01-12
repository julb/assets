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

import me.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailPatchDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailVerifyDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The user mail service.
 * <br>
 * @author Julb.
 */
public interface MyMailService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available mails (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of mails.
     */
    Flux<UserMailDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a mail through its ID.
     * @param id the mail identifier.
     * @return the mail.
     */
    Mono<UserMailDTO> findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a mail.
     * @param mailCreationDTO the DTO to create a mail.
     * @return the created mail.
     */
    Mono<UserMailDTO> create(@NotNull @Valid UserMailCreationDTO mailCreationDTO);

    /**
     * Updates a mail.
     * @param id the mail identifier.
     * @param mailUpdateDTO the DTO to update a mail.
     * @return the updated mail.
     */
    Mono<UserMailDTO> update(@NotNull @Identifier String id, @NotNull @Valid UserMailUpdateDTO mailUpdateDTO);

    /**
     * Patches a mail.
     * @param id the mail identifier.
     * @param mailPatchDTO the DTO to update a mail.
     * @return the updated mail.
     */
    Mono<UserMailDTO> patch(@NotNull @Identifier String id, @NotNull @Valid UserMailPatchDTO mailPatchDTO);

    /**
     * Triggers a mail verification process.
     * @param id the mail identifier.
     * @return the updated mail.
     */
    Mono<UserMailDTO> triggerMailVerify(@NotNull @Identifier String id);

    /**
     * Verify the mail.
     * @param id the mail identifier.
     * @param verifyDTO the DTO to verify the mail.
     * @return the updated mail.
     */
    Mono<UserMailDTO> updateVerify(@NotNull @Identifier String id, @NotNull @Valid UserMailVerifyDTO verifyDTO);

    /**
     * Deletes a mail.
     * @param id the id of the mail to delete.
     */
    Mono<Void> delete(@NotNull @Identifier String id);

}
