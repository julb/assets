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
package me.julb.springbootstarter.web.reactive.resolvers.page;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;

import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.constants.PageRequestAttributes;

/**
 * A custom pageable handler method argument resolver.
 * <br>
 * @author Julb.
 */
public class CustomPageableHandlerMethodArgumentResolver extends ReactivePageableHandlerMethodArgumentResolver {

    /**
     * The default solr resolver.
     */
    private static final ReactiveSortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new CustomSortHandlerMethodArgumentResolver();

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
    public Pageable resolveArgumentValue(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        // Paged / Unpaged
        String paged = exchange.getRequest().getQueryParams().getFirst(PageRequestAttributes.PAGED_PARAMETER_NAME);
        if (StringUtils.isNotBlank(paged) && Boolean.FALSE.toString().equalsIgnoreCase(paged)) {
            Sort sort = DEFAULT_SORT_RESOLVER.resolveArgumentValue(parameter, bindingContext, exchange);
            return PageRequest.of(Integers.ZERO, Integer.MAX_VALUE, sort);
        }

        String unpaged = exchange.getRequest().getQueryParams().getFirst(PageRequestAttributes.UNPAGED_PARAMETER_NAME);
        if (StringUtils.isNotBlank(unpaged) && Boolean.TRUE.toString().equalsIgnoreCase(unpaged)) {
            Sort sort = DEFAULT_SORT_RESOLVER.resolveArgumentValue(parameter, bindingContext, exchange);
            return PageRequest.of(Integers.ZERO, Integer.MAX_VALUE, sort);
        }

        // Fallback to default.
        return super.resolveArgumentValue(parameter, bindingContext, exchange);
    }
}
