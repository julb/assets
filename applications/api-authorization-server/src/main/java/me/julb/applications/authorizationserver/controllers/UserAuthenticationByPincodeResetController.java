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

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeTriggerPincodeResetDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationRecoveryChannelType;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.dto.simple.identifier.IdentifierDTO;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.library.utility.validator.constraints.SecurePincode;

/**
 * The rest controller to reset the pincode of a user.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAuthenticationByPincodeResetController {

    /**
     * The user mail service.
     */
    @Autowired
    private UserMailService userMailService;

    /**
     * The my authentication by pincode service.
     */
    @Autowired
    private UserAuthenticationByPincodeService userAuthenticationByPincodeService;

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * Trigger the reset process of a authentication by pincode.
     * @param mail the user mail.
     * @param recoveryChannelType the recovery channel type.
     * @param recoveryChannelDevice the recovery channel device identifier.
     */
    @Operation(summary = "triggers the reset of the authentication by pincode of the user")
    @PostMapping(path = "/users/authentications/type/pincode/trigger-reset", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("permitAll()")
    public void triggerPincodeReset(@RequestParam("mail") @NotNull @NotBlank @Email String mail, @RequestParam("recoveryChannelType") @NotNull UserAuthenticationRecoveryChannelType recoveryChannelType,
        @RequestParam("recoveryChannelDevice") @NotNull @NotBlank @Identifier String recoveryChannelDevice) {
        UserDTO user = userMailService.findUserByMailVerified(mail);
        if (user != null) {
            UserAuthenticationByPincodeTriggerPincodeResetDTO triggerPincodeResetDTO = new UserAuthenticationByPincodeTriggerPincodeResetDTO();
            triggerPincodeResetDTO.setRecoveryChannelType(recoveryChannelType);
            triggerPincodeResetDTO.setRecoveryChannelDevice(new IdentifierDTO(recoveryChannelDevice));
            userAuthenticationByPincodeService.triggerPincodeReset(user.getId(), triggerPincodeResetDTO);
        }
    }

    /**
     * Updates the pincode using reset token.
     * @param mail the mail.
     * @param newPincode the new pincode.
     * @param resetToken the reseet token.
     */
    @Operation(summary = "triggers the reset of the authentication by pincode of the user")
    @PostMapping(path = "/users/authentications/type/pincode/reset-pincode", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("permitAll()")
    public void resetPincode(@RequestParam("mail") @NotNull @NotBlank @Email String mail, @RequestParam("newPincode") @NotNull @NotBlank @SecurePincode String newPincode, @RequestParam("resetToken") @NotNull @NotBlank String resetToken) {
        UserDTO user = userMailService.findUserByMailVerified(mail);
        if (user != null) {
            UserAuthenticationByPincodePincodeResetDTO pincodeResetDTO = new UserAuthenticationByPincodePincodeResetDTO();
            pincodeResetDTO.setNewPincode(newPincode);
            pincodeResetDTO.setResetToken(resetToken);
            userAuthenticationByPincodeService.updatePincode(user.getId(), pincodeResetDTO);
        }
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
