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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import me.julb.applications.authorizationserver.services.MyCurrentSessionService;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFromIdTokenCreationDTO;
import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.BadRequestException;
import me.julb.library.utility.validator.constraints.SecureIdToken;
import me.julb.springbootstarter.web.reactive.utility.ServerHttpRequestUtility;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

/**
 * The rest controller to return current session.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/session", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyCurrentSessionController {

    /**
     * The my session service.
     */
    @Autowired
    private MyCurrentSessionService myCurrentSessionService;

    /**
     * The HTTP user access token service.
     */
    @Autowired
    private HttpUserAccessTokenService httpUserAccessTokenService;

    // ------------------------------------------ Read methods.

    /**
     * Gets the current session.
     * @return the authenticated user.
     */
    @Operation(summary = "get the current session")
    @GetMapping
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<AuthenticatedUserDTO> findMySession() {
        return myCurrentSessionService.findCurrent();
    }

    // ------------------------------------------ Write methods.

    /**
     * Returns the access token.
     * @param idTokenFromParam the ID token from param.
     * @param idTokenFromCookie the ID token from cookie.
     * @param exchange the exchange.
     */
    @Operation(summary = "gets an access token")
    @PostMapping(path = "/refresh")
    @PreAuthorize("permitAll()")
    public Mono<Void> refreshAccessToken(@RequestParam(value = "idToken", required = false) @SecureIdToken String idTokenFromParam, @CookieValue(value = "idToken", required = false) @SecureIdToken String idTokenFromCookie, ServerWebExchange exchange) {
        // Extract ID Token: param has precedence over cookie.
        String idToken;
        if (StringUtils.isNotBlank(idTokenFromParam)) {
            idToken = idTokenFromParam;
        } else if (StringUtils.isNotBlank(idTokenFromCookie)) {
            idToken = idTokenFromCookie;
        } else {
            // No token provided so reject the request.
            throw new BadRequestException();
        }

        //
        ServerHttpRequest request = exchange.getRequest();

        // Generate access token
        UserSessionAccessTokenFromIdTokenCreationDTO creationDTO = new UserSessionAccessTokenFromIdTokenCreationDTO();
        creationDTO.setBrowser(Objects.toString(ServerHttpRequestUtility.getBrowser(request)));
        creationDTO.setIpv4Address(ServerHttpRequestUtility.getUserIpAddress(request));
        creationDTO.setLastUseDateTime(DateUtility.dateTimeNow());
        creationDTO.setOperatingSystem(Objects.toString(ServerHttpRequestUtility.getOperatingSystem(request)));
        creationDTO.setRawIdToken(idToken);
        return myCurrentSessionService.createAccessToken(creationDTO).flatMap(createAccessToken -> {
            // Write response.
            return httpUserAccessTokenService.writeResponseWithIdToken(createAccessToken, exchange.getResponse());
        });        
    }

    /**
     * Deletes the current session.
     */
    @Operation(summary = "deletes the current session")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Mono<Void> deleteMySession() {
        return myCurrentSessionService.deleteCurrent();
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
