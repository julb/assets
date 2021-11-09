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

import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPasswordChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPasswordResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordTriggerPasswordResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The password user authentication service.
 * <br>
 * @author Julb.
 */
public interface UserAuthenticationByPasswordService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a password authentication through the user ID.
     * @param userId the user identifier.
     * @return the authentication.
     */
    UserAuthenticationByPasswordDTO findOne(@NotNull @Identifier String userId);

    /**
     * Gets the credentials of the user.
     * @param userId the user ID.
     * @return the DTO holding the credentials.
     */
    UserAuthenticationCredentialsDTO findOneCredentials(@NotNull @Identifier String userId);

    // ------------------------------------------ Write methods.

    /**
     * Creates a password authentication.
     * @param userId the user identifier.
     * @param authenticationCreationDTO the DTO to create a password authentication.
     * @return the created authentication.
     */
    UserAuthenticationByPasswordDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordCreationDTO authenticationCreationDTO);

    /**
     * Updates a password authentication.
     * @param userId the user identifier.
     * @param authenticationUpdateDTO the DTO to update a password authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO update(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordUpdateDTO authenticationUpdateDTO);

    /**
     * Patches a password authentication.
     * @param userId the user identifier.
     * @param authenticationPatchDTO the DTO to update a password authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO patch(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPatchDTO authenticationPatchDTO);

    /**
     * Triggers a password reset.
     * @param userId the user identifier.
     * @param triggerPasswordResetDTO the DTO to trigger a password reset.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO triggerPasswordReset(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordTriggerPasswordResetDTO triggerPasswordResetDTO);

    /**
     * Changes the password.
     * @param userId the user identifier.
     * @param changePasswordDTO the change password.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO updatePassword(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPasswordChangeDTO changePasswordDTO);

    /**
     * Changes the password with reset token.
     * @param userId the user identifier.
     * @param changePasswordDTO the change password.
     * @return the updated authentication.
     */
    UserAuthenticationByPasswordDTO updatePassword(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPasswordPasswordResetDTO changePasswordDTO);

    /**
     * Deletes a password authentication.
     * @param userId the user identifier.
     */
    void delete(@NotNull @Identifier String userId);

}
