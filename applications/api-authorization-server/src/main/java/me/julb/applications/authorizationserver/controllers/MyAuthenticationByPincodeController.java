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

import me.julb.applications.authorizationserver.services.MyAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;

/**
 * The rest controller to manage my authentication by pincodes.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/authentications/type/pincode", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyAuthenticationByPincodeController {

    /**
     * The my authentication by pincode service.
     */
    @Autowired
    private MyAuthenticationByPincodeService myAuthenticationByPincodeService;

    // ------------------------------------------ Read methods.
    /**
     * Finds my authentication by pincodes by its ID.
     * @return the authentication fetched.
     */
    @Operation(summary = "gets my authentication by pincode")
    @GetMapping()
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPincodeDTO get() {
        return myAuthenticationByPincodeService.findOne();
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an authentication.
     * @param creationDTO the DTO to create the authentication.
     * @return the created authentication.
     */
    @Operation(summary = "creates my authentication by pincode")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPincodeDTO create(@RequestBody @NotNull @Valid UserAuthenticationByPincodeCreationDTO creationDTO) {
        return myAuthenticationByPincodeService.create(creationDTO);
    }

    /**
     * Updates my authentication by pincodes.
     * @param updateDTO the DTO to update the authentication.
     * @return the response.
     */
    @Operation(summary = "updates my authentication by pincodes")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPincodeDTO update(@RequestBody @NotNull @Valid UserAuthenticationByPincodeUpdateDTO updateDTO) {
        return myAuthenticationByPincodeService.update(updateDTO);
    }

    /**
     * Patches my authentication by pincode.
     * @param patchDTO the DTO to patch the authentication.
     * @return the response.
     */
    @Operation(summary = "patches my authentication by pincode")
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByPincodeDTO patch(@RequestBody @NotNull @Valid UserAuthenticationByPincodePatchDTO patchDTO) {
        return myAuthenticationByPincodeService.patch(patchDTO);
    }

    /**
     * Updates the authentication by pincode.
     * @param pincodeChangeDTO the pincode change DTO.
     * @return the updated authentication by pincode DTO.
     */
    @Operation(summary = "updates the authentication by pincode of the user")
    @PostMapping(path = "/update-pincode", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserAuthenticationByPincodeDTO updatePincode(@RequestBody @NotNull @Valid UserAuthenticationByPincodePincodeChangeDTO pincodeChangeDTO) {
        return myAuthenticationByPincodeService.updatePincode(pincodeChangeDTO);
    }

    /**
     * Deletes my authentication by pincode.
     */
    @Operation(summary = "deletes my authentication by pincode")
    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete() {
        myAuthenticationByPincodeService.delete();
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
