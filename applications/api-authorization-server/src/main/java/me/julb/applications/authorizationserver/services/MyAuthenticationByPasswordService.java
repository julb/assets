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

package me.julb.applications.authorizationserver.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPasswordChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordUpdateDTO;

/**
 * The password user authentication service.
 * <P>
 * @author Julb.
 */
public interface MyAuthenticationByPasswordService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a password authentication through the user ID.
     * @return the authentication.
     */
    UserAuthenticationByPasswordDTO findOne();

    // ------------------------------------------ Write methods.

    /**
     * Creates a password authentication.
     * @param authenticationCreationDTO the DTO to create a password authentication.
     * @return the created authentication.
     */
    UserAuthenticationByPasswordDTO create(@NotNull @Valid UserAuthenticationByPasswordCreationDTO authenticationCreationDTO);

    /**
     * Updates a password authentication.
     * @param authenticationUpdateDTO the DTO to update a password authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO update(@NotNull @Valid UserAuthenticationByPasswordUpdateDTO authenticationUpdateDTO);

    /**
     * Patches a password authentication.
     * @param authenticationPatchDTO the DTO to update a password authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO patch(@NotNull @Valid UserAuthenticationByPasswordPatchDTO authenticationPatchDTO);

    /**
     * Changes the password.
     * @param changePasswordDTO the change password.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO updatePassword(@NotNull @Valid UserAuthenticationByPasswordPasswordChangeDTO changePasswordDTO);

    /**
     * Deletes a password authentication.
     */
    void delete();

}
