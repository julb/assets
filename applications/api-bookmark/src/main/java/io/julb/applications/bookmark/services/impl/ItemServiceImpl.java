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

package io.julb.applications.bookmark.services.impl;

import io.julb.applications.bookmark.entities.AbstractItemEntity;
import io.julb.applications.bookmark.entities.ExternalLinkEntity;
import io.julb.applications.bookmark.entities.FolderEntity;
import io.julb.applications.bookmark.entities.ObjectLinkEntity;
import io.julb.applications.bookmark.entities.comparators.ItemEntityByPositionComparator;
import io.julb.applications.bookmark.repositories.ExternalLinkRepository;
import io.julb.applications.bookmark.repositories.FolderRepository;
import io.julb.applications.bookmark.repositories.ItemRepository;
import io.julb.applications.bookmark.repositories.ObjectLinkRepository;
import io.julb.applications.bookmark.repositories.specifications.ItemBelongsToUserIdSpecification;
import io.julb.applications.bookmark.repositories.specifications.ItemOfGivenTypeSpecification;
import io.julb.applications.bookmark.services.ItemService;
import io.julb.applications.bookmark.services.dto.ItemType;
import io.julb.applications.bookmark.services.dto.item.AbstractItemCreationDTO;
import io.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import io.julb.applications.bookmark.services.dto.item.AbstractItemPatchDTO;
import io.julb.applications.bookmark.services.dto.item.AbstractItemUpdateDTO;
import io.julb.applications.bookmark.services.exceptions.CannotMoveFolderInSubfolderException;
import io.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import io.julb.library.dto.messaging.events.ResourceEventType;
import io.julb.library.dto.security.AuthenticatedUserIdentityDTO;
import io.julb.library.dto.simple.identifier.IdentifierDTO;
import io.julb.library.dto.simple.value.PositiveIntegerValueDTO;
import io.julb.library.persistence.mongodb.entities.user.UserEntity;
import io.julb.library.utility.constants.Chars;
import io.julb.library.utility.constants.Strings;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.messaging.builders.ResourceEventAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;
import io.julb.springbootstarter.resourcetypes.ResourceTypes;
import io.julb.springbootstarter.security.services.ISecurityService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The item service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ItemServiceImpl implements ItemService {

    /**
     * The item repository.
     */
    @Autowired
    private ItemRepository itemRepository;

    /**
     * The folder repository.
     */
    @Autowired
    private FolderRepository folderRepository;

    /**
     * The external link repository.
     */
    @Autowired
    private ExternalLinkRepository externalLinkRepository;

    /**
     * The object link repository.
     */
    @Autowired
    private ObjectLinkRepository objectLinkRepository;

    /**
     * The mapper.
     */
    @Autowired
    private IMappingService mappingService;

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    /**
     * The async message poster service.
     */
    @Autowired
    private IAsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<? extends AbstractItemDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<AbstractItemEntity> spec = new SearchSpecification<AbstractItemEntity>(searchable).and(new TmSpecification<>(tm)).and(new ItemBelongsToUserIdSpecification<>(userId));
        Page<AbstractItemEntity> result = itemRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AbstractItemDTO> findAllByParent(@NotNull @Identifier String userId, @Identifier String parentId) {
        String tm = TrademarkContextHolder.getTrademark();

        List<AbstractItemEntity> items;

        // Get folder parent.
        if (StringUtils.isNotBlank(parentId)) {
            FolderEntity folderParent = (FolderEntity) itemRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, ItemType.FOLDER, parentId);
            if (folderParent == null) {
                throw new ResourceNotFoundException(AbstractItemEntity.class, parentId);
            }

            // Return children.
            items = itemRepository.findByTmAndUser_IdAndParentOrderByPositionAsc(tm, userId, folderParent);
        } else {
            items = itemRepository.findByTmAndUser_IdAndParentIsNullOrderByPositionAsc(tm, userId);
        }
        return mappingService.mapAsList(items, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<? extends AbstractItemDTO> findAllByType(@NotNull @Identifier String userId, @NotNull ItemType type, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        Page<? extends AbstractItemEntity> result;

        switch (type) {
            case EXTERNAL_LINK:
                ISpecification<ExternalLinkEntity> externalLinkSpec =
                    new SearchSpecification<ExternalLinkEntity>(searchable).and(new ItemOfGivenTypeSpecification<>(type)).and(new TmSpecification<>(tm)).and(new ItemBelongsToUserIdSpecification<>(userId));
                result = externalLinkRepository.findAll(externalLinkSpec, pageable);
                break;
            case FOLDER:
                ISpecification<FolderEntity> folderSpec = new SearchSpecification<FolderEntity>(searchable).and(new ItemOfGivenTypeSpecification<>(type)).and(new TmSpecification<>(tm)).and(new ItemBelongsToUserIdSpecification<>(userId));
                result = folderRepository.findAll(folderSpec, pageable);
                break;
            case OBJECT_LINK:
                ISpecification<ObjectLinkEntity> objectEntitySpec =
                    new SearchSpecification<ObjectLinkEntity>(searchable).and(new ItemOfGivenTypeSpecification<>(type)).and(new TmSpecification<>(tm)).and(new ItemBelongsToUserIdSpecification<>(userId));
                result = objectLinkRepository.findAll(objectEntitySpec, pageable);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return mappingService.mapAsPage(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractItemDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        AbstractItemEntity result = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (result == null) {
            throw new ResourceNotFoundException(AbstractItemEntity.class, id);
        }

        return mappingService.map(result, AbstractItemDTO.class);
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO create(@NotNull @Identifier String userId, @NotNull @Valid AbstractItemCreationDTO creationDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check parent folder exists
        FolderEntity folderParent = null;
        Integer position = 0;
        String pathPrefix = Strings.EMPTY;
        if (creationDTO.getParent() != null && StringUtils.isNotBlank(creationDTO.getParent().getId())) {
            // Get folder parent.
            folderParent = (FolderEntity) itemRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, ItemType.FOLDER, creationDTO.getParent().getId());
            if (folderParent == null) {
                throw new ResourceNotFoundException(AbstractItemEntity.class, creationDTO.getParent().getId());
            }

            // Compute position.
            Integer greatestPosition = -1;
            AbstractItemEntity itemWithGreatestPosition = itemRepository.findTopByTmAndUser_IdAndParentOrderByPositionDesc(tm, userId, folderParent);
            if (itemWithGreatestPosition != null) {
                greatestPosition = Math.max(greatestPosition, itemWithGreatestPosition.getPosition());
            }

            // Increment position.
            position = greatestPosition + 1;

            // Compute path.
            pathPrefix = StringUtils.join(folderParent.getPath(), Chars.UNDERSCORE);
        } else {
            // Compute position.
            Integer greatestPosition = -1;

            AbstractItemEntity itemWithGreatestPosition = itemRepository.findTopByTmAndUser_IdAndParentIsNullOrderByPositionDesc(tm, userId);
            if (itemWithGreatestPosition != null) {
                greatestPosition = Math.max(greatestPosition, itemWithGreatestPosition.getPosition());
            }

            // Increment position.
            position = greatestPosition + 1;
        }

        // Update the entity
        AbstractItemEntity entityToCreate = mappingService.map(creationDTO, AbstractItemEntity.class);
        entityToCreate.setParent(folderParent);
        entityToCreate.setPosition(position);
        this.onPersist(entityToCreate);

        // Set path.
        entityToCreate.setPath(StringUtils.join(pathPrefix, entityToCreate.getId()));

        AbstractItemEntity result = itemRepository.save(entityToCreate);
        return mappingService.map(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid AbstractItemUpdateDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        AbstractItemEntity existing = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AbstractItemEntity.class, id);
        }

        // Update the entity
        mappingService.map(updateDTO, existing);
        this.onUpdate(existing);

        AbstractItemEntity result = itemRepository.save(existing);
        return mappingService.map(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO updatePosition(@NotNull @Identifier String userId, @Identifier String id, @NotNull @Valid PositiveIntegerValueDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        AbstractItemEntity existing = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AbstractItemEntity.class, id);
        }

        // Finds all items under the same parent.
        List<AbstractItemEntity> siblings;
        if (existing.getParent() == null) {
            siblings = new ArrayList<>(itemRepository.findByTmAndUser_IdAndParentIsNullOrderByPositionAsc(tm, userId));
        } else {
            siblings = new ArrayList<>(itemRepository.findByTmAndUser_IdAndParentOrderByPositionAsc(tm, userId, existing.getParent()));
        }

        // Update position of all elements in the hierarchy.
        for (AbstractItemEntity sibling : siblings) {
            if (sibling.equals(existing)) {
                // Set new position for given element.
                sibling.setPosition(updateDTO.getValue());
            } else if (sibling.getPosition() >= updateDTO.getValue()) {
                // Increment position of all others under this one.
                sibling.setPosition(sibling.getPosition() + 1);
            }
        }

        // Re-sort all items by position..
        Collections.sort(siblings, new ItemEntityByPositionComparator());

        // Rewrite all the position.
        for (int i = 0; i < siblings.size(); i++) {
            AbstractItemEntity sibling = siblings.get(i);

            // Update the position.
            sibling.setPosition(i);

            // Notify update.
            this.onUpdate(sibling);
        }

        // Update in DB.
        itemRepository.saveAll(siblings);

        // Return updated element.
        AbstractItemEntity result = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        return mappingService.map(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO updateParent(@NotNull @Identifier String userId, @Identifier String id, @Valid IdentifierDTO updateDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        AbstractItemEntity existing = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AbstractItemEntity.class, id);
        }

        // Check parent folder exists
        FolderEntity folderParent = null;
        Integer position = 0;
        String pathPrefix = Strings.EMPTY;
        if (updateDTO != null && StringUtils.isNotBlank(updateDTO.getId())) {
            // Get folder parent.
            folderParent = (FolderEntity) itemRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, ItemType.FOLDER, updateDTO.getId());
            if (folderParent == null) {
                throw new ResourceNotFoundException(AbstractItemEntity.class, updateDTO.getId());
            }

            // Compute position.
            Integer greatestPosition = -1;
            AbstractItemEntity itemWithGreatestPosition = itemRepository.findTopByTmAndUser_IdAndParentOrderByPositionDesc(tm, userId, folderParent);
            if (itemWithGreatestPosition != null) {
                greatestPosition = Math.max(greatestPosition, itemWithGreatestPosition.getPosition());
            }

            // Increment position.
            position = greatestPosition + 1;

            // Compute path.
            pathPrefix = StringUtils.join(folderParent.getPath(), Chars.UNDERSCORE);
        } else {
            // Compute position.
            Integer greatestPosition = -1;

            AbstractItemEntity itemWithGreatestPosition = itemRepository.findTopByTmAndUser_IdAndParentIsNullOrderByPositionDesc(tm, userId);
            if (itemWithGreatestPosition != null) {
                greatestPosition = Math.max(greatestPosition, itemWithGreatestPosition.getPosition());
            }

            // Increment position.
            position = greatestPosition + 1;
        }

        // Store old path and children path.
        FolderEntity oldFolderParent = existing.getParent();
        String oldPath = existing.getPath();
        String newPath = StringUtils.join(pathPrefix, existing.getId());

        // If parent are the same, nothing to do.
        if (StringUtils.equals(oldPath, newPath)) {
            return mappingService.map(existing, AbstractItemDTO.class);
        }

        // If new path under old path, it means that we want to put a parent within a child of this parent.
        if (newPath.startsWith(oldPath)) {
            throw new CannotMoveFolderInSubfolderException();
        }

        // Update the entity
        existing.setParent(folderParent);
        existing.setPosition(position);
        existing.setPath(newPath);
        this.onUpdate(existing);

        // Update all the children elements if folder
        if (ItemType.FOLDER.equals(existing.getType())) {
            List<AbstractItemEntity> items = itemRepository.findByTmAndUser_IdAndIdNotAndPathStartsWith(tm, userId, existing.getId(), oldPath);
            for (AbstractItemEntity item : items) {
                item.setPath(item.getPath().replaceFirst(oldPath, newPath));
                itemRepository.save(item);
                this.onUpdate(item);
            }
        }

        // Update all the position of the old folder.
        List<AbstractItemEntity> oldFolderChildren;
        if (oldFolderParent == null) {
            oldFolderChildren = new ArrayList<>(itemRepository.findByTmAndUser_IdAndIdNotAndParentIsNullOrderByPositionAsc(tm, userId, id));
        } else {
            oldFolderChildren = new ArrayList<>(itemRepository.findByTmAndUser_IdAndIdNotAndParentOrderByPositionAsc(tm, userId, id, oldFolderParent));
        }

        // Rewrite all the position.
        for (int i = 0; i < oldFolderChildren.size(); i++) {
            AbstractItemEntity oldFolderChild = oldFolderChildren.get(i);

            // Update the position.
            oldFolderChild.setPosition(i);

            // Notify update.
            this.onUpdate(oldFolderChild);
        }

        // Update in DB.
        itemRepository.saveAll(oldFolderChildren);

        // Return the result.
        AbstractItemEntity result = itemRepository.save(existing);
        return mappingService.map(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AbstractItemDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid AbstractItemPatchDTO patchDTO) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        AbstractItemEntity existing = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AbstractItemEntity.class, id);
        }

        // Update the entity
        mappingService.map(patchDTO, existing);
        this.onUpdate(existing);

        AbstractItemEntity result = itemRepository.save(existing);
        return mappingService.map(result, AbstractItemDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the item exists
        AbstractItemEntity existing = itemRepository.findByTmAndUser_IdAndId(tm, userId, id);
        if (existing == null) {
            throw new ResourceNotFoundException(AbstractItemEntity.class, id);
        }

        // Delete entity.
        itemRepository.delete(existing);

        // Handle deletion.
        this.onDelete(existing);

        // Delete items under this one.
        if (ItemType.FOLDER.equals(existing.getType())) {
            List<AbstractItemEntity> items = itemRepository.findByTmAndUser_IdAndIdNotAndPathStartsWith(tm, userId, existing.getId(), existing.getPath());
            for (AbstractItemEntity item : items) {
                itemRepository.delete(item);
                this.onDelete(item);
            }
        }
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param entity the entity.
     */
    private void onPersist(AbstractItemEntity entity) {
        entity.setId(IdentifierUtility.generateId());
        entity.setTm(TrademarkContextHolder.getTrademark());
        entity.setCreatedAt(DateUtility.dateTimeNow());
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());

        // Add author.
        AuthenticatedUserIdentityDTO connnectedUser = securityService.getConnectedUserIdentity();
        entity.setUser(new UserEntity());
        entity.getUser().setFirstName(connnectedUser.getFirstName());
        entity.getUser().setId(connnectedUser.getId());
        entity.getUser().setLastName(connnectedUser.getLastName());
        entity.getUser().setMail(connnectedUser.getMail());

        postResourceEvent(entity, ResourceEventType.CREATED);
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private void onUpdate(AbstractItemEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private void onDelete(AbstractItemEntity entity) {
        postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private void postResourceEvent(AbstractItemEntity entity, ResourceEventType resourceEventType) {
        //@formatter:off
        ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
            .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.BOOMARK_ITEM)
            .eventType(resourceEventType)
            .user(securityService.getConnectedUserName())
            .build();
        //@formatter:on

        this.asyncMessagePosterService.postResourceEventMessage(resourceEvent);
    }
}
