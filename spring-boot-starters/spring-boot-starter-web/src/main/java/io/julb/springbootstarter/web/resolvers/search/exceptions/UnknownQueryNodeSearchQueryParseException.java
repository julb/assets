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
package io.julb.springbootstarter.web.resolvers.search.exceptions;

/**
 * This exception is thrown when an unknown operator is passed.
 * <P>
 * @author Julb.
 */
public class UnknownQueryNodeSearchQueryParseException extends SearchQueryParseException {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -3022729104571926557L;

    /**
     * The operator short name.
     */
    private String queryNode;

    // ------------------------------------------ Constructors.

    /**
     * Constructor.
     * @param searchQuery the search query.
     * @param queryNode the query node type.
     */
    public UnknownQueryNodeSearchQueryParseException(String searchQuery, String queryNode) {
        super(searchQuery);
        this.queryNode = queryNode;
    }

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return String.format("Failed to parse search query: [%s] : QueryNode type <%s> is not supported.", queryString, queryNode);
    }
}
