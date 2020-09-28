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

package io.julb.applications.authorizationserver.controllers;

import io.julb.applications.authorizationserver.services.MyPreferencesService;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesPatchDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The rest controller to manage my preferences.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyPreferencesController {

    /**
     * The my preferences service.
     */
    @Autowired
    private MyPreferencesService myPreferencesService;

    // ------------------------------------------ Read methods.

    /**
     * Finds my preferences.
     * @return the my preferences fetched.
     */
    @Operation(summary = "gets my preferences")
    @GetMapping()
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserPreferencesDTO get() {
        return myPreferencesService.findOne();
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates my preferences.
     * @param creationDTO the DTO to create the my preferences.
     * @return the created my preferences.
     */
    @Operation(summary = "creates my preferences")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserPreferencesDTO create(@RequestBody @NotNull @Valid UserPreferencesCreationDTO creationDTO) {
        return myPreferencesService.create(creationDTO);
    }

    /**
     * Updates my preferences.
     * @param updateDTO the DTO to update the my preferences.
     * @return the response.
     */
    @Operation(summary = "updates my preferences")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserPreferencesDTO update(@RequestBody @NotNull @Valid UserPreferencesUpdateDTO updateDTO) {
        return myPreferencesService.update(updateDTO);
    }

    /**
     * Updates my preferences.
     * @param updateDTO the DTO to update the my preferences.
     * @return the response.
     */
    @Operation(summary = "patches my preferences")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserPreferencesDTO patch(@RequestBody @NotNull @Valid UserPreferencesPatchDTO updateDTO) {
        return myPreferencesService.patch(updateDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
