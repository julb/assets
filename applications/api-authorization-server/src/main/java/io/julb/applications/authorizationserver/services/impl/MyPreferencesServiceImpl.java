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

package io.julb.applications.authorizationserver.services.impl;

import io.julb.applications.authorizationserver.services.MyPreferencesService;
import io.julb.applications.authorizationserver.services.UserPreferencesService;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesCreationDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesPatchDTO;
import io.julb.applications.authorizationserver.services.dto.preferences.UserPreferencesUpdateDTO;
import io.julb.springbootstarter.security.services.ISecurityService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The preferences service implementation.
 * <P>
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
    public UserPreferencesDTO findOne() {
        String userId = securityService.getConnectedUserId();
        return userPreferencesService.findOne(userId);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserPreferencesDTO create(@NotNull @Valid UserPreferencesCreationDTO userPreferencesCreationDTO) {
        String userId = securityService.getConnectedUserId();
        return userPreferencesService.create(userId, userPreferencesCreationDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserPreferencesDTO update(@NotNull @Valid UserPreferencesUpdateDTO userPreferencesUpdateDTO) {
        String userId = securityService.getConnectedUserId();
        return userPreferencesService.update(userId, userPreferencesUpdateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserPreferencesDTO patch(@NotNull @Valid UserPreferencesPatchDTO userPreferencesPatchDTO) {
        String userId = securityService.getConnectedUserId();
        return userPreferencesService.patch(userId, userPreferencesPatchDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.
}
