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
package me.julb.springbootstarter.persistence.mongodb.reactive.specifications;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.data.search.predicates.SearchPredicate;
import me.julb.library.utility.data.search.predicates.attributes.AbstractAttributePredicate;
import me.julb.library.utility.data.search.predicates.joins.AbstractJoinPredicate;
import me.julb.library.utility.data.search.predicates.modifiers.NotPredicate;
import me.julb.library.utility.exceptions.BadRequestException;
import me.julb.springbootstarter.persistence.core.specifications.AbstractSearchSpecification;

/**
 * The search specification that generates a MongoDB query from a searchable object.
 * <br>
 * @author Julb.
 */
public class SearchSpecification<T> extends AbstractSearchSpecification<T> implements ISpecification<T> {

    /**
     * Default constructor.
     * @param searchable the searchable object.
     */
    public SearchSpecification(Searchable searchable) {
        super(searchable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Criteria> toCriteria(Class<T> rootClass) {
        return Optional.ofNullable(genericToMongoDbCriteria(searchable.getSearchPredicate(), rootClass));
    }

    /**
     * Creates the criteria definition to filter the search.
     * @param predicate the search predicate.
     * @param rootClass the root class.
     * @return the MongoDB criteria definition.
     */
    private Criteria genericToMongoDbCriteria(SearchPredicate predicate, Class<T> rootClass) {
        if (predicate instanceof AbstractAttributePredicate) {
            return attributeToMongoDbCriteria((AbstractAttributePredicate) predicate, rootClass);
        } else if (predicate instanceof AbstractJoinPredicate) {
            return joinToMongoDbCriteria((AbstractJoinPredicate) predicate, rootClass);
        } else if (predicate instanceof NotPredicate) {
            NotPredicate notPredicate = (NotPredicate) predicate;
            return new Criteria().norOperator(genericToMongoDbCriteria(notPredicate.getPredicate(), rootClass));
        } else {
            return null;
        }
    }

    /**
     * Creates the criteria definition to filter the search.
     * @param joinPredicate the search predicate.
     * @param rootClass the root class.
     * @return the MongoDB criteria definition.
     */
    protected Criteria joinToMongoDbCriteria(AbstractJoinPredicate joinPredicate, Class<T> rootClass) {
        List<Criteria> criteria = new ArrayList<>();
        for (SearchPredicate predicate : joinPredicate.getPredicates()) {
            Criteria jpaPredicate = genericToMongoDbCriteria(predicate, rootClass);
            if (jpaPredicate != null) {
                criteria.add(jpaPredicate);
            }
        }

        if (criteria.isEmpty()) {
            return null;
        }

        if (joinPredicate.isAnd()) {
            return new Criteria().andOperator(criteria.toArray(new Criteria[0]));
        } else if (joinPredicate.isOr()) {
            return new Criteria().orOperator(criteria.toArray(new Criteria[0]));
        } else {
            return null;
        }
    }

    /**
     * Creates the predicates to filter the search.
     * @param attributePredicate the attribute predicate.
     * @param rootClass the root class.
     * @return the Mongo predicate.
     */
    @SuppressWarnings({"rawtypes"})
    protected Criteria attributeToMongoDbCriteria(AbstractAttributePredicate attributePredicate, Class<T> rootClass) {
        // Get root type.
        Class<?> fieldType = getFieldType(rootClass, attributePredicate.getName());
        if (fieldType == null) {
            throw new BadRequestException(String.format("Attribute <%s> not found.", attributePredicate.getName()));
        }

        // Get the converted value.
        Object convertedValue = convertAttributePredicateValue(attributePredicate, fieldType);

        // Build the JPA predicate
        switch (attributePredicate.getOperator()) {
            case EQUAL:
                if (convertedValue == null) {
                    return new Criteria().orOperator(where(attributePredicate.getName()).exists(false), where(attributePredicate.getName()).is(null));
                } else {
                    return where(attributePredicate.getName()).is(convertedValue);
                }
            case NOT_EQUAL:
                if (convertedValue == null) {
                    return new Criteria().andOperator(where(attributePredicate.getName()).exists(true), where(attributePredicate.getName()).ne(null));
                } else {
                    return where(attributePredicate.getName()).ne(convertedValue);
                }
            case LIKE:
                return where(attributePredicate.getName()).regex(Pattern.compile(StringUtils.lowerCase(convertedValue.toString()), Pattern.CASE_INSENSITIVE));
            case NOT_LIKE:
                return where(attributePredicate.getName()).not().regex(Pattern.compile(StringUtils.lowerCase(convertedValue.toString()), Pattern.CASE_INSENSITIVE));
            case STARTS_WITH:
                return where(attributePredicate.getName()).regex(Pattern.compile(Chars.CIRCUMFLEX + StringUtils.lowerCase(convertedValue.toString()), Pattern.CASE_INSENSITIVE));
            case ENDS_WITH:
                return where(attributePredicate.getName()).regex(Pattern.compile(StringUtils.lowerCase(convertedValue.toString()) + Chars.DOLLAR, Pattern.CASE_INSENSITIVE));
            case GREATER_THAN:
                return where(attributePredicate.getName()).gt(convertedValue);
            case GREATER_OR_EQUAL_THAN:
                return where(attributePredicate.getName()).gte(convertedValue);
            case LESS_THAN:
                return where(attributePredicate.getName()).lt(convertedValue);
            case LESS_OR_EQUAL_THAN:
                return where(attributePredicate.getName()).lte(convertedValue);
            case IS_NULL:
                return new Criteria().orOperator(where(attributePredicate.getName()).exists(false), where(attributePredicate.getName()).is(null));
            case IS_NOT_NULL:
                return new Criteria().andOperator(where(attributePredicate.getName()).exists(true), where(attributePredicate.getName()).ne(null));
            case IN:
                return where(attributePredicate.getName()).in((Collection) convertedValue);
            case NOT_IN:
                return where(attributePredicate.getName()).nin((Collection) convertedValue);
            default:
                return null;
        }
    }

    /**
     * Extracts the field related to its name, handling "field" and "field.subField".
     * @param from the query.
     * @param attributeName the attribute name.
     * @return the JPA path.
     */
    private Class<?> getFieldType(Class<? super T> clazz, String fullPath) {
        List<String> fields = Arrays.asList(StringUtils.split(fullPath, Chars.DOT));

        Iterator<String> fieldIterator = fields.iterator();
        Field currentField = null;
        Class<?> currentClass = clazz;

        do {
            String fieldName = fieldIterator.next();
            currentField = getClassField(currentClass, fieldName);
            if (currentField == null) {
                return null;
            }

            if (Collection.class.isAssignableFrom(currentField.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) currentField.getGenericType();
                currentClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            } else if (Map.class.isAssignableFrom(currentField.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) currentField.getGenericType();
                // Skip key because we are not interested in its type.
                if (fieldIterator.hasNext()) {
                    fieldIterator.next();
                }

                // Extract value.
                currentClass = (Class<?>) parameterizedType.getActualTypeArguments()[1];
            } else {
                currentClass = currentField.getType();
            }
        } while (fieldIterator.hasNext());

        return currentClass;
    }

    /**
     * Gets a field including superclasses.
     * @param c the class object.
     * @param fieldName the field name.
     * @return the field.
     */
    public Field getClassField(Class<?> c, String fieldName) {
        // Return null if no class.
        if (c == null) {
            return null;
        }

        // Return fields.
        Field result = null;
        try {
            result = c.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsfe) {
            Class<?> sc = c.getSuperclass();
            result = getClassField(sc, fieldName);
        }
        return result;
    }
}
