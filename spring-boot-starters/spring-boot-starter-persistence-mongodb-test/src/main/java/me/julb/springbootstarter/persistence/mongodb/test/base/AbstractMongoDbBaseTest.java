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

package me.julb.springbootstarter.persistence.mongodb.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Base class for unit tests for MongoDB.
 * <P>
 * @author Julb.
 */
public abstract class AbstractMongoDbBaseTest extends AbstractBaseTest {

    /**
     * The mongo-template to create collections.
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Method triggered during method startup.
     */
    @BeforeEach
    public void setUp() {
        // Create collections.
        for (Class<?> entityClass : getEntityClasses()) {
            if (mongoTemplate.collectionExists(entityClass)) {
                mongoTemplate.dropCollection(entityClass);
            }
            mongoTemplate.createCollection(entityClass);
        }

        // Create data.
        this.setupData();
    }

    /**
     * Gets entity classes to initialize.
     * @return the entity classes to initialize.
     */
    public Class<?>[] getEntityClasses() {
        return new Class<?>[0];
    }

    /**
     * Sets-up the data.
     */
    public void setupData() {
        // NOOP
    }
}
