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

import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesPatchDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesUpdateDTO;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The user preferences service.
 * <br>
 * @author Julb.
 */
public interface UserPreferencesService {

    // ------------------------------------------ Read methods.

    /**
     * Gets a user preferences through its user ID.
     * @param userId the user identifier.
     * @return the user preferences.
     */
    UserPreferencesDTO findOne(@NotNull @Identifier String userId);

    // ------------------------------------------ Write methods.

    /**
     * Creates a user preferences.
     * @param userId the user identifier.
     * @param userPreferencesCreationDTO the DTO to create a user preferences.
     * @return the created user preferences.
     */
    UserPreferencesDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserPreferencesCreationDTO userPreferencesCreationDTO);

    /**
     * Updates a user preferences.
     * @param userId the user identifier.
     * @param userPreferencesUpdateDTO the DTO to update a user preferences.
     * @return the updated user preferences.
     */
    UserPreferencesDTO update(@NotNull @Identifier String userId, @NotNull @Valid UserPreferencesUpdateDTO userPreferencesUpdateDTO);

    /**
     * Patches a user preferences.
     * @param userId the user identifier.
     * @param userPreferencesPatchDTO the DTO to update a user preferences.
     * @return the updated user preferences.
     */
    UserPreferencesDTO patch(@NotNull @Identifier String userId, @NotNull @Valid UserPreferencesPatchDTO userPreferencesPatchDTO);

    /**
     * Deletes a user preferences.
     * @param userId the user identifier.
     */
    void delete(@NotNull @Identifier String userId);
}
