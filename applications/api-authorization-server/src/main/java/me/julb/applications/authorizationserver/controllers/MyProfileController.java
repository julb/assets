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

package me.julb.applications.authorizationserver.controllers;

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

import me.julb.applications.authorizationserver.services.MyProfileService;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfilePatchDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileUpdateDTO;

/**
 * The rest controller to manage my profile.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyProfileController {

    /**
     * The my profile service.
     */
    @Autowired
    private MyProfileService myProfileService;

    // ------------------------------------------ Read methods.

    /**
     * Finds my profile.
     * @return the my profile fetched.
     */
    @Operation(summary = "gets my profile")
    @GetMapping()
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserProfileDTO get() {
        return myProfileService.findOne();
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates my profile.
     * @param creationDTO the DTO to create the my profile.
     * @return the created my profile.
     */
    @Operation(summary = "creates my profile")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserProfileDTO create(@RequestBody @NotNull @Valid UserProfileCreationDTO creationDTO) {
        return myProfileService.create(creationDTO);
    }

    /**
     * Updates my profile.
     * @param updateDTO the DTO to update the my profile.
     * @return the response.
     */
    @Operation(summary = "updates my profile")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserProfileDTO update(@RequestBody @NotNull @Valid UserProfileUpdateDTO updateDTO) {
        return myProfileService.update(updateDTO);
    }

    /**
     * Updates my profile.
     * @param updateDTO the DTO to update the my profile.
     * @return the response.
     */
    @Operation(summary = "patches my profile")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserProfileDTO patch(@RequestBody @NotNull @Valid UserProfilePatchDTO updateDTO) {
        return myProfileService.patch(updateDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
