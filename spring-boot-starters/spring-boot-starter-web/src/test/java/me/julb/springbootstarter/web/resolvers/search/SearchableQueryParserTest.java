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
package me.julb.springbootstarter.web.resolvers.search;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.julb.library.utility.data.search.predicates.attributes.MultipleValuesAttributePredicate;
import me.julb.library.utility.data.search.predicates.attributes.NoValueAttributePredicate;
import me.julb.library.utility.data.search.predicates.attributes.OperatorAttributePredicate;
import me.julb.library.utility.data.search.predicates.attributes.SingleValueAttributePredicate;
import me.julb.springbootstarter.web.resolvers.search.exceptions.SearchTermSearchQueryParseException;
import me.julb.springbootstarter.web.resolvers.search.exceptions.UnknownOperatorSearchQueryParseException;

/**
 * Test class for {@link SearchableQueryParser} class.
 * <br>
 * @author Julb.
 */
public class SearchableQueryParserTest {

    /**
     * Test method.
     */
    @Test
    public void whenParsingComplexExpression_thenSuccess()
        throws Exception {
        //@formatter:off
        String[] queries = {
            "lastName:\"John DOE\"",
            "lastName:\"John DOE\" firstName:\"Alice\"",
            "lastName:\"John DOE\" AND firstName:\"Alice\"",
            "lastName:\"John DOE\" OR firstName:\"Alice\"",
            "(lastName:\"John DOE\" OR firstName:\"Alice\") AND age:16",
            "(lastName:\"John DOE\" OR firstName:\"Alice\") AND !age:16",
            "((lastName:\"John DOE\" OR firstName:\"Alice\") AND !(age:16 && size:1)) OR test|in:09,10,11"
        };
        //@formatter:on 

        for (String query : queries) {
            SearchableQueryParser.parse(query);
        }
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNoOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.EQUAL, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithEqOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|eq", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.EQUAL, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotEqualOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ne", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.NOT_EQUAL, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithGreaterOrEqualThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ge", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.GREATER_OR_EQUAL_THAN, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithGreaterThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|gt", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.GREATER_THAN, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithLessOrEqualThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|le", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.LESS_OR_EQUAL_THAN, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithLessThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|lt", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.LESS_THAN, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithLikeOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|lk", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.LIKE, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotLikeOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|nl", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.NOT_LIKE, predicate.getOperator());
        Assertions.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithInOperator_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|in", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.IN, predicate.getOperator());
        Assertions.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithInOperatorMultipleValues_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|in", "DOE,BOB");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.IN, predicate.getOperator());
        Assertions.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
        Assertions.assertTrue(ArrayUtils.contains(predicate.getValue(), "BOB"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotInOperator_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ni", "DOE");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.NOT_IN, predicate.getOperator());
        Assertions.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotInOperatorMultipleValues_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ni", "DOE,BOB");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.NOT_IN, predicate.getOperator());
        Assertions.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
        Assertions.assertTrue(ArrayUtils.contains(predicate.getValue(), "BOB"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithIsNullOperator_thenReturnValidPredicate() {
        NoValueAttributePredicate predicate = (NoValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|nu", "");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.IS_NULL, predicate.getOperator());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithIsNotNullOperatorMultipleValues_thenReturnValidPredicate() {
        NoValueAttributePredicate predicate = (NoValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|nn", "");
        Assertions.assertEquals("lastName", predicate.getName());
        Assertions.assertEquals(OperatorAttributePredicate.IS_NOT_NULL, predicate.getOperator());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithIsUnknownOperator_thenThrowUnknownOperatorSearchQueryParseException() {
        Assertions.assertThrows(UnknownOperatorSearchQueryParseException.class, () -> {
            SearchableQueryParser.searchTermToPredicate("", "lastName|zz", "");
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithEqOperatorNoValue_ThrowSearchTermSearchQueryParseException() {
        Assertions.assertThrows(SearchTermSearchQueryParseException.class, () -> {
            SearchableQueryParser.searchTermToPredicate("", "lastName|eq", "");
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNoOperatorNoValue_ThrowSearchTermSearchQueryParseException() {
        Assertions.assertThrows(SearchTermSearchQueryParseException.class, () -> {
            SearchableQueryParser.searchTermToPredicate("", "lastName", "");
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNoProvidedOperator_ThrowSearchTermSearchQueryParseException() {
        Assertions.assertThrows(SearchTermSearchQueryParseException.class, () -> {
            SearchableQueryParser.searchTermToPredicate("", "lastName|", "");
        });
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNoValueOperatorValueProvided_ThrowSearchTermSearchQueryParseException() {
        Assertions.assertThrows(SearchTermSearchQueryParseException.class, () -> {
            SearchableQueryParser.searchTermToPredicate("", "lastName|nn", "DOE");
        });
    }
}
