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

package me.julb.applications.bookmark.services.impl;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.bookmark.services.ItemService;
import me.julb.applications.bookmark.services.MyItemService;
import me.julb.applications.bookmark.services.dto.ItemType;
import me.julb.applications.bookmark.services.dto.item.AbstractItemCreationDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemPatchDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemUpdateDTO;
import me.julb.library.dto.simple.identifier.IdentifierDTO;
import me.julb.library.dto.simple.value.PositiveIntegerValueDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.security.services.ISecurityService;

/**
 * The my item service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MyItemServiceImpl implements MyItemService {

    /**
     * The item service.
     */
    @Autowired
    private ItemService itemService;

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
    public Page<? extends AbstractItemDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        String userId = securityService.getConnectedUserId();
        return itemService.findAll(userId, searchable, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AbstractItemDTO> findAllByParent(@Identifier String parentId) {
        String userId = securityService.getConnectedUserId();
        return itemService.findAllByParent(userId, parentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<? extends AbstractItemDTO> findAllByType(@NotNull ItemType type, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String userId = securityService.getConnectedUserId();
        return itemService.findAllByType(userId, type, searchable, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractItemDTO findOne(@NotNull @Identifier String id) {
        String userId = securityService.getConnectedUserId();
        return itemService.findOne(userId, id);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO create(@NotNull @Valid AbstractItemCreationDTO creationDTO) {
        String userId = securityService.getConnectedUserId();
        return itemService.create(userId, creationDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO update(@NotNull @Identifier String id, @NotNull @Valid AbstractItemUpdateDTO updateDTO) {
        String userId = securityService.getConnectedUserId();
        return itemService.update(userId, userId, updateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO updatePosition(@Identifier String id, @NotNull @Valid PositiveIntegerValueDTO updateDTO) {
        String userId = securityService.getConnectedUserId();
        return itemService.updatePosition(userId, id, updateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO updateParent(@Identifier String id, @Valid IdentifierDTO updateDTO) {
        String userId = securityService.getConnectedUserId();
        return itemService.updateParent(userId, id, updateDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO patch(@NotNull @Identifier String id, @NotNull @Valid AbstractItemPatchDTO patchDTO) {
        String userId = securityService.getConnectedUserId();
        return itemService.patch(userId, id, patchDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String id) {
        String userId = securityService.getConnectedUserId();
        itemService.delete(userId, id);
    }

    // ------------------------------------------ Private methods.
}
