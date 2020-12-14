/*
 * Copyright 2013-2019 the original author or authors.
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
package me.julb.springbootstarter.web.resolvers.search;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.PageRequestAttributes;
import me.julb.library.utility.constants.SearchRequestAttributes;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.data.search.Searchable;

/**
 * Extract query information from web requests.
 * <P>
 * @author Julb.
 */
public class CustomSearchableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * The default prefix.
     */
    private static final String DEFAULT_PREFIX = "";

    /**
     * The stop words.
     */
    private static final String[] STOP_WORDS = new String[] {SearchRequestAttributes.QUERY_PARAMETER_NAME, PageRequestAttributes.PAGE_PARAMETER_NAME, PageRequestAttributes.SIZE_PARAMETER_NAME, PageRequestAttributes.SORT_PARAMETER_NAME,
        PageRequestAttributes.PAGED_PARAMETER_NAME, PageRequestAttributes.UNPAGED_PARAMETER_NAME};

    /**
     * The query parameter name.
     */
    private String queryParameterName = SearchRequestAttributes.QUERY_PARAMETER_NAME;

    /**
     * The prefix to use for parameter resolving.
     */
    private String prefix = DEFAULT_PREFIX;

    /**
     * Constructor.
     */
    public CustomSearchableHandlerMethodArgumentResolver() {
        super();
    }

    /**
     * Sets the prefix to use to resolve parameters.
     * @param prefix the prefix to use.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Searchable.class.equals(parameter.getParameterType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Searchable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        SearchableAnnotationUtils.assertSearchableUniqueness(methodParameter);
        String searchQuery = webRequest.getParameter(getParameterNameToUse(queryParameterName));
        if (searchQuery != null) {
            return SearchableQueryParser.parse(searchQuery);
        } else {
            return parseQueryParameters(methodParameter, webRequest);
        }
    }

    /**
     * Gets the parameter name to use.
     * @param source the source parameter name.
     * @return the parameter name to use.
     */
    protected String getParameterNameToUse(String source) {
        return new StringBuilder(prefix).append(source).toString();
    }

    /**
     * @param methodParameter
     * @return
     */
    @Deprecated
    private Searchable parseQueryParameters(MethodParameter methodParameter, WebRequest webRequest) {
        Iterator<String> parameterNamesIterator = webRequest.getParameterNames();

        Set<String> stopWords = new HashSet<>();
        stopWords.addAll(Arrays.asList(STOP_WORDS));
        Method method = methodParameter.getMethod();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                String value = parameter.getAnnotation(RequestParam.class).value();
                if (StringUtils.isNotBlank(value)) {
                    stopWords.add(value.toLowerCase());
                } else {
                    String name = parameter.getAnnotation(RequestParam.class).name();
                    if (StringUtils.isNotBlank(name)) {
                        stopWords.add(name.toLowerCase());
                    }
                }
            }
        }

        StringBuffer sb = new StringBuffer();
        while (parameterNamesIterator.hasNext()) {
            String parameterName = parameterNamesIterator.next();

            // Stop words.
            if (stopWords.contains(parameterName.toLowerCase())) {
                continue;
            }

            // Format parameter name
            String reformatedParameterName = parameterName.replace(Strings.LEFT_BRACKET, Strings.PIPE).replace(Strings.RIGHT_BRACKET, Strings.EMPTY);

            // Append query.
            sb.append(reformatedParameterName).append(Chars.SEMICOLON).append(Strings.DOUBLE_QUOTE).append(webRequest.getParameter(parameterName)).append(Strings.DOUBLE_QUOTE);
            sb.append(Chars.SPACE);
        }
        return SearchableQueryParser.parse(sb.toString());
    }
}
