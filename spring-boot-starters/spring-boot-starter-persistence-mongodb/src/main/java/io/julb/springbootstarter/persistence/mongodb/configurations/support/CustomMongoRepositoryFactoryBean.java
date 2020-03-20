/*
 * Copyright 2010-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.julb.springbootstarter.persistence.mongodb.configurations.support;

import java.io.Serializable;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * {@link org.springframework.beans.factory.FactoryBean} to create {@link MongoRepository} instances.
 * @author Oliver Gierke
 */
public class CustomMongoRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends MongoRepositoryFactoryBean<T, S, ID> {

    /**
     * Creates a new {@link MongoRepositoryFactoryBean} for the given repository interface.
     * @param repositoryInterface must not be {@literal null}.
     */
    public CustomMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new CustomMongoRepositoryFactory(operations);
    }
}
