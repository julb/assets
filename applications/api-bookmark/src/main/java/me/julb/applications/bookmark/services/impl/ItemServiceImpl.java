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

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.bookmark.entities.AbstractItemEntity;
import me.julb.applications.bookmark.entities.ExternalLinkEntity;
import me.julb.applications.bookmark.entities.FolderEntity;
import me.julb.applications.bookmark.entities.ObjectLinkEntity;
import me.julb.applications.bookmark.entities.comparators.ItemEntityByPositionComparator;
import me.julb.applications.bookmark.entities.mappers.ItemEntityMapper;
import me.julb.applications.bookmark.repositories.ExternalLinkRepository;
import me.julb.applications.bookmark.repositories.FolderRepository;
import me.julb.applications.bookmark.repositories.ItemRepository;
import me.julb.applications.bookmark.repositories.ObjectLinkRepository;
import me.julb.applications.bookmark.repositories.specifications.ItemBelongsToUserIdSpecification;
import me.julb.applications.bookmark.repositories.specifications.ItemOfGivenTypeSpecification;
import me.julb.applications.bookmark.services.ItemService;
import me.julb.applications.bookmark.services.dto.ItemType;
import me.julb.applications.bookmark.services.dto.item.AbstractItemCreationDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemPatchDTO;
import me.julb.applications.bookmark.services.dto.item.AbstractItemUpdateDTO;
import me.julb.applications.bookmark.services.exceptions.CannotMoveFolderInSubfolderException;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.dto.simple.identifier.IdentifierDTO;
import me.julb.library.dto.simple.value.PositiveIntegerValueDTO;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.mapping.entities.user.mappers.UserRefEntityMapper;
import me.julb.springbootstarter.messaging.reactive.builders.ResourceEventAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.TmSpecification;
import me.julb.springbootstarter.resourcetypes.ResourceTypes;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The item service implementation.
 * <br>
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
    private ItemEntityMapper mapper;

    /**
     * The user ref mapper.
     */
    private UserRefEntityMapper userRefMapper;

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    /**
     * The async message poster service.
     */
    @Autowired
    private AsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<? extends AbstractItemDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<AbstractItemEntity> spec = new SearchSpecification<AbstractItemEntity>(searchable)
                .and(new TmSpecification<>(tm))
                .and(new ItemBelongsToUserIdSpecification<>(userId));
            return itemRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<? extends AbstractItemDTO> findAllByParent(@NotNull @Identifier String userId, @Identifier String parentId) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Get folder parent.
            if (StringUtils.isNotBlank(parentId)) {
                return itemRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, ItemType.FOLDER, parentId)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, parentId)))
                    .cast(FolderEntity.class)
                    .flatMapMany(folderParent -> {
                        return itemRepository.findByTmAndUser_IdAndParentOrderByPositionAsc(tm, userId, folderParent).map(mapper::map);
                    });
            } else {
                return itemRepository.findByTmAndUser_IdAndParentIsNullOrderByPositionAsc(tm, userId).map(mapper::map);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<? extends AbstractItemDTO> findAllByType(@NotNull @Identifier String userId, @NotNull ItemType type, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            Flux<? extends AbstractItemEntity> result;

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
            return result.map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AbstractItemDTO> findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the item exists
            return itemRepository.findByTmAndUser_IdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AbstractItemDTO> create(@NotNull @Identifier String userId, @NotNull @Valid AbstractItemCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check parent folder exists
            if (creationDTO.getParent() != null && StringUtils.isNotBlank(creationDTO.getParent().getId())) {
                return itemRepository.findByTmAndUser_IdAndTypeAndId(tm, userId, ItemType.FOLDER, creationDTO.getParent().getId())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, creationDTO.getParent().getId())))
                    .cast(FolderEntity.class)
                    .flatMap(folderParent -> {
                        String pathPrefix = StringUtils.join(folderParent.getPath(), Chars.UNDERSCORE);

                        return itemRepository.findTopByTmAndUser_IdAndParentOrderByPositionDesc(tm, userId, folderParent)
                            .map(itemWithGreatestPosition -> (itemWithGreatestPosition.getPosition() + 1))
                            .switchIfEmpty(Mono.just(0))
                            .flatMap(position -> {
                                // Update the entity
                                AbstractItemEntity entityToCreate = mapper.map(creationDTO);
                                entityToCreate.setParent(folderParent);
                                entityToCreate.setPosition(position);
                                return this.onPersist(tm, pathPrefix, entityToCreate).flatMap(entityToCreateWithFields -> {
                                    return itemRepository.save(entityToCreateWithFields).map(mapper::map);
                                });
                            });
                    });
            } else {
                return itemRepository.findTopByTmAndUser_IdAndParentIsNullOrderByPositionDesc(tm, userId)
                    .map(itemWithGreatestPosition -> (itemWithGreatestPosition.getPosition() + 1))
                    .switchIfEmpty(Mono.just(0))
                    .flatMap(position -> {
                        // Update the entity
                        AbstractItemEntity entityToCreate = mapper.map(creationDTO);
                        entityToCreate.setParent(null);
                        entityToCreate.setPosition(position);
                        return this.onPersist(tm, Strings.EMPTY, entityToCreate).flatMap(entityToCreateWithFields -> {
                            return itemRepository.save(entityToCreateWithFields).map(mapper::map);
                        });
                    });
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AbstractItemDTO> update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid AbstractItemUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return itemRepository.findByTmAndUser_IdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, id)))
                .flatMap(existing -> {
                    mapper.map(updateDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return itemRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AbstractItemDTO> updatePosition(@NotNull @Identifier String userId, @Identifier String id, @NotNull @Valid PositiveIntegerValueDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return itemRepository.findByTmAndUser_IdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, id)))
                .flatMap(existing -> {
                    // Finds all items under the same parent.
                    Flux<AbstractItemEntity> siblings;
                    if (existing.getParent() == null) {
                        siblings = itemRepository.findByTmAndUser_IdAndParentIsNullOrderByPositionAsc(tm, userId);
                    } else {
                        siblings = itemRepository.findByTmAndUser_IdAndParentOrderByPositionAsc(tm, userId, existing.getParent());
                    }

                    return siblings.doOnNext(sibling -> {
                        if (sibling.equals(existing)) {
                            // Set new position for given element.
                            sibling.setPosition(updateDTO.getValue());
                        } else if (sibling.getPosition() >= updateDTO.getValue()) {
                            // Increment position of all others under this one.
                            sibling.setPosition(sibling.getPosition() + 1);
                        }
                    })
                    .collectSortedList(new ItemEntityByPositionComparator())
                    .flatMapMany(sortedSiblings -> {
                        // Rewrite all the position.
                        for (int i = 0; i < sortedSiblings.size(); i++) {
                            AbstractItemEntity sibling = sortedSiblings.get(i);

                            // Update the position.
                            sibling.setPosition(i);
                        }

                        return Flux.fromIterable(sortedSiblings)
                                .flatMap(this::onUpdate)
                                .collectList()
                                .flatMapMany(itemRepository::saveAll);
                    })
                    .then(itemRepository.findByTmAndUser_IdAndId(tm, userId, id).map(mapper::map));
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AbstractItemDTO> updateParent(@NotNull @Identifier String userId, @Identifier String id, @Valid IdentifierDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            return Mono.empty();

            // Check that the announcement exists
/*            return itemRepository.findByTmAndUser_IdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, id)))
                .flatMap(existing -> {
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
                        return mapper.map(existing);
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
                    return itemRepository.save(existing).map(mapper::map);
                })
                .then(itemRepository.save(existing).map(mapper::map));*/
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<AbstractItemDTO> patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid AbstractItemPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return itemRepository.findByTmAndUser_IdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, id)))
                .flatMap(existing -> {
                    mapper.map(patchDTO, existing);
                
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return itemRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String userId, @NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check that the announcement exists
            return itemRepository.findByTmAndUser_IdAndId(tm, userId, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(AbstractItemEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return itemRepository.delete(existing).then(
                        this.onDelete(existing)
                    ).flatMap(deletedElement -> {
                        // Delete items under this one.
                        if (ItemType.FOLDER.equals(deletedElement.getType())) {
                            return itemRepository.findByTmAndUser_IdAndIdNotAndPathStartsWith(tm, userId, existing.getId(), existing.getPath())
                                .flatMap(childItem -> {
                                    return itemRepository.delete(childItem).then(this.onDelete(childItem));
                                }).then();
                        } else {
                            return Mono.empty();
                        }
                    });
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting an item.
     * @param tm the trademark.
     * @param pathPrefix the path prefix.
     * @param entity the entity.
     */
    private Mono<AbstractItemEntity> onPersist(String tm, String pathPrefix, AbstractItemEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connnectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());
            entity.setPath(StringUtils.join(pathPrefix, entity.getId()));

            // Add author.
            entity.setUser(userRefMapper.map(connnectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a item.
     * @param entity the entity.
     */
    private Mono<AbstractItemEntity> onUpdate(AbstractItemEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a item.
     * @param entity the entity.
     */
    private Mono<AbstractItemEntity> onDelete(AbstractItemEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<AbstractItemEntity> postResourceEvent(AbstractItemEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.BOOMARK_ITEM)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
