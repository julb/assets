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

package me.julb.springbootstarter.persistence.mongodb.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;

/**
 * A mongo specification executor.
 * <br>
 * @author Julb.
 */
public interface MongoSpecificationExecutor<T> {

    /**
     * Returns a single entity matching the given {@link ISpecification} or {@link Optional#empty()} if none found.
     * @param spec the specification.
     * @return never {@literal null}.
     */
    Optional<T> findOne(ISpecification<T> spec);

    /**
     * Returns all entities matching the given {@link ISpecification}.
     * @param spec the specification.
     * @return never {@literal null}.
     */
    List<T> findAll(ISpecification<T> spec);

    /**
     * Returns a {@link Page} of entities matching the given {@link ISpecification}.
     * @param spec the specification.
     * @param pageable the pageable information.
     * @return never {@literal null}.
     */
    Page<T> findAll(ISpecification<T> spec, Pageable pageable);

    /**
     * Returns all entities matching the given {@link ISpecification} and {@link Sort}.
     * @param spec the specification.
     * @param sort the sort information.
     * @return never {@literal null}.
     */
    List<T> findAll(ISpecification<T> spec, Sort sort);

    /**
     * Returns the number of instances that the given {@link ISpecification} will return.
     * @param spec the specification.
     * @return the number of instances.
     */
    long count(ISpecification<T> spec);

}