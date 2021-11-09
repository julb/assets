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
package me.julb.springbootstarter.persistence.mongodb.configurations;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import me.julb.springbootstarter.persistence.mongodb.configurations.support.CustomMongoRepositoryFactoryBean;
import me.julb.springbootstarter.persistence.mongodb.converters.LocaleReadingConverter;
import me.julb.springbootstarter.persistence.mongodb.converters.LocaleWritingConverter;

/**
 * The persistence MongoDB configuration.
 * <br>
 * @author Julb.
 */
@Configuration
@EnableMongoRepositories(basePackages = "me.julb", repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class)
public class PersistenceMongoConfiguration {

    /**
     * Creates a transaction manager for MongoDB.
     * @param dbFactory the DB factory.
     * @return the Mongo transaction manager.
     */
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * Returns a lister to validate object before insertion.
     * @param validator the validator.
     * @return an event listener to validate objects.
     */
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(Validator validator) {
        return new ValidatingMongoEventListener(validator);
    }

    /**
     * MongoDB custom conversions.
     * @return MongoDB custom conversions.
     */
    @Bean
    public MongoCustomConversions customConversions() {
        return MongoCustomConversions.create(this::configureConverters);
    }

    /**
     * Configuration hook for {@link MongoCustomConversions} creation.
     * @param converterConfigurationAdapter the adapter to register converters.
     */
    protected void configureConverters(MongoConverterConfigurationAdapter converterConfigurationAdapter) {
        converterConfigurationAdapter.registerConverter(new LocaleReadingConverter());
        converterConfigurationAdapter.registerConverter(new LocaleWritingConverter());
    }
}
