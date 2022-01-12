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

import me.julb.applications.authorizationserver.services.MyAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePincodeChangeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeUpdateDTO;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Mono;

/**
 * The user authentication service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyAuthenticationByPincodeServiceImpl implements MyAuthenticationByPincodeService {
    /**
     * The service.
     */
    @Autowired
    private UserAuthenticationByPincodeService userAuthenticationByPincodeService;

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
    public Mono<UserAuthenticationByPincodeDTO> findOne() {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByPincodeService.findOne(userId);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPincodeDTO> create(@NotNull @Valid UserAuthenticationByPincodeCreationDTO creationDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByPincodeService.create(userId, creationDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPincodeDTO> update(@NotNull @Valid UserAuthenticationByPincodeUpdateDTO updateDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByPincodeService.update(userId, updateDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPincodeDTO> patch(@NotNull @Valid UserAuthenticationByPincodePatchDTO patchDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByPincodeService.patch(userId, patchDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserAuthenticationByPincodeDTO> updatePincode(@NotNull @Valid UserAuthenticationByPincodePincodeChangeDTO changePincodeDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByPincodeService.updatePincode(userId, changePincodeDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete() {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userAuthenticationByPincodeService.delete(userId);
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
