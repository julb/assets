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

import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;

import reactor.core.publisher.Mono;

/**
 * The pincode user authentication service.
 * <br>
 * @author Julb.
 */
public interface MyAuthenticationByPincodeService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a pincode authentication through the user ID.
     * @return the authentication.
     */
    Mono<UserAuthenticationByPincodeDTO> findOne();

    // ------------------------------------------ Write methods.

    /**
     * Creates a pincode authentication.
     * @param authenticationCreationDTO the DTO to create a pincode authentication.
     * @return the created authentication.
     */
    Mono<UserAuthenticationByPincodeDTO> create(@NotNull @Valid UserAuthenticationByPincodeCreationDTO authenticationCreationDTO);

    /**
     * Updates a pincode authentication.
     * @param authenticationUpdateDTO the DTO to update a pincode authentication.
     * @return the updated authentication.
     */
    Mono<UserAuthenticationByPincodeDTO> update(@NotNull @Valid UserAuthenticationByPincodeUpdateDTO authenticationUpdateDTO);

    /**
     * Patches a pincode authentication.
     * @param authenticationPatchDTO the DTO to update a pincode authentication.
     * @return the updated authentication.
     */
    Mono<UserAuthenticationByPincodeDTO> patch(@NotNull @Valid UserAuthenticationByPincodePatchDTO authenticationPatchDTO);

    /**
     * Changes the pincode.
     * @param changePincodeDTO the change pincode.
     * @return the updated authentication.
     */
    Mono<UserAuthenticationByPincodeDTO> updatePincode(@NotNull @Valid UserAuthenticationByPincodePincodeChangeDTO changePincodeDTO);

    /**
     * Deletes a pincode authentication.
     */
    Mono<Void> delete();

}
