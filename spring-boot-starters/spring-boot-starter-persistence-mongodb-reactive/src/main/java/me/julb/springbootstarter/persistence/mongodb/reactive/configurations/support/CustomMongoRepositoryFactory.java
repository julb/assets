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
package me.julb.springbootstarter.persistence.mongodb.reactive.configurations.support;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import me.julb.springbootstarter.persistence.mongodb.reactive.repositories.MongoSpecificationExecutor;

/**
 * Factory to create {@link MongoRepository} instances.
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public class CustomMongoRepositoryFactory extends ReactiveMongoRepositoryFactory {

    /**
     * Creates a new {@link CustomMongoRepositoryFactory} with the given {@link MongoOperations}.
     * @param mongoOperations must not be {@literal null}.
     */
    public CustomMongoRepositoryFactory(ReactiveMongoOperations mongoOperations) {
        super(mongoOperations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (MongoSpecificationExecutor.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            return SpecificationMongoRepository.class;
        } else {
            return super.getRepositoryBaseClass(metadata);
        }
    }
}
