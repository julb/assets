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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.UserAccountRecoveryService;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.dto.recovery.RecoveryChannelDeviceDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The rest controller to recover the account.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAccountRecoveryController {

    /**
     * The user account recovery service.
     */
    @Autowired
    private UserAccountRecoveryService userAccountRecoveryService;

    /**
     * The user mail service.
     */
    @Autowired
    private UserMailService userMailService;

    // ------------------------------------------ Read methods.

    /**
     * Gets the list of devices to recover the account.
     * @param userId the user id.
     * @return the list of devices to recover the account.
     */
    @Operation(summary = "get the list of devices for the given to recover the account")
    @GetMapping(path = "/users/{userId}/recovery-devices", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PreAuthorize("permitAll()")
    public List<RecoveryChannelDeviceDTO> findAllByUserId(@PathVariable("userId") @Identifier String userId) {
        return userAccountRecoveryService.findAll(userId);
    }

    /**
     * Gets the list of devices to recover the account.
     * @param mail the user mail.
     * @return the list of devices to recover the account.
     */
    @Operation(summary = "get the list of devices to recover the account")
    @GetMapping(path = "/users/recovery-devices", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<RecoveryChannelDeviceDTO> findAllByMailAddress(@RequestParam("mail") @NotNull @NotBlank @Email String mail) {
        UserDTO user = userMailService.findUserByMailVerified(mail);
        if (user != null) {
            return userAccountRecoveryService.findAll(user.getId());
        } else {
            return new ArrayList<>();
        }
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
