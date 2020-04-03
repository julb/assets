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

package io.julb.applications.platformhealth.repositories;

import io.julb.applications.platformhealth.entities.ComponentCategoryEntity;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The component category repository.
 * <P>
 * @author Julb.
 */
public interface ComponentCategoryRepository extends MongoRepository<ComponentCategoryEntity, String>, MongoSpecificationExecutor<ComponentCategoryEntity> {

    /**
     * Finds an component category by trademark and id.
     * @param tm the trademark.
     * @param id the id.
     * @return the component category, or <code>null</code> if not exists.
     */
    ComponentCategoryEntity findByTmAndId(String tm, @NotNull @Identifier String id);

    /**
     * Finds all the component categories by their trademark ordered by position.
     * @param tm the trademark.
     * @return the component categories.
     */
    List<ComponentCategoryEntity> findByTmOrderByPositionAsc(String tm);
}
