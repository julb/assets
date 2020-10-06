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

package me.julb.applications.disclaimer.controllers;

import io.swagger.v3.oas.annotations.Operation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.disclaimer.services.MyAgreementService;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementCreationDTO;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;
import me.julb.springbootstarter.web.utility.HttpServletRequestUtility;

/**
 * The rest controller to manage the connected user agreements.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/agreements", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyAgreementController {

    /**
     * The agreement service.
     */
    @Autowired
    private MyAgreementService myAgreementService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the agreements.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the agreements paged list.
     */
    @Operation(summary = "list agreements of the connected user")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<AgreementDTO> findAll(Searchable searchable, Pageable pageable) {
        return myAgreementService.findAll(searchable, pageable);
    }

    /**
     * Finds a agreement by its ID.
     * @param disclaimerId the ID of the disclaimer to fetch.
     * @return the agreement fetched.
     */
    @Operation(summary = "gets the agreement to a disclaimer for the connected user")
    @GetMapping(path = "/{disclaimerId}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public AgreementDTO get(@PathVariable("disclaimerId") @Identifier String disclaimerId) {
        return myAgreementService.findOne(disclaimerId);
    }

    // ------------------------------------------ Write methods.

    /**
     * Creates a agreement.
     * @param disclaimerId the ID of the disclaimer to fetch.
     * @param httpServletRequest the HTTP servlet request.
     * @return the created agreement.
     */
    @Operation(summary = "signify the agreement to a disclaimer for the connected user")
    @PostMapping(path = "/{disclaimerId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public AgreementDTO create(@PathVariable("disclaimerId") @Identifier String disclaimerId, HttpServletRequest httpServletRequest) {
        AgreementCreationDTO creationDTO = new AgreementCreationDTO();
        creationDTO.setIpv4Address(HttpServletRequestUtility.getUserIpv4Address(httpServletRequest));
        return myAgreementService.create(disclaimerId, creationDTO);
    }

    // ------------------------------------------ Overridden methods.
}
