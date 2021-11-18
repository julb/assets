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

package me.julb.springbootstarter.persistence.mongodb.configurations.support;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import me.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;
import me.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;

/**
 * The mongo repository implementation with specification executor.
 * <br>
 * @author Julb.
 */
public class SpecificationMongoRepository<T, ID> extends SimpleMongoRepository<T, ID> implements MongoSpecificationExecutor<T> {

    //@formatter:off
     /**
     * The mongoOperations attribute.
     */
     //@formatter:on
    private final MongoOperations mongoOperations;

    //@formatter:off
     /**
     * The entityInformation attribute.
     */
     //@formatter:on
    private final MongoEntityInformation<T, ID> entityInformation;

    /**
     * Creates a new {@link SimpleMongoRepository} for the given {@link MongoEntityInformation} and {@link MongoTemplate}.
     * @param metadata must not be {@literal null}.
     * @param mongoOperations must not be {@literal null}.
     */
    public SpecificationMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);

        this.entityInformation = metadata;
        this.mongoOperations = mongoOperations;
    }
    // ------------------------------------------ Utility methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findOne(ISpecification<T> spec) {
        return Optional.ofNullable(mongoOperations.findOne(query(spec), entityInformation.getJavaType()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(ISpecification<T> spec) {
        return mongoOperations.find(query(spec), entityInformation.getJavaType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(ISpecification<T> spec, Pageable pageable) {
        Query query = query(spec);

        Long count = mongoOperations.count(query, entityInformation.getJavaType());
        List<T> list = mongoOperations.find(query.with(pageable), entityInformation.getJavaType());
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(ISpecification<T> spec, Sort sort) {
        return mongoOperations.find(query(spec).with(sort), entityInformation.getJavaType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(ISpecification<T> spec) {
        return mongoOperations.count(query(spec), entityInformation.getJavaType());
    }

    // ------------------------------------------ Private methods.

    /**
     * Builds a query instance from a criteria.
     * @param spec the specification.
     * @return the quer instance.
     */
    private Query query(ISpecification<T> spec) {
        Optional<Criteria> criteria = spec.toCriteria(entityInformation.getJavaType());
        if (criteria.isEmpty()) {
            return new Query();
        } else {
            return new Query(criteria.get());
        }
    }
}
