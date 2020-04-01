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
package io.julb.library.utility.data.search.predicates.modifiers;

import io.julb.library.utility.constants.Chars;
import io.julb.library.utility.data.search.predicates.IPredicate;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

/**
 * The not predicate.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class NotPredicate implements IPredicate {

    //@formatter:off
     /**
     * The predicate attribute.
     * -- GETTER --
     * Getter for {@link #predicate} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #predicate} property.
     * @param predicate the value to set.
     */
     //@formatter:on
    private IPredicate predicate = null;

    /**
     * Constructor.
     * @param predicate the predicate.
     */
    public NotPredicate(IPredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return StringUtils.join(Chars.EXCLAMATION_MARK, predicate.toString());
    }

}
