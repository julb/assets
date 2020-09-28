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

package io.julb.applications.authorizationserver.controllers;

import io.julb.applications.authorizationserver.services.MySessionService;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import io.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The rest controller to manage my sessions.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class MySessionController {

    /**
     * The my session service.
     */
    @Autowired
    private MySessionService mySessionService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the sessions.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the session paged list.
     */
    @Operation(summary = "list sessions")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<UserSessionDTO> findAll(Searchable searchable, Pageable pageable) {
        return mySessionService.findAll(searchable, pageable);
    }

    /**
     * Finds a session by its ID.
     * @param id the ID of the session to fetch.
     * @return the session fetched.
     */
    @Operation(summary = "gets a session")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public UserSessionDTO get(@PathVariable @Identifier String id) {
        return mySessionService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Deletes all sessions.
     */
    @Operation(summary = "deletes all sessions")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete() {
        mySessionService.delete();
    }

    /**
     * Deletes a session.
     * @param id the id of the session to delete.
     */
    @Operation(summary = "deletes a session")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete(@PathVariable String id) {
        mySessionService.delete(id);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
