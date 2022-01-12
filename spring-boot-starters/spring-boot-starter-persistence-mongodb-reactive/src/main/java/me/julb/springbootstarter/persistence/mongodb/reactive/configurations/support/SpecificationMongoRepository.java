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

package me.julb.springbootstarter.persistence.mongodb.reactive.configurations.support;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository;

import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The mongo repository implementation with specification executor.
 * <br>
 * @author Julb.
 */
public class SpecificationMongoRepository<T, ID extends Serializable> extends SimpleReactiveMongoRepository<T, ID> implements MongoSpecificationExecutor<T> {

    //@formatter:off
     /**
     * The mongoOperations attribute.
     */
     //@formatter:on
    private final ReactiveMongoOperations reactiveMongoOperations;

    //@formatter:off
     /**
     * The entityInformation attribute.
     */
     //@formatter:on
    private final MongoEntityInformation<T, ID> entityInformation;

    /**
     * Creates a new {@link SimpleMongoRepository} for the given {@link MongoEntityInformation} and {@link MongoTemplate}.
     * @param metadata must not be {@literal null}.
     * @param reactiveMongoOperations must not be {@literal null}.
     */
    public SpecificationMongoRepository(MongoEntityInformation<T, ID> metadata, ReactiveMongoOperations reactiveMongoOperations) {
        super(metadata, reactiveMongoOperations);

        this.entityInformation = metadata;
        this.reactiveMongoOperations = reactiveMongoOperations;
    }
    // ------------------------------------------ Utility methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<T> findOne(ISpecification<T> spec) {
        return reactiveMongoOperations.findOne(query(spec), entityInformation.getJavaType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findAll(ISpecification<T> spec) {
        return reactiveMongoOperations.find(query(spec), entityInformation.getJavaType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findAll(ISpecification<T> spec, Pageable pageable) {
        Query query = query(spec);
        return reactiveMongoOperations.find(query.with(pageable), entityInformation.getJavaType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<T> findAll(ISpecification<T> spec, Sort sort) {
        return reactiveMongoOperations.find(query(spec).with(sort), entityInformation.getJavaType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Long> count(ISpecification<T> spec) {
        return reactiveMongoOperations.count(query(spec), entityInformation.getJavaType());
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
