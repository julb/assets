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

import me.julb.applications.authorizationserver.services.MyMailService;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailPatchDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mail.UserMailVerifyDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage my mails.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/mails", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyMailController {

    /**
     * The my mail service.
     */
    @Autowired
    private MyMailService myMailService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the mails.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the mail paged list.
     */
    @Operation(summary = "list my mails")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<UserMailDTO> findAll(Searchable searchable, Pageable pageable) {
        return myMailService.findAll(searchable, pageable);
    }

    /**
     * Finds my mails by its ID.
     * @param id the ID of the mail to fetch.
     * @return the mail fetched.
     */
    @Operation(summary = "gets my mails")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMailDTO get(@PathVariable @Identifier String id) {
        return myMailService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an mail.
     * @param creationDTO the DTO to create the mail.
     * @return the created mail.
     */
    @Operation(summary = "creates my mail")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMailDTO create(@RequestBody @NotNull @Valid UserMailCreationDTO creationDTO) {
        return myMailService.create(creationDTO);
    }

    /**
     * Updates my mails.
     * @param id the ID of the mail to update.
     * @param updateDTO the DTO to update the mail.
     * @return the response.
     */
    @Operation(summary = "updates my mails")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMailDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid UserMailUpdateDTO updateDTO) {
        return myMailService.update(id, updateDTO);
    }

    /**
     * Patches my mails.
     * @param id the ID of the mail to patch.
     * @param patchDTO the DTO to patch the mail.
     * @return the response.
     */
    @Operation(summary = "patches my mails")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserMailDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid UserMailPatchDTO patchDTO) {
        return myMailService.patch(id, patchDTO);
    }

    /**
     * Trigger the verification process of a mail address.
     * @param id the user mail ID.
     * @return the updated mail DTO.
     */
    @Operation(summary = "verifies the email of the user")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/{id}/trigger-verify")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserMailDTO triggerVerify(@PathVariable("id") @Identifier String id) {
        return myMailService.triggerMailVerify(id);
    }

    /**
     * Verifies a mail address.
     * @param id the user mail ID.
     * @param verifyToken the token to verify the email address.
     * @return the updated mail DTO.
     */
    @Operation(summary = "verifies the email of the user")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/{id}/verify")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll()")
    public UserMailDTO verify(@PathVariable("id") @Identifier String id, @RequestParam("verifyToken") @NotNull @NotBlank @Size(min = 128, max = 128) String verifyToken) {
        UserMailVerifyDTO dto = new UserMailVerifyDTO();
        dto.setVerifyToken(verifyToken);
        return myMailService.updateVerify(id, dto);
    }

    /**
     * Deletes my mails.
     * @param id the id of the mail to delete.
     */
    @Operation(summary = "deletes my mails")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete(@PathVariable String id) {
        myMailService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
