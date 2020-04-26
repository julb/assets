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

import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneCreationDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhonePatchDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneVerifyDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The user mobile phone service.
 * <P>
 * @author Julb.
 */
public interface MyMobilePhoneService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available mobile phones (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of mobile phones.
     */
    Page<UserMobilePhoneDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a mobile phone through its ID.
     * @param id the mobile phone identifier.
     * @return the mobile phone.
     */
    UserMobilePhoneDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Creates a mobile phone.
     * @param mobilePhoneCreationDTO the DTO to create a mobile phone.
     * @return the created mobile phone.
     */
    UserMobilePhoneDTO create(@NotNull @Valid UserMobilePhoneCreationDTO mobilePhoneCreationDTO);

    /**
     * Updates a mobile phone.
     * @param id the mobile phone identifier.
     * @param mobilePhoneUpdateDTO the DTO to update a mobile phone.
     * @return the updated mobile phone.
     */
    UserMobilePhoneDTO update(@NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneUpdateDTO mobilePhoneUpdateDTO);

    /**
     * Patches a mobile phone.
     * @param id the mobile phone identifier.
     * @param mobilePhonePatchDTO the DTO to update a mobile phone.
     * @return the updated mobile phone.
     */
    UserMobilePhoneDTO patch(@NotNull @Identifier String id, @NotNull @Valid UserMobilePhonePatchDTO mobilePhonePatchDTO);

    /**
     * Triggers a mobile phone verification process.
     * @param id the mobile phone identifier.
     * @return the updated mobile phone.
     */
    UserMobilePhoneDTO triggerMobilePhoneVerify(@NotNull @Identifier String id);

    /**
     * Verify the mobile phone.
     * @param id the mobile phone identifier.
     * @param verifyDTO the DTO to verify the mobile phone.
     * @return the updated mobile phone.
     */
    UserMobilePhoneDTO updateVerify(@NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneVerifyDTO verifyDTO);

    /**
     * Deletes a mobile phone.
     * @param id the id of the mobile phone to delete.
     */
    void delete(@NotNull @Identifier String id);

}
