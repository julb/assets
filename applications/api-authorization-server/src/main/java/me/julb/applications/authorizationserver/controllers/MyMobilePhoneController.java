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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.MyMobilePhoneService;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhonePatchDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneVerifyDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage my mobile phones.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/mobile-phones", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyMobilePhoneController {

    /**
     * The my mobile phone service.
     */
    @Autowired
    private MyMobilePhoneService myMobilePhoneService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the mobile phones.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the mobile phone paged list.
     */
    @Operation(summary = "list my mobile phones")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<UserMobilePhoneDTO> findAll(Searchable searchable, Pageable pageable) {
        return myMobilePhoneService.findAll(searchable, pageable);
    }

    /**
     * Finds my mobile phones by its ID.
     * @param id the ID of the mobile phone to fetch.
     * @return the mobile phone fetched.
     */
    @Operation(summary = "gets my mobile phones")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMobilePhoneDTO get(@PathVariable @Identifier String id) {
        return myMobilePhoneService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a mobile phone.
     * @param creationDTO the DTO to create the mobile phone.
     * @return the created mobile phone.
     */
    @Operation(summary = "creates my mobile phone")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMobilePhoneDTO create(@RequestBody @NotNull @Valid UserMobilePhoneCreationDTO creationDTO) {
        return myMobilePhoneService.create(creationDTO);
    }

    /**
     * Updates my mobile phones.
     * @param id the ID of the mobile phone to update.
     * @param updateDTO the DTO to update the mobile phone.
     * @return the response.
     */
    @Operation(summary = "updates my mobile phones")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMobilePhoneDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid UserMobilePhoneUpdateDTO updateDTO) {
        return myMobilePhoneService.update(id, updateDTO);
    }

    /**
     * Patches my mobile phones.
     * @param id the ID of the mobile phone to patch.
     * @param patchDTO the DTO to patch the mobile phone.
     * @return the response.
     */
    @Operation(summary = "patches my mobile phones")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMobilePhoneDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid UserMobilePhonePatchDTO patchDTO) {
        return myMobilePhoneService.patch(id, patchDTO);
    }

    /**
     * Trigger the verification process of a mobile phone.
     * @param id the user mobile phone ID.
     * @return the updated mobile phone DTO.
     */
    @Operation(summary = "verifies the mobile phone of the user")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/{id}/trigger-verify")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserMobilePhoneDTO triggerVerify(@PathVariable("id") @Identifier String id) {
        return myMobilePhoneService.triggerMobilePhoneVerify(id);
    }

    /**
     * Verifies a mobile phone.
     * @param id the user mobile phone ID.
     * @param verifyToken the token to verify the mobile phone.
     * @return the updated mobile phone DTO.
     */
    @Operation(summary = "verifies the mobile phone of the user")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/{id}/verify")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserMobilePhoneDTO verify(@PathVariable("id") @Identifier String id, @RequestParam("verifyToken") @NotNull @NotBlank @Size(min = 128, max = 128) String verifyToken) {
        UserMobilePhoneVerifyDTO dto = new UserMobilePhoneVerifyDTO();
        dto.setVerifyToken(verifyToken);
        return myMobilePhoneService.updateVerify(id, dto);
    }

    /**
     * Deletes my mobile phones.
     * @param id the id of the mobile phone to delete.
     */
    @Operation(summary = "deletes my mobile phones")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete(@PathVariable String id) {
        myMobilePhoneService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
