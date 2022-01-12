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

package me.julb.applications.disclaimer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.disclaimer.services.UserAgreementService;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The rest controller to view user agreements.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/users/{userId}/agreements", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAgreementController {

    /**
     * The user agreement service.
     */
    @Autowired
    private UserAgreementService userAgreementService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the agreements of the user.
     * @param userId the user ID.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the agreements paged list.
     */
    @Operation(summary = "list agreements of the user")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasPermission('agreement', 'read')")
    public Flux<AgreementDTO> findAll(@PathVariable("userId") @Identifier String userId, Searchable searchable, Pageable pageable) {
        return userAgreementService.findAll(userId, searchable, pageable);
    }

    /**
     * Finds a agreement by its disclaimer ID.
     * @param userId the user ID.
     * @param disclaimerId the disclaimer ID.
     * @return the agreement fetched.
     */
    @Operation(summary = "gets the agreement to a disclaimer for the user")
    @GetMapping(path = "/{disclaimerId}")
    @PreAuthorize("hasPermission('agreement', 'read')")
    public Mono<AgreementDTO> get(@PathVariable("userId") @Identifier String userId, @PathVariable("disclaimerId") @Identifier String disclaimerId) {
        return userAgreementService.findOne(userId, disclaimerId);
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Overridden methods.
}
