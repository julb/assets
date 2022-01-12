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

package me.julb.applications.urlshortener.services.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.urlshortener.entities.LinkEntity;
import me.julb.applications.urlshortener.entities.mappers.LinkEntityMapper;
import me.julb.applications.urlshortener.repositories.LinkRepository;
import me.julb.applications.urlshortener.services.HostService;
import me.julb.applications.urlshortener.services.LinkService;
import me.julb.applications.urlshortener.services.dto.LinkCreationDTO;
import me.julb.applications.urlshortener.services.dto.LinkDTO;
import me.julb.applications.urlshortener.services.dto.LinkPatchDTO;
import me.julb.applications.urlshortener.services.dto.LinkUpdateDTO;
import me.julb.library.dto.messaging.events.ResourceEventAsyncMessageDTO;
import me.julb.library.dto.messaging.events.ResourceEventType;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.exceptions.ResourceAlreadyExistsException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.ContextConstants;
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
 * The link service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class LinkServiceImpl implements LinkService {

    /**
     * The link repository.
     */
    @Autowired
    private LinkRepository linkRepository; 

    /**
     * The host service.
     */
    @Autowired
    private HostService hostService;

    /**
     * The mapper.
     */
    @Autowired
    private LinkEntityMapper mapper;

    /**
     * The user ref mapper.
     */
    @Autowired
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
    public Flux<LinkDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable) {
        return Flux.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            ISpecification<LinkEntity> spec = new SearchSpecification<LinkEntity>(searchable).and(new TmSpecification<>(tm));
            return linkRepository.findAll(spec, pageable).map(mapper::map);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<LinkDTO> findOne(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return linkRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(LinkEntity.class, id)))
                .map(mapper::map);
        });
    }

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<LinkDTO> create(@NotNull @Valid LinkCreationDTO creationDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // Check if not overlapping another one.
            return hostService.exists(creationDTO.getHost()).flatMap(hostExists -> {
                if (!hostExists.booleanValue()) {
                    return Mono.error(new ResourceNotFoundException(String.class, creationDTO.getHost()));
                }

                return linkRepository.existsByTmAndHostIgnoreCaseAndUriIgnoreCase(tm, creationDTO.getHost(), creationDTO.getUri())
                    .flatMap(alreadyExists -> {
                        if(alreadyExists.booleanValue()) {
                            return Mono.error(new ResourceAlreadyExistsException(LinkEntity.class, Map.<String, String> of("host", creationDTO.getHost(), "uri", creationDTO.getUri())));
                        }

                        // Update the entity
                        LinkEntity entityToCreate = mapper.map(creationDTO);
                        return this.onPersist(tm, entityToCreate).flatMap(entityToCreateWithFields -> {
                            return linkRepository.save(entityToCreateWithFields).map(mapper::map);
                        });
                });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<LinkDTO> incrementNumberOfHits(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return linkRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(LinkEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    existing.incrementNumberOfHits();
        
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return linkRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<LinkDTO> resetNumberOfHits(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return linkRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(LinkEntity.class, id)))
                .flatMap(existing -> {
                    // Update the entity
                    existing.setHits(0);
        
                    // Proceed to the update
                    return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                        return linkRepository.save(entityToUpdateWithFields).map(mapper::map);
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<LinkDTO> update(@NotNull @Identifier String id, @NotNull @Valid LinkUpdateDTO updateDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return linkRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(LinkEntity.class, id)))
                .flatMap(existing -> {
                    // Check if not overlapping another one.
                    return hostService.exists(updateDTO.getHost()).flatMap(hostExists -> {
                        if (!hostExists.booleanValue()) {
                            return Mono.error(new ResourceNotFoundException(String.class, updateDTO.getHost()));
                        }

                        return linkRepository.existsByTmAndIdNotAndHostIgnoreCaseAndUriIgnoreCase(tm, id, updateDTO.getHost(), updateDTO.getUri())
                            .flatMap(alreadyExists -> {
                                if(alreadyExists.booleanValue()) {
                                    return Mono.error(new ResourceAlreadyExistsException(LinkEntity.class, Map.<String, String> of("host", updateDTO.getHost(), "uri", updateDTO.getUri())));
                                }

                                // Update the entity
                                mapper.map(updateDTO, existing);
                    
                                // Proceed to the update
                                return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return linkRepository.save(entityToUpdateWithFields).map(mapper::map);
                                });
                        });
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<LinkDTO> patch(@NotNull @Identifier String id, @NotNull @Valid LinkPatchDTO patchDTO) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return linkRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(LinkEntity.class, id)))
                .flatMap(existing -> {
                    final String host;
                    if (StringUtils.isNotBlank(patchDTO.getHost())) {
                        host = patchDTO.getHost();
                    } else {
                        host = existing.getHost();
                    }

                    final String uri;
                    if (StringUtils.isNotBlank(patchDTO.getUri())) {
                        uri = patchDTO.getUri();
                    } else {
                        uri = existing.getUri();
                    }

                    // Check if not overlapping another one.
                    return hostService.exists(host).flatMap(hostExists -> {
                        if (!hostExists.booleanValue()) {
                            return Mono.error(new ResourceNotFoundException(String.class, host));
                        }

                        return linkRepository.existsByTmAndIdNotAndHostIgnoreCaseAndUriIgnoreCase(tm, id, host, uri)
                            .flatMap(alreadyExists -> {
                                if(alreadyExists.booleanValue()) {
                                    return Mono.error(new ResourceAlreadyExistsException(LinkEntity.class, Map.<String, String> of("host", host, "uri", uri)));
                                }

                                // Update the entity
                                mapper.map(patchDTO, existing);
                    
                                // Proceed to the update
                                return this.onUpdate(existing).flatMap(entityToUpdateWithFields -> {
                                    return linkRepository.save(entityToUpdateWithFields).map(mapper::map);
                                });
                        });
                    });
                });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Mono<Void> delete(@NotNull @Identifier String id) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            return linkRepository.findByTmAndId(tm, id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(LinkEntity.class, id)))
                .flatMap(existing -> {
                    // Delete entity.
                    return linkRepository.delete(existing).then(
                        this.onDelete(existing)
                    ).then();
                });
        });
    }

    // ------------------------------------------ Private methods.

    /**
     * Method called when persisting a link.
     * @param entity the entity.
     */
    private Mono<LinkEntity> onPersist(String tm, LinkEntity entity) {
        return securityService.getConnectedUserRefIdentity().flatMap(connectedUser -> {
            entity.setId(IdentifierUtility.generateId());
            entity.setTm(tm);
            entity.setCreatedAt(DateUtility.dateTimeNow());
            entity.setLastUpdatedAt(DateUtility.dateTimeNow());
            entity.setEnabled(true);
            entity.setHits(0);

            // Add author.
            entity.setUser(userRefMapper.map(connectedUser));

            return postResourceEvent(entity, ResourceEventType.CREATED);
        });
    }

    /**
     * Method called when updating a link.
     * @param entity the entity.
     */
    private Mono<LinkEntity> onUpdate(LinkEntity entity) {
        entity.setLastUpdatedAt(DateUtility.dateTimeNow());
        return postResourceEvent(entity, ResourceEventType.UPDATED);
    }

    /**
     * Method called when deleting a link.
     * @param entity the entity.
     */
    private Mono<LinkEntity> onDelete(LinkEntity entity) {
        return postResourceEvent(entity, ResourceEventType.DELETED);
    }

    /**
     * Post a resource event.
     * @param entity the entity.
     * @param resourceEventType the resource event type.
     */
    private Mono<LinkEntity> postResourceEvent(LinkEntity entity, ResourceEventType resourceEventType) {
        return securityService.getConnectedUserName().flatMap(userName -> {
            //@formatter:off
            ResourceEventAsyncMessageDTO resourceEvent = new ResourceEventAsyncMessageBuilder()
                .withObject(entity.getClass(), entity.getTm(), entity.getId(), entity.getId(), ResourceTypes.LINK)
                .eventType(resourceEventType)
                .user(userName)
                .build();
            //@formatter:on

            return this.asyncMessagePosterService.postResourceEventMessage(resourceEvent).then(Mono.just(entity));
        });
    }
}
