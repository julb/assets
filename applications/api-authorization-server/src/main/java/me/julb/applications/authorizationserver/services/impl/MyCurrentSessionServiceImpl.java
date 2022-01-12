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

package me.julb.applications.authorizationserver.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.services.MyCurrentSessionService;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFromIdTokenCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenWithIdTokenDTO;
import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Mono;

/**
 * The user session service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyCurrentSessionServiceImpl implements MyCurrentSessionService {
    /**
     * The mapper.
     */
    @Autowired
    private UserSessionService userSessionService;

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AuthenticatedUserDTO> findCurrent() {
        return securityService.getConnectedUserIdentity();
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserSessionAccessTokenWithIdTokenDTO> createAccessToken(@NotNull @Valid UserSessionAccessTokenFromIdTokenCreationDTO accessTokenCreation) {
        return userSessionService.createAccessTokenFromIdToken(accessTokenCreation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> deleteCurrent() {
        return securityService.getConnectedUserIdentity().flatMap(connectedUser -> {
            return userSessionService.delete(connectedUser.getUserId(), connectedUser.getSessionId());
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.
}
