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

package io.julb.applications.disclaimer.repositories;

import io.julb.applications.disclaimer.entities.AgreementEntity;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The agreement repository.
 * <P>
 * @author Julb.
 */
public interface AgreementRepository extends MongoRepository<AgreementEntity, String>, MongoSpecificationExecutor<AgreementEntity> {

    /**
     * Checks if an agreement exists between the disclaimer and the user.
     * @param tm the trademark.
     * @param disclaimerId the disclaimer ID.
     * @param userId the user ID.
     * @return <code>true</code> if the agreement exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndDisclaimerIdAndUser_Id(String tm, String disclaimerId, String userId);

    /**
     * Checks if an agreement exists between the disclaimer and the user.
     * @param tm the trademark.
     * @param disclaimerId the disclaimer ID.
     * @param userId the user ID.
     * @return <code>true</code> if the agreement exists, <code>false</code> otherwise.
     */
    AgreementEntity findByTmAndDisclaimerIdAndUser_Id(String tm, String disclaimerId, String userId);

}
