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
package me.julb.springbootstarter.web.resolvers.page;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.constants.PageRequestAttributes;

/**
 * A custom pageable handler method argument resolver.
 * <P>
 * @author Julb.
 */
public class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {

    /**
     * The default solr resolver.
     */
    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new CustomSortHandlerMethodArgumentResolver();

    /**
     * Constructor.
     */
    public CustomPageableHandlerMethodArgumentResolver() {
        super(DEFAULT_SORT_RESOLVER);
        this.setPageParameterName(PageRequestAttributes.PAGE_PARAMETER_NAME);
        this.setSizeParameterName(PageRequestAttributes.SIZE_PARAMETER_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        // Paged / Unpaged
        String paged = webRequest.getParameter(PageRequestAttributes.PAGED_PARAMETER_NAME);
        if (StringUtils.isNotBlank(paged) && Boolean.FALSE.toString().equalsIgnoreCase(paged)) {
            Sort sort = DEFAULT_SORT_RESOLVER.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
            return PageRequest.of(Integers.ZERO, Integer.MAX_VALUE, sort);
        }

        String unpaged = webRequest.getParameter(PageRequestAttributes.UNPAGED_PARAMETER_NAME);
        if (StringUtils.isNotBlank(unpaged) && Boolean.TRUE.toString().equalsIgnoreCase(unpaged)) {
            Sort sort = DEFAULT_SORT_RESOLVER.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
            return PageRequest.of(Integers.ZERO, Integer.MAX_VALUE, sort);
        }

        // Fallback to default.
        return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    }
}
