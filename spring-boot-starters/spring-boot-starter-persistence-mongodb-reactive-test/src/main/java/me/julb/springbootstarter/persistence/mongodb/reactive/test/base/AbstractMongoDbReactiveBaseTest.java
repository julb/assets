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

package me.julb.springbootstarter.persistence.mongodb.reactive.test.base;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Base class for unit tests for MongoDB.
 * <br>
 * @author Julb.
 */
public abstract class AbstractMongoDbReactiveBaseTest extends AbstractBaseTest {

    /**
     * The mongo-template to create collections.
     */
    @Autowired
    protected ReactiveMongoTemplate reactiveMongoTemplate;

    /**
     * Method triggered during method startup.
     */
    @BeforeEach
    public void setUp() {
        // Create collections.
        getEntityClasses().flatMap(entityClass -> {
            return reactiveMongoTemplate.collectionExists(entityClass).map(collectionExists -> {
                if (collectionExists.booleanValue()) {
                    return reactiveMongoTemplate.dropCollection(entityClass).then(reactiveMongoTemplate.createCollection(entityClass));
                } else {
                    return reactiveMongoTemplate.createCollection(entityClass);
                }
            });
        })
        .then(
            this.setupData()
                .contextWrite(Context.of(
                    ContextConstants.TRADEMARK, TM, 
                    ContextConstants.LOCALE, Locale.getDefault()
                )))
        .block();
    }

    /**
     * Gets entity classes to initialize.
     * @return the entity classes to initialize.
     */
    public Flux<Class<?>> getEntityClasses() {
        return Flux.empty();
    }

    /**
     * Sets-up the data.
     */
    public Mono<Void> setupData() {
        // NOOP
        return Mono.empty();
    }
}
