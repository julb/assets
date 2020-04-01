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
package io.julb.library.utility.data.search.predicates.attributes;

import io.julb.library.utility.constants.Chars;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

/**
 * The search attribute predicate with multiple values.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class MultipleValuesAttributePredicate extends AbstractAttributePredicate {

    //@formatter:off
     /**
     * The value attribute.
     * -- GETTER --
     * Getter for {@link #value} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #value} property.
     * @param value the value to set.
     */
     //@formatter:on
    private String[] value;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String valuesAsString() {
        return StringUtils.join(value, Chars.COMMA);
    }
}
