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

import io.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import io.julb.applications.authorizationserver.services.dto.profile.UserProfileDTO;
import io.julb.applications.authorizationserver.services.dto.profile.UserProfilePatchDTO;
import io.julb.applications.authorizationserver.services.dto.profile.UserProfileUpdateDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The user profile service.
 * <P>
 * @author Julb.
 */
public interface MyProfileService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a user profile.
     * @return the user profile.
     */
    UserProfileDTO findOne();

    // ------------------------------------------ Write methods.

    /**
     * Creates a user profile.
     * @param userProfileCreationDTO the DTO to create a user profile.
     * @return the created user profile.
     */
    UserProfileDTO create(@NotNull @Valid UserProfileCreationDTO userProfileCreationDTO);

    /**
     * Updates a user profile.
     * @param userProfileUpdateDTO the DTO to update a user profile.
     * @return the updated user profile.
     */
    UserProfileDTO update(@NotNull @Valid UserProfileUpdateDTO userProfileUpdateDTO);

    /**
     * Patches a user profile.
     * @param userProfilePatchDTO the DTO to update a user profile.
     * @return the updated user profile.
     */
    UserProfileDTO patch(@NotNull @Valid UserProfilePatchDTO userProfilePatchDTO);
}
