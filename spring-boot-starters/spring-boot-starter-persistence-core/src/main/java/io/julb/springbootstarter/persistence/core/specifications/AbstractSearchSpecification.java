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
package io.julb.springbootstarter.persistence.core.specifications;

import io.julb.library.utility.data.search.ISearchable;
import io.julb.library.utility.data.search.predicates.attributes.AbstractAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.MultipleValuesAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.NoValueAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.SingleValueAttributePredicate;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The search specification that generates a JPA specification from a searchable object.
 * <P>
 * @author Julb.
 */
public abstract class AbstractSearchSpecification<T> {

    /**
     * The searchable.
     */
    protected ISearchable searchable;

    /**
     * Constructor.
     * @param searchable the searcheable information.
     */
    public AbstractSearchSpecification(ISearchable searchable) {
        this.searchable = searchable;
    }

    /**
     * /** Converts an attribute predicate value to target java type.
     * @param attributePredicate the attribute predicate.
     * @param javaType the java type.
     * @return the converted value.
     */
    protected Object convertAttributePredicateValue(AbstractAttributePredicate attributePredicate, Class<?> javaType) {
        // No value. Skip.
        if (attributePredicate instanceof NoValueAttributePredicate) {
            return null;
        }

        // Single value.
        if (attributePredicate instanceof SingleValueAttributePredicate) {
            return convertStringValueToTargetType(((SingleValueAttributePredicate) attributePredicate).getValue(), javaType);
        }

        // Multiple value.
        if (attributePredicate instanceof MultipleValuesAttributePredicate) {
            String[] values = ((MultipleValuesAttributePredicate) attributePredicate).getValue();
            Collection<Object> inValues = new ArrayList<>();
            for (String value : values) {
                inValues.add(convertStringValueToTargetType(value, javaType));
            }
            return inValues;
        }

        // NOOP
        return null;
    }

    /**
     * Converts a string value to an object of the target class.
     * @param value the value.
     * @param targetClass the target class.
     * @return the object in the targetClass type.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object convertStringValueToTargetType(String value, Class<?> targetClass) {
        // Work only on String value
        if (targetClass.equals(String.class)) {
            return value;
        }

        // Integer
        if (targetClass.equals(Integer.class)) {
            return Integer.parseInt(value);
        }

        // Long
        if (targetClass.equals(Long.class)) {
            return Long.parseLong(value);
        }

        // Double
        if (targetClass.equals(Double.class)) {
            return Double.parseDouble(value);
        }

        // Float
        if (targetClass.equals(Float.class)) {
            return Float.parseFloat(value);
        }

        // Boolean
        if (targetClass.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }

        // Enum
        if (value instanceof String && Enum.class.isAssignableFrom(targetClass)) {
            final Class<? extends Enum> enumType = (Class<? extends Enum>) targetClass;
            return Enum.valueOf(enumType, value);
        }

        // Other
        return value;
    }
}
