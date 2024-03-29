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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.SignupService;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;

/**
 * The rest controller to handle sign-up.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignupController {

    /**
     * The signup service.
     */
    @Autowired
    private SignupService signupService;

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * Performs a sign-up using a password.
     * @param signupWithPasswordCreationDTO the DTO to sign-up.
     * @return the user created.
     */
    @Operation(summary = "sign-up using password")
    @PostMapping(path = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserDTO signupWithPassword(@RequestBody @NotNull @Valid SignupWithPasswordCreationDTO signupWithPasswordCreationDTO) {
        return signupService.signup(signupWithPasswordCreationDTO);
    }

    /**
     * Performs a sign-up using a pincode.
     * @param signupWithPincodeCreationDTO the DTO to sign-up.
     * @return the user created.
     */
    @Operation(summary = "sign-up using pincode")
    @PostMapping(path = "/pincode", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserDTO signupWithPincode(@RequestBody @NotNull @Valid SignupWithPincodeCreationDTO signupWithPincodeCreationDTO) {
        return signupService.signup(signupWithPincodeCreationDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
