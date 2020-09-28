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

import io.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailPatchDTO;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailVerifyDTO;
import io.julb.applications.authorizationserver.services.dto.user.UserDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The user mail service.
 * <P>
 * @author Julb.
 */
public interface UserMailService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available mails (paged).
     * @param userId the user identifier.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of mails.
     */
    Page<UserMailDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a mail through its ID.
     * @param userId the user identifier.
     * @param id the mail identifier.
     * @return the mail.
     */
    UserMailDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id);

    /**
     * Checks existence of this user by address.
     * @param mail the mail address.
     * @return the user mail.
     */
    boolean existsByMail(@NotNull @NotBlank @Email String mail);

    /**
     * Finds the mail by address.
     * @param mail the mail address.
     * @return the user mail.
     */
    UserMailDTO findByMail(@NotNull @NotBlank @Email String mail);

    /**
     * Gets a mail by address.
     * @param mail the mail address.
     * @return the user mail.
     */
    UserMailDTO findByMailVerified(@NotNull @NotBlank @Email String mail);

    /**
     * Gets a user by address.
     * @param mail the mail address.
     * @return the user.
     */
    UserDTO findUserByMailVerified(@NotNull @NotBlank @Email String mail);

    // ------------------------------------------ Write methods.

    /**
     * Creates a mail.
     * @param userId the user identifier.
     * @param mailCreationDTO the DTO to create a mail.
     * @return the created mail.
     */
    UserMailDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserMailCreationDTO mailCreationDTO);

    /**
     * Updates a mail.
     * @param userId the user identifier.
     * @param id the mail identifier.
     * @param mailUpdateDTO the DTO to update a mail.
     * @return the updated mail.
     */
    UserMailDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailUpdateDTO mailUpdateDTO);

    /**
     * Patches a mail.
     * @param userId the user identifier.
     * @param id the mail identifier.
     * @param mailPatchDTO the DTO to update a mail.
     * @return the updated mail.
     */
    UserMailDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailPatchDTO mailPatchDTO);

    /**
     * Triggers a mail verification process.
     * @param userId the user identifier.
     * @param id the mail identifier.
     * @return the updated mail.
     */
    UserMailDTO triggerMailVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id);

    /**
     * Verify the mail.
     * @param userId the user identifier.
     * @param id the mail identifier.
     * @param verifyDTO the DTO to verify the mail.
     * @return the updated mail.
     */
    UserMailDTO updateVerify(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserMailVerifyDTO verifyDTO);

    /**
     * Verify the mail without token check. Should be reserved for administrators..
     * @param userId the user identifier.
     * @param id the mail identifier.
     * @return the updated mail.
     */
    UserMailDTO updateVerifyWithoutToken(@NotNull @Identifier String userId, @NotNull @Identifier String id);

    /**
     * Deletes a mail.
     * @param userId the user identifier.
     * @param id the id of the mail to delete.
     */
    void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id);
}
