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
package me.julb.library.utility.data.search.predicates.attributes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.data.search.predicates.SearchPredicate;

/**
 * The attribute predicate.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"name", "operator"})
public abstract class AbstractAttributePredicate implements SearchPredicate {
    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    private String name;

    //@formatter:off
     /**
     * The operator attribute.
     * -- GETTER --
     * Getter for {@link #operator} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #operator} property.
     * @param operator the value to set.
     */
     //@formatter:on
    private OperatorAttributePredicate operator;

    /**
     * Returns the values as String.
     * @return the values as string.
     */
    protected abstract String valuesAsString();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String stringifiedValue = valuesAsString();
        if (StringUtils.isNotBlank(stringifiedValue)) {
            stringifiedValue = StringUtils.join(Chars.QUOTATION_MARK, stringifiedValue, Chars.QUOTATION_MARK);
        }
        return StringUtils.join(this.name, Chars.PIPE, this.operator.shortName(), Chars.SEMICOLON, stringifiedValue);
    }
}
