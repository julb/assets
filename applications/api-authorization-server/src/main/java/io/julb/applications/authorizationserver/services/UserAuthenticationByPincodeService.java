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

import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeChangeDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeResetDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeTriggerPincodeResetDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The pincode user authentication service.
 * <P>
 * @author Julb.
 */
public interface UserAuthenticationByPincodeService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a pincode authentication through the user ID.
     * @param userId the user identifier.
     * @return the authentication.
     */
    UserAuthenticationByPincodeDTO findOne(@NotNull @Identifier String userId);

    /**
     * Gets the credentials of the user.
     * @param userId the user ID.
     * @return the DTO holding the credentials.
     */
    UserAuthenticationCredentialsDTO findOneCredentials(@NotNull @Identifier String userId);

    // ------------------------------------------ Write methods.

    /**
     * Creates a pincode authentication.
     * @param userId the user identifier.
     * @param authenticationCreationDTO the DTO to create a pincode authentication.
     * @return the created authentication.
     */
    UserAuthenticationByPincodeDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodeCreationDTO authenticationCreationDTO);

    /**
     * Updates a pincode authentication.
     * @param userId the user identifier.
     * @param authenticationUpdateDTO the DTO to update a pincode authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByPincodeDTO update(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodeUpdateDTO authenticationUpdateDTO);

    /**
     * Patches a pincode authentication.
     * @param userId the user identifier.
     * @param authenticationPatchDTO the DTO to update a pincode authentication.
     * @return the updated authentication.
     */
    UserAuthenticationByPincodeDTO patch(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodePatchDTO authenticationPatchDTO);

    /**
     * Triggers a pincode reset.
     * @param userId the user identifier.
     * @param triggerPincodeResetDTO the DTO to trigger a pincode reset.
     * @return the updated authentication.
     */
    UserAuthenticationByPincodeDTO triggerPincodeReset(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodeTriggerPincodeResetDTO triggerPincodeResetDTO);

    /**
     * Changes the pincode.
     * @param userId the user identifier.
     * @param changePincodeDTO the change pincode.
     * @return the updated authentication.
     */
    UserAuthenticationByPincodeDTO updatePincode(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodePincodeChangeDTO changePincodeDTO);

    /**
     * Changes the pincode with reset token.
     * @param userId the user identifier.
     * @param changePincodeDTO the change pincode.
     * @return the updated authentication.
     */
    UserAuthenticationByPincodeDTO updatePincode(@NotNull @Identifier String userId, @NotNull @Valid UserAuthenticationByPincodePincodeResetDTO changePincodeDTO);

    /**
     * Deletes a pincode authentication.
     * @param userId the user identifier.
     */
    void delete(@NotNull @Identifier String userId);

}
