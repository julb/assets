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

package me.julb.springbootstarter.mapping.services;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * The mapping service.
 * <P>
 * @author Julb.
 */
public interface IMappingService {

    /**
     * Maps a value to a target class.
     * @param source the source value.
     * @param <S> the source generic type.
     * @param targetClass the target class type.
     * @param <T> the target generic type.
     * @return the target class instance.
     */
    <S, T> T map(S source, Class<T> targetClass);

    /**
     * Maps a value to a target value.
     * @param source the source value.
     * @param <S> the source generic type.
     * @param target the target value.
     * @param <T> the target generic type.
     */
    <S, T> void map(S source, T target);

    /**
     * Maps a source list to a target list of targetClass elements.
     * @param sourceList the source list.
     * @param <S> the source generic type.
     * @param targetClass the target class.
     * @param <T> the target generic type.
     * @return the list of targetClass elements.
     */
    <S, T> List<T> mapAsList(List<S> sourceList, Class<T> targetClass);

    /**
     * Maps a page to a target page of targetClass elements.
     * @param sourcePage the source page.
     * @param <S> the source generic type.
     * @param targetClass the target class.
     * @param <T> the target generic type.
     * @return a page of targetClass elements.
     */
    <S, T> Page<T> mapAsPage(Page<S> sourcePage, Class<T> targetClass);

}
