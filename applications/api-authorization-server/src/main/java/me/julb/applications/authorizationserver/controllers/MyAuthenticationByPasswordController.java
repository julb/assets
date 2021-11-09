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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.MyAuthenticationByPasswordService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPasswordChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordUpdateDTO;

/**
 * The rest controller to manage my authentication by passwords.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/authentications/type/password", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyAuthenticationByPasswordController {

    /**
     * The my authentication by password service.
     */
    @Autowired
    private MyAuthenticationByPasswordService myAuthenticationByPasswordService;

    // ------------------------------------------ Read methods.
    /**
     * Finds my authentication by passwords by its ID.
     * @return the authentication fetched.
     */
    @Operation(summary = "gets my authentication by password")
    @GetMapping()
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPasswordDTO get() {
        return myAuthenticationByPasswordService.findOne();
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an authentication.
     * @param creationDTO the DTO to create the authentication.
     * @return the created authentication.
     */
    @Operation(summary = "creates my authentication by password")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPasswordDTO create(@RequestBody @NotNull @Valid UserAuthenticationByPasswordCreationDTO creationDTO) {
        return myAuthenticationByPasswordService.create(creationDTO);
    }

    /**
     * Updates my authentication by passwords.
     * @param updateDTO the DTO to update the authentication.
     * @return the response.
     */
    @Operation(summary = "updates my authentication by passwords")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPasswordDTO update(@RequestBody @NotNull @Valid UserAuthenticationByPasswordUpdateDTO updateDTO) {
        return myAuthenticationByPasswordService.update(updateDTO);
    }

    /**
     * Patches my authentication by password.
     * @param patchDTO the DTO to patch the authentication.
     * @return the response.
     */
    @Operation(summary = "patches my authentication by password")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPasswordDTO patch(@RequestBody @NotNull @Valid UserAuthenticationByPasswordPatchDTO patchDTO) {
        return myAuthenticationByPasswordService.patch(patchDTO);
    }

    /**
     * Updates the authentication by password.
     * @param passwordChangeDTO the password change DTO.
     * @return the updated authentication by password DTO.
     */
    @Operation(summary = "updates the authentication by password of the user")
    @PostMapping(path = "/update-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserAuthenticationByPasswordDTO updatePassword(@RequestBody @NotNull @Valid UserAuthenticationByPasswordPasswordChangeDTO passwordChangeDTO) {
        return myAuthenticationByPasswordService.updatePassword(passwordChangeDTO);
    }

    /**
     * Deletes my authentication by password.
     */
    @Operation(summary = "deletes my authentication by password")
    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete() {
        myAuthenticationByPasswordService.delete();
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
