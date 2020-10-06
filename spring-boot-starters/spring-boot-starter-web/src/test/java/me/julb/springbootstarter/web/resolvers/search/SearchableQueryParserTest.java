/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.julb.springbootstarter.web.resolvers.search;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import me.julb.library.utility.data.search.predicates.attributes.MultipleValuesAttributePredicate;
import me.julb.library.utility.data.search.predicates.attributes.NoValueAttributePredicate;
import me.julb.library.utility.data.search.predicates.attributes.OperatorAttributePredicate;
import me.julb.library.utility.data.search.predicates.attributes.SingleValueAttributePredicate;
import me.julb.springbootstarter.web.resolvers.search.SearchableQueryParser;
import me.julb.springbootstarter.web.resolvers.search.exceptions.SearchTermSearchQueryParseException;
import me.julb.springbootstarter.web.resolvers.search.exceptions.UnknownOperatorSearchQueryParseException;

/**
 * Test class for {@link SearchableQueryParser} class.
 * <P>
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
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.EQUAL, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithEqOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|eq", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.EQUAL, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotEqualOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ne", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.NOT_EQUAL, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithGreaterOrEqualThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ge", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.GREATER_OR_EQUAL_THAN, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithGreaterThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|gt", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.GREATER_THAN, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithLessOrEqualThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|le", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.LESS_OR_EQUAL_THAN, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithLessThanOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|lt", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.LESS_THAN, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithLikeOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|lk", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.LIKE, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotLikeOperator_thenReturnValidPredicate() {
        SingleValueAttributePredicate predicate = (SingleValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|nl", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.NOT_LIKE, predicate.getOperator());
        Assert.assertEquals("DOE", predicate.getValue());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithInOperator_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|in", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.IN, predicate.getOperator());
        Assert.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithInOperatorMultipleValues_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|in", "DOE,BOB");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.IN, predicate.getOperator());
        Assert.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
        Assert.assertTrue(ArrayUtils.contains(predicate.getValue(), "BOB"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotInOperator_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ni", "DOE");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.NOT_IN, predicate.getOperator());
        Assert.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotInOperatorMultipleValues_thenReturnValidPredicate() {
        MultipleValuesAttributePredicate predicate = (MultipleValuesAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|ni", "DOE,BOB");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.NOT_IN, predicate.getOperator());
        Assert.assertTrue(ArrayUtils.contains(predicate.getValue(), "DOE"));
        Assert.assertTrue(ArrayUtils.contains(predicate.getValue(), "BOB"));
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithIsNullOperator_thenReturnValidPredicate() {
        NoValueAttributePredicate predicate = (NoValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|nu", "");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.IS_NULL, predicate.getOperator());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithIsNotNullOperatorMultipleValues_thenReturnValidPredicate() {
        NoValueAttributePredicate predicate = (NoValueAttributePredicate) SearchableQueryParser.searchTermToPredicate("", "lastName|nn", "");
        Assert.assertEquals("lastName", predicate.getName());
        Assert.assertEquals(OperatorAttributePredicate.IS_NOT_NULL, predicate.getOperator());
    }

    /**
     * Test method.
     */
    @Test(expected = UnknownOperatorSearchQueryParseException.class)
    public void whenTermWithIsUnknownOperator_thenThrowUnknownOperatorSearchQueryParseException() {
        SearchableQueryParser.searchTermToPredicate("", "lastName|zz", "");
    }

    /**
     * Test method.
     */
    @Test(expected = SearchTermSearchQueryParseException.class)
    public void whenTermWithEqOperatorNoValue_ThrowSearchTermSearchQueryParseException() {
        SearchableQueryParser.searchTermToPredicate("", "lastName|eq", "");
    }

    /**
     * Test method.
     */
    @Test(expected = SearchTermSearchQueryParseException.class)
    public void whenTermWithNoOperatorNoValue_ThrowSearchTermSearchQueryParseException() {
        SearchableQueryParser.searchTermToPredicate("", "lastName", "");
    }

    /**
     * Test method.
     */
    @Test(expected = SearchTermSearchQueryParseException.class)
    public void whenTermWithNoProvidedOperator_ThrowSearchTermSearchQueryParseException() {
        SearchableQueryParser.searchTermToPredicate("", "lastName|", "");
    }

    /**
     * Test method.
     */
    @Test(expected = SearchTermSearchQueryParseException.class)
    public void whenTermWithNoValueOperatorValueProvided_ThrowSearchTermSearchQueryParseException() {
        SearchableQueryParser.searchTermToPredicate("", "lastName|nn", "DOE");
    }
}
