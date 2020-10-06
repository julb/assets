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

package me.julb.applications.urlshortener.controllers;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.urlshortener.repositories.LinkRepository;
import me.julb.applications.urlshortener.services.HostService;
import me.julb.library.dto.simple.value.ValueDTO;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.exceptions.ServiceUnavailableException;
import me.julb.library.utility.validator.constraints.DNS;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The rest controller to return hosts.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/hosts", produces = MediaType.APPLICATION_JSON_VALUE)
public class HostController {

    /**
     * The host service.
     */
    @Autowired
    private HostService hostService;

    /**
     * The link repository.
     */
    @Autowired
    private LinkRepository linkRepository;

    // ------------------------------------------ Read methods.

    /**
     * Lists the hosts eligible to URL shortener.
     * @return the hosts.
     */
    @Operation(summary = "list hosts eligible to URL shorten")
    @GetMapping
    @PreAuthorize("hasPermission('host', 'read')")
    public List<ValueDTO> findAll() {
        return hostService.findAll();
    }

    /**
     * Generates a random URI for the given host.
     * @param host the host.
     * @return a random URI for the given host.
     */
    @Operation(summary = "generates a random uri for the given host")
    @PostMapping("/{host}/.random-uri")
    @PreAuthorize("hasPermission('host', 'read')")
    public ValueDTO generateRandomUri(@PathVariable("host") @DNS String host) {
        // Check if host exists.
        if (!hostService.exists(host)) {
            throw new ResourceNotFoundException(String.class, host);
        }

        // Generate a random URI.
        String tm = TrademarkContextHolder.getTrademark();

        int currentTry = 0;
        while (currentTry < Integers.EIGHT) {
            // Generate random URI.
            String randomUri = generateRandomUri();

            // Check it.
            if (!linkRepository.existsByTmAndHostIgnoreCaseAndUriIgnoreCase(tm, host, randomUri)) {
                return new ValueDTO(randomUri);
            }

            currentTry++;
        }

        // Return unavailable.
        throw new ServiceUnavailableException();
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Utility methods.

    /**
     * Generates a random URI.
     * @return the random URI.
     */
    private String generateRandomUri() {
        // Generate an URI.
        //@formatter:off
        return new RandomStringGenerator.Builder()
            .withinRange(new char[] {Chars.ZERO, Chars.NINE}, new char[] {Chars.A_LOWERCASE, Chars.Z_LOWERCASE})
            .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.ASCII_LOWERCASE_LETTERS)
            .build()
            .generate(Integers.FOUR, Integers.EIGHT);
        //@formatter:on
    }

    // ------------------------------------------ Overridden methods.
}
