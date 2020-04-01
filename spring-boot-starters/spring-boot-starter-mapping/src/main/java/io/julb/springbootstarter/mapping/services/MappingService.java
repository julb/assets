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

package io.julb.springbootstarter.mapping.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ma.glasnost.orika.MapperFacade;

/**
 * A default mapper between DTOs and entities of the project
 * <P>
 * @author Julb.
 */
@Service
public class MappingService implements IMappingService {

    /**
     * The mapper facae instance.
     */
    @Autowired
    private MapperFacade mapperFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T> T map(S source, Class<T> targetClass) {
        return mapperFacade.map(source, targetClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T> void map(S source, T target) {
        mapperFacade.map(source, target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T> List<T> mapAsList(List<S> sourceList, Class<T> targetClass) {
        return mapperFacade.mapAsList(sourceList, targetClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S, T> Page<T> mapAsPage(Page<S> sourcePage, Class<T> targetClass) {
        return sourcePage.map((entity) -> {
            return map(entity, targetClass);
        });
    }
}
