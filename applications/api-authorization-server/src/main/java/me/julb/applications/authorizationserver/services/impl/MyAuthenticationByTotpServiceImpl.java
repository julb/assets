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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.services.MyAuthenticationByTotpService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByTotpService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpWithRawSecretDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The user authentication service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyAuthenticationByTotpServiceImpl implements MyAuthenticationByTotpService {
    /**
     * The service.
     */
    @Autowired
    private UserAuthenticationByTotpService userAuthenticationByTotpService;

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
    public Flux<UserAuthenticationByTotpDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return securityService.getConnectedUserId().flatMapMany(userId -> {
            return userAuthenticationByTotpService.findAll(userId, searchable, pageable);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserAuthenticationByTotpDTO> findOne(@NotNull @Identifier String id) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByTotpService.findOne(userId, id);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByTotpWithRawSecretDTO> create(@NotNull @Valid UserAuthenticationByTotpCreationDTO creationDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByTotpService.create(userId, creationDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByTotpDTO> update(@NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpUpdateDTO updateDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByTotpService.update(userId, id, updateDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByTotpDTO> patch(@NotNull @Identifier String id, @NotNull @Valid UserAuthenticationByTotpPatchDTO patchDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByTotpService.patch(userId, id, patchDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String id) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByTotpService.delete(userId, id);
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
