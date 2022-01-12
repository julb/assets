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

import me.julb.applications.authorizationserver.services.MyPreferencesService;
import me.julb.applications.authorizationserver.services.UserPreferencesService;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesPatchDTO;
import me.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesUpdateDTO;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Mono;

/**
 * The preferences service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyPreferencesServiceImpl implements MyPreferencesService {
    /**
     * The preferences service.
     */
    @Autowired
    private UserPreferencesService userPreferencesService;

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
    public Mono<UserPreferencesDTO> findOne() {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userPreferencesService.findOne(userId);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserPreferencesDTO> create(@NotNull @Valid UserPreferencesCreationDTO userPreferencesCreationDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userPreferencesService.create(userId, userPreferencesCreationDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserPreferencesDTO> update(@NotNull @Valid UserPreferencesUpdateDTO userPreferencesUpdateDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userPreferencesService.update(userId, userPreferencesUpdateDTO);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<UserPreferencesDTO> patch(@NotNull @Valid UserPreferencesPatchDTO userPreferencesPatchDTO) {
        return securityService.getConnectedUserId().flatMap(userId -> {
            return userPreferencesService.patch(userId, userPreferencesPatchDTO);
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.
}
