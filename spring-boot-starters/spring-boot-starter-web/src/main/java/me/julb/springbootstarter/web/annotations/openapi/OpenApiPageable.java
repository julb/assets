/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package me.julb.springbootstarter.web.annotations.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.domain.Pageable;

import me.julb.library.utility.constants.PageRequestAttributes;

/**
 * OpenAPI annotation for Methods having {@link Pageable} parameter.
 * <br>
 * @author Julb.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(in = ParameterIn.QUERY, description = "Zero-based page index (0..N)", name = PageRequestAttributes.PAGE_PARAMETER_NAME, content = @Content(schema = @Schema(type = "integer", defaultValue = "0")))
@Parameter(in = ParameterIn.QUERY, description = "The size of the page to be returned", name = PageRequestAttributes.SIZE_PARAMETER_NAME, content = @Content(schema = @Schema(type = "integer", defaultValue = "20")))
@Parameter(in = ParameterIn.QUERY, description = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. "
    + "Multiple sort criteria are supported.", name = PageRequestAttributes.SORT_PARAMETER_NAME, content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))
@Parameter(in = ParameterIn.QUERY, description = "Flag to return the results unpaged.", name = PageRequestAttributes.UNPAGED_PARAMETER_NAME, content = @Content(schema = @Schema(type = "boolean")))
public @interface OpenApiPageable {

}