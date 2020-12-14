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
package me.julb.library.utility.data.search;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import me.julb.library.utility.data.search.predicates.SearchPredicate;
import me.julb.library.utility.data.search.predicates.joins.AndPredicate;

/**
 * The search request.
 * <P>
 * @author Julb.
 */
public class SearchRequest implements Searchable {

    //@formatter:off
     /**
     * The searchPredicate attribute.
     * -- GETTER --
     * Getter for {@link #searchPredicate} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #searchPredicate} property.
     * @param searchPredicate the value to set.
     */
     //@formatter:on
    private SearchPredicate searchPredicate;

    /**
     * Constructor.
     * @param predicates the predicates.
     */
    public SearchRequest(SearchPredicate... predicates) {
        if (ArrayUtils.isEmpty(predicates)) {
            this.searchPredicate = new AndPredicate();
        } else if (ArrayUtils.getLength(predicates) == 1) {
            this.searchPredicate = predicates[0];
        } else {
            this.searchPredicate = new AndPredicate(predicates);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchPredicate getSearchPredicate() {
        return searchPredicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (searchPredicate != null) {
            return searchPredicate.toString();
        } else {
            return StringUtils.EMPTY;
        }
    }
}
