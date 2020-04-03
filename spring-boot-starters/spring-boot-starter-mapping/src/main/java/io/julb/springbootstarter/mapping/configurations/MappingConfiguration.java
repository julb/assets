/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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

package io.julb.springbootstarter.mapping.configurations;

import io.julb.library.dto.simple.identifier.IdentifierDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * The mapper configuration.
 * <P>
 * This configuration looks for {@link ObjectMappingFactory} in entities and configure the mapper.
 * <P>
 * @author Julb.
 */
@Configuration
public class MappingConfiguration {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingConfiguration.class);

    /**
     * The base-package mapping.
     */
    @Value("${mapping.base-package:io.julb}")
    private String basePackage;

    /**
     * The mapper facade for all DTOs/entities of the application.
     * @return the mapper facade.
     * @throws ClassNotFoundException if unable to load the bean definition class.
     */
    @Bean
    @ConditionalOnMissingBean
    public MapperFacade getMapperFacade()
        throws ClassNotFoundException {
        MapperFactory mapperFactory = getMapperFactory();
        return mapperFactory.getMapperFacade();
    }

    /**
     * Create a mapper factory.
     * @return the mapper factory.
     * @throws ClassNotFoundException if a class is not found.
     */
    @Bean
    public MapperFactory getMapperFactory()
        throws ClassNotFoundException {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassPathScanningCandidateComponentProvider scanner;
        LOGGER.info("Starting mappings configuration");

        // Exclude all IdDTO conversion.
        mapperFactory.classMap(IdentifierDTO.class, Object.class).exclude("id").register();

        // Look for creation annotations
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ObjectMappingFactory.class));
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
            Class<?> entityClass = getClass().getClassLoader().loadClass(beanDefinition.getBeanClassName());
            ObjectMappingFactory entityMapperFactory = entityClass.getAnnotationsByType(ObjectMappingFactory.class)[0];

            // Read DTO mapping
            for (Class<?> dtoClass : entityMapperFactory.read()) {
                mapperFactory.classMap(entityClass, dtoClass).byDefault().register();
                LOGGER.info("Mapping found for read: {} -> {}", dtoClass.getSimpleName(), entityClass.getSimpleName());
            }

            // Creation DTO mapping
            for (Class<?> dtoClass : entityMapperFactory.creation()) {
                mapperFactory.classMap(dtoClass, entityClass).byDefault().register();
                LOGGER.info("Mapping found for creation: {} -> {}", dtoClass.getSimpleName(), entityClass.getSimpleName());
            }

            // Update DTO mapping
            for (Class<?> dtoClass : entityMapperFactory.update()) {
                mapperFactory.classMap(dtoClass, entityClass).byDefault().register();
                LOGGER.info("Mapping found for update: {} -> {}", dtoClass.getSimpleName(), entityClass.getSimpleName());
            }

            // Patch DTO mapping
            for (Class<?> dtoClass : entityMapperFactory.patch()) {
                mapperFactory.classMap(dtoClass, entityClass).mapNulls(false).byDefault().register();
                LOGGER.info("Mapping found for patch: {} -> {}", dtoClass.getSimpleName(), entityClass.getSimpleName());
            }
        }

        LOGGER.info("Default mappings configuration done.");
        return mapperFactory;
    }

}
