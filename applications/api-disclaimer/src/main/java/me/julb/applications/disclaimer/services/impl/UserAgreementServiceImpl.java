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

package me.julb.applications.disclaimer.services.impl;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.disclaimer.entities.AgreementEntity;
import me.julb.applications.disclaimer.entities.DisclaimerEntity;
import me.julb.applications.disclaimer.entities.mappers.AgreementEntityMapper;
import me.julb.applications.disclaimer.repositories.AgreementRepository;
import me.julb.applications.disclaimer.repositories.DisclaimerRepository;
import me.julb.applications.disclaimer.repositories.specifications.AgreementByUserIdSpecification;
import me.julb.applications.disclaimer.services.UserAgreementService;
import me.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import me.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;

/**
 * The user agreement service implementation.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class UserAgreementServiceImpl implements UserAgreementService {

    /**
     * The disclaimer repository.
     */
    @Autowired
    private DisclaimerRepository disclaimerRepository;

    /**
     * The agreement repository.
     */
    @Autowired
    private AgreementRepository agreementRepository;

    /**
     * The mapper.
     */
    @Autowired
    private AgreementEntityMapper mapper;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<AgreementDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        ISpecification<AgreementEntity> spec = new SearchSpecification<AgreementEntity>(searchable).and(new TmSpecification<>(tm)).and(new AgreementByUserIdSpecification(userId));
        Page<AgreementEntity> result = agreementRepository.findAll(spec, pageable);
        return result.map(mapper::map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgreementDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String disclaimerId) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity disclaimer = disclaimerRepository.findByTmAndId(tm, disclaimerId);
        if (disclaimer == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, disclaimerId);
        }

        // Gets the agreement.
        AgreementEntity result = agreementRepository.findByTmAndDisclaimerIdAndUser_Id(tm, disclaimer.getId(), userId);
        if (result == null) {
            throw new ResourceNotFoundException(AgreementEntity.class, Map.<String, String> of("disclaimer", disclaimerId, "user", userId));
        }

        return mapper.map(result);
    }

    // ------------------------------------------ Write methods.
    // ------------------------------------------ Private methods.
}
