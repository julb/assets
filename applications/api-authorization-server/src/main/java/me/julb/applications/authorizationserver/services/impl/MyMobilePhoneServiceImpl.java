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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.services.MyMobilePhoneService;
import me.julb.applications.authorizationserver.services.UserMobilePhoneService;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneCreationDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhonePatchDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneUpdateDTO;
import me.julb.applications.authorizationserver.services.dto.mobilephone.UserMobilePhoneVerifyDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The mobile phone service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyMobilePhoneServiceImpl implements MyMobilePhoneService {
    /**
     * The service.
     */
    @Autowired
    private UserMobilePhoneService userMobilePhoneService;

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
    public Page<UserMobilePhoneDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.findAll(userId, searchable, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMobilePhoneDTO findOne(@NotNull @Identifier String id) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.findOne(userId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO create(@NotNull @Valid UserMobilePhoneCreationDTO mailCreationDTO) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.create(userId, mailCreationDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO update(@NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneUpdateDTO mailUpdateDTO) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.update(userId, id, mailUpdateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO patch(@NotNull @Identifier String id, @NotNull @Valid UserMobilePhonePatchDTO mailPatchDTO) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.patch(userId, id, mailPatchDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO triggerMobilePhoneVerify(@NotNull @Identifier String id) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.triggerMobilePhoneVerify(userId, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserMobilePhoneDTO updateVerify(@NotNull @Identifier String id, @NotNull @Valid UserMobilePhoneVerifyDTO verifyDTO) {
        String userId = securityService.getConnectedUserId();
        return userMobilePhoneService.updateVerify(userId, id, verifyDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String userId = securityService.getConnectedUserId();
        userMobilePhoneService.delete(userId, id);
    }

}
