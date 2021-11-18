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

package me.julb.applications.disclaimer.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import me.julb.applications.disclaimer.entities.DisclaimerEntity;
import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

/**
 * The disclaimer repository.
 * <br>
 * @author Julb.
 */
public interface DisclaimerRepository extends MongoRepository<DisclaimerEntity, String>, MongoSpecificationExecutor<DisclaimerEntity> {

    /**
     * Finds a disclaimer by trademark and id.
     * @param tm the trademark.
     * @param id the id.
     * @return the disclaimer, or <code>null</code> if not exists.
     */
    DisclaimerEntity findByTmAndId(String tm, String id);

    /**
     * Checks if a disclaimer exists with given code and version.
     * @param tm the trademark.
     * @param code the code.
     * @param version the version.
     * @return <code>true</code> if a disclaimer exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndCodeIgnoreCaseAndVersion(String tm, String code, Integer version);

    /**
     * Checks if a disclaimer not having given id exists with given code and version.
     * @param tm the trademark.
     * @param id the ID to exclude.
     * @param code the code.
     * @param version the version.
     * @return <code>true</code> if a disclaimer exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndIdNotAndCodeIgnoreCaseAndVersion(String tm, String id, String code, Integer version);

    /**
     * Finds all disclaimers with given code excluding given ID that are active.
     * @param tm the trademark.
     * @param id the ID.
     * @param code the code.
     * @return the disclaimers.
     */
    List<DisclaimerEntity> findByTmAndIdNotAndCodeIgnoreCaseAndActiveIsTrue(String tm, String id, String code);

}
