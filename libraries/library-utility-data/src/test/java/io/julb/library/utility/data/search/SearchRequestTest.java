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
package io.julb.library.utility.data.search;

import io.julb.library.utility.data.search.predicates.attributes.MultipleValuesAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.NoValueAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.OperatorAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.SingleValueAttributePredicate;
import io.julb.library.utility.data.search.predicates.joins.AndPredicate;
import io.julb.library.utility.data.search.predicates.joins.OrPredicate;
import io.julb.library.utility.data.search.predicates.modifiers.NotPredicate;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link SearchRequest} class.
 * <P>
 * @author Julb.
 */
public class SearchRequestTest {

    /**
     * Test method.
     */
    @Test
    public void whenToStringEmpty_thenReturnsEmptyString() {
        Assert.assertEquals(StringUtils.EMPTY, Searchable.empty().toString());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithSingleValueEqOperator_thenReturnValidString() {
        SingleValueAttributePredicate predicate = new SingleValueAttributePredicate();
        predicate.setName("lastName");
        predicate.setOperator(OperatorAttributePredicate.EQUAL);
        predicate.setValue("John DOE");
        Assert.assertEquals("lastName|eq:\"John DOE\"", predicate.toString());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithMultipleValueInOperator_thenReturnValidString() {
        MultipleValuesAttributePredicate predicate = new MultipleValuesAttributePredicate();
        predicate.setName("lastName");
        predicate.setOperator(OperatorAttributePredicate.IN);
        predicate.setValue(new String[] {"John DOE", "Alice DOE"});
        Assert.assertEquals("lastName|in:\"John DOE,Alice DOE\"", predicate.toString());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNoValueNotNullOperator_thenReturnValidString() {
        NoValueAttributePredicate predicate = new NoValueAttributePredicate();
        predicate.setName("lastName");
        predicate.setOperator(OperatorAttributePredicate.IS_NOT_NULL);
        Assert.assertEquals("lastName|nn:", predicate.toString());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNotValueOperator_thenReturnValidString() {
        NoValueAttributePredicate predicate = new NoValueAttributePredicate();
        predicate.setName("lastName");
        predicate.setOperator(OperatorAttributePredicate.IS_NOT_NULL);
        Assert.assertEquals("!lastName|nn:", new NotPredicate(predicate).toString());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithAndValueOperator_thenReturnValidString() {
        SingleValueAttributePredicate predicate1 = new SingleValueAttributePredicate();
        predicate1.setName("lastName");
        predicate1.setOperator(OperatorAttributePredicate.EQUAL);
        predicate1.setValue("John DOE");

        SingleValueAttributePredicate predicate2 = new SingleValueAttributePredicate();
        predicate2.setName("firstName");
        predicate2.setOperator(OperatorAttributePredicate.EQUAL);
        predicate2.setValue("Alice DOE");

        Assert.assertEquals("(lastName|eq:\"John DOE\" AND firstName|eq:\"Alice DOE\")", new AndPredicate(predicate1, predicate2).toString());
    }

    /**
     * Test method.
     */
    @Test
    public void whenTermWithNestedJoinValueOperator_thenReturnValidString() {
        SingleValueAttributePredicate predicate1 = new SingleValueAttributePredicate();
        predicate1.setName("lastName");
        predicate1.setOperator(OperatorAttributePredicate.EQUAL);
        predicate1.setValue("John DOE");

        SingleValueAttributePredicate predicate2 = new SingleValueAttributePredicate();
        predicate2.setName("firstName");
        predicate2.setOperator(OperatorAttributePredicate.EQUAL);
        predicate2.setValue("Alice DOE");

        SingleValueAttributePredicate predicate3 = new SingleValueAttributePredicate();
        predicate3.setName("age");
        predicate3.setOperator(OperatorAttributePredicate.GREATER_OR_EQUAL_THAN);
        predicate3.setValue("20");

        Assert.assertEquals("((lastName|eq:\"John DOE\" AND firstName|eq:\"Alice DOE\") OR age|ge:\"20\")", new OrPredicate(new AndPredicate(predicate1, predicate2), predicate3).toString());
    }
}
