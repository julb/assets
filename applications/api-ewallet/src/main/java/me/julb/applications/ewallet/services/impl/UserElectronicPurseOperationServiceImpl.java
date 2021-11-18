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

import me.julb.applications.ewallet.services.ElectronicPurseOperationExecutionService;
import me.julb.applications.ewallet.services.ElectronicPurseOperationService;
import me.julb.applications.ewallet.services.ElectronicPurseService;
import me.julb.applications.ewallet.services.UserElectronicPurseOperationService;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationPatchDTO;
import me.julb.applications.ewallet.services.dto.electronicpurse.ElectronicPurseOperationUpdateDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The electronic purse service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserElectronicPurseOperationServiceImpl implements UserElectronicPurseOperationService {

    /**
     * The electronic purse service.
     */
    @Autowired
    private ElectronicPurseService electronicPurseService;

    /**
     * The electronic purse operation service.
     */
    @Autowired
    private ElectronicPurseOperationService electronicPurseOperationService;

    /**
     * The electronic purse operation execution service.
     */
    @Autowired
    private ElectronicPurseOperationExecutionService electronicPurseOperationExecutionService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ElectronicPurseOperationDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseOperationService.findAll(electronicPurse.getId(), searchable, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElectronicPurseOperationDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseOperationService.findOne(electronicPurse.getId(), id);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationUpdateDTO updateDTO) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseOperationService.update(electronicPurse.getId(), id, updateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ElectronicPurseOperationDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid ElectronicPurseOperationPatchDTO patchDTO) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        return electronicPurseOperationService.patch(electronicPurse.getId(), id, patchDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cancel(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        electronicPurseOperationExecutionService.cancelOperation(electronicPurse.getId(), id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        ElectronicPurseDTO electronicPurse = electronicPurseService.findByUserId(userId);
        electronicPurseOperationExecutionService.deleteOperationExecution(electronicPurse.getId(), id);
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
