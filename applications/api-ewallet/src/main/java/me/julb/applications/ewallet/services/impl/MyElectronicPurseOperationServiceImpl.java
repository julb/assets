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

package me.julb.applications.ewallet.services.impl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ewallet.services.MyElectronicPurseOperationService;
import me.julb.applications.ewallet.services.UserElectronicPurseOperationService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The my electronic purse service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyElectronicPurseOperationServiceImpl implements MyElectronicPurseOperationService {

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private UserElectronicPurseOperationService userElectronicPurseOperationService;

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
    public Page<ElectronicPurseOperationDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String userId = securityService.getConnectedUserId();
        return userElectronicPurseOperationService.findAll(userId, searchable, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElectronicPurseOperationDTO findOne(@NotNull @Identifier String id) {
        String userId = securityService.getConnectedUserId();
        return userElectronicPurseOperationService.findOne(userId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO update(@NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationUpdateDTO updateDTO) {
        String userId = securityService.getConnectedUserId();
        return userElectronicPurseOperationService.update(userId, id, updateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO patch(@NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationPatchDTO patchDTO) {
        String userId = securityService.getConnectedUserId();
        return userElectronicPurseOperationService.patch(userId, id, patchDTO);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
