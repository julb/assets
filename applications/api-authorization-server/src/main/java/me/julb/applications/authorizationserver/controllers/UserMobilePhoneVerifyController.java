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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.UserMobilePhoneService;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneVerifyDTO;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The rest controller to manage mobile phones.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserMobilePhoneVerifyController {

    /**
     * The signup service.
     */
    @Autowired
    private UserMobilePhoneService userMobilePhoneService;

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * Trigger the verification process of a mobile phone address.
     * @param userId the user ID.
     * @param userMobilePhoneId the user mobile phone ID.
     * @return the updated mobile phone DTO.
     */
    @Operation(summary = "triggers the verification the mobile phone of the user")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/users/{userId}/mobile-phones/{userMobilePhoneId}/trigger-verify")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserMobilePhoneDTO triggerVerify(@PathVariable("userId") @Identifier String userId, @PathVariable("userMobilePhoneId") @Identifier String userMobilePhoneId) {
        return userMobilePhoneService.triggerMobilePhoneVerify(userId, userMobilePhoneId);
    }

    /**
     * Verifies a mobile phone address.
     * @param userId the user ID.
     * @param userMobilePhoneId the user mail ID.
     * @param verifyToken the token to verify the mobile phone.
     * @return the updated mobile phone DTO.
     */
    @Operation(summary = "verifies the mobile phone of the user")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/users/{userId}/mobile-phones/{userMobilePhoneId}/verify")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserMobilePhoneDTO verify(@PathVariable("userId") @Identifier String userId, @PathVariable("userMobilePhoneId") @Identifier String userMobilePhoneId,
        @RequestParam("verifyToken") @NotNull @NotBlank @Size(min = 128, max = 128) String verifyToken) {
        UserMobilePhoneVerifyDTO dto = new UserMobilePhoneVerifyDTO();
        dto.setVerifyToken(verifyToken);
        return userMobilePhoneService.updateVerify(userId, userMobilePhoneId, dto);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
