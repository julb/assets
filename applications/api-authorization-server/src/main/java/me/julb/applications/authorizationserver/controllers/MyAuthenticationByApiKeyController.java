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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.authorizationserver.services.MyAuthenticationByApiKeyService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByApiKeyWithRawKeyDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage my authentication by api-keys.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/authentications/type/api-key", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyAuthenticationByApiKeyController {

    /**
     * The my authentication by api-key service.
     */
    @Autowired
    private MyAuthenticationByApiKeyService myAuthenticationByApiKeyService;

    // ------------------------------------------ Read methods.

    /**
     * Lists my authentications by api-key.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the uthentications by api-key paged list.
     */
    @Operation(summary = "list my authentications by api-key")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<UserAuthenticationByApiKeyDTO> findAll(Searchable searchable, Pageable pageable) {
        return myAuthenticationByApiKeyService.findAll(searchable, pageable);
    }

    /**
     * Finds my authentication by api-keys by its ID.
     * @param id the ID of the authentication to fetch.
     * @return the authentication fetched.
     */
    @Operation(summary = "gets my authentication by api-key")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByApiKeyDTO get(@PathVariable @Identifier String id) {
        return myAuthenticationByApiKeyService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates an authentication.
     * @param creationDTO the DTO to create the authentication.
     * @return the created authentication.
     */
    @Operation(summary = "creates my authentication by api-key")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByApiKeyWithRawKeyDTO create(@RequestBody @NotNull @Valid UserAuthenticationByApiKeyCreationDTO creationDTO) {
        return myAuthenticationByApiKeyService.create(creationDTO);
    }

    /**
     * Updates my authentication by api-keys.
     * @param id the ID of the authentication to update.
     * @param updateDTO the DTO to update the authentication.
     * @return the response.
     */
    @Operation(summary = "updates my authentication by api-key")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByApiKeyDTO update(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid UserAuthenticationByApiKeyUpdateDTO updateDTO) {
        return myAuthenticationByApiKeyService.update(id, updateDTO);
    }

    /**
     * Patches my authentication by api-key.
     * @param id the ID of the authentication to patch.
     * @param patchDTO the DTO to patch the authentication.
     * @return the response.
     */
    @Operation(summary = "patches my authentication by api-key")
    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserAuthenticationByApiKeyDTO patch(@PathVariable @Identifier String id, @RequestBody @NotNull @Valid UserAuthenticationByApiKeyPatchDTO patchDTO) {
        return myAuthenticationByApiKeyService.patch(id, patchDTO);
    }

    /**
     * Deletes my authentication by api-key.
     * @param id the id of the authentication to delete.
     */
    @Operation(summary = "deletes my authentication by api-key")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete(@PathVariable String id) {
        myAuthenticationByApiKeyService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
