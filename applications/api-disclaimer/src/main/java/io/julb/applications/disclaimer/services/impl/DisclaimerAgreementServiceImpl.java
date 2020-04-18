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

package io.julb.applications.disclaimer.services.impl;

import io.julb.applications.disclaimer.entities.AgreementEntity;
import io.julb.applications.disclaimer.entities.DisclaimerEntity;
import io.julb.applications.disclaimer.repositories.AgreementRepository;
import io.julb.applications.disclaimer.repositories.DisclaimerRepository;
import io.julb.applications.disclaimer.repositories.specifications.AgreementByDisclaimerIdSpecification;
import io.julb.applications.disclaimer.services.DisclaimerAgreementService;
import io.julb.applications.disclaimer.services.UserAgreementService;
import io.julb.applications.disclaimer.services.dto.agreement.AgreementDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.mapping.services.IMappingService;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;
import io.julb.springbootstarter.persistence.mongodb.specifications.TmSpecification;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The disclaimer agreement service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DisclaimerAgreementServiceImpl implements DisclaimerAgreementService {

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
     * The user agreement service.
     */
    @Autowired
    private UserAgreementService userAgreementService;

    /**
     * The mapper.
     */
    @Autowired
    private IMappingService mappingService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<AgreementDTO> findAll(@NotNull @Identifier String disclaimerId, @NotNull Searchable searchable, @NotNull Pageable pageable) {
        String tm = TrademarkContextHolder.getTrademark();

        // Check that the disclaimer exists
        DisclaimerEntity disclaimer = disclaimerRepository.findByTmAndId(tm, disclaimerId);
        if (disclaimer == null) {
            throw new ResourceNotFoundException(DisclaimerEntity.class, disclaimerId);
        }

        ISpecification<AgreementEntity> spec = new SearchSpecification<AgreementEntity>(searchable).and(new TmSpecification<>(tm)).and(new AgreementByDisclaimerIdSpecification(disclaimerId));
        Page<AgreementEntity> result = agreementRepository.findAll(spec, pageable);
        return mappingService.mapAsPage(result, AgreementDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgreementDTO findOne(@NotNull @Identifier String disclaimerId, @NotNull @Identifier String userId) {
        return userAgreementService.findOne(userId, disclaimerId);
    }

    // ------------------------------------------ Write methods.
    // ------------------------------------------ Private methods.
}
