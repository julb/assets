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
package me.julb.springbootstarter.persistence.jpa.specifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.data.search.predicates.SearchPredicate;
import me.julb.library.utility.data.search.predicates.attributes.AbstractAttributePredicate;
import me.julb.library.utility.data.search.predicates.joins.AbstractJoinPredicate;
import me.julb.library.utility.data.search.predicates.modifiers.NotPredicate;
import me.julb.springbootstarter.persistence.core.specifications.AbstractSearchSpecification;

/**
 * The search specification that generates a JPA specification from a searchable object.
 * <br>
 * @author Julb.
 */
public class SearchSpecification<T> extends AbstractSearchSpecification<T> implements Specification<T> {

    /**
     * Constructor.
     * @param searchable the searcheable information.
     */
    public SearchSpecification(Searchable searchable) {
        super(searchable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return genericToJpaPredicate(this.searchable.getSearchPredicate(), criteriaBuilder, root);
    }

    /**
     * Creates the predicates to filter the search.
     * @param predicate the predicate.
     * @param criteriaBuilder the criteria builder.
     * @param from the from criteria.
     * @return the JPA predicates.
     */
    protected Predicate genericToJpaPredicate(SearchPredicate predicate, CriteriaBuilder criteriaBuilder, Root<T> from) {
        if (predicate instanceof AbstractAttributePredicate) {
            return attributeToJpaPredicate((AbstractAttributePredicate) predicate, criteriaBuilder, from);
        } else if (predicate instanceof AbstractJoinPredicate) {
            return joinToJpaPredicate((AbstractJoinPredicate) predicate, criteriaBuilder, from);
        } else if (predicate instanceof NotPredicate) {
            NotPredicate notPredicate = (NotPredicate) predicate;
            return criteriaBuilder.not(genericToJpaPredicate(notPredicate.getPredicate(), criteriaBuilder, from));
        } else {
            return null;
        }
    }

    /**
     * Creates the predicates to filter the search.
     * @param joinPredicate the join predicate.
     * @param criteriaBuilder the criteria builder.
     * @param from the from criteria.
     * @return the JPA predicates.
     */
    protected Predicate joinToJpaPredicate(AbstractJoinPredicate joinPredicate, CriteriaBuilder criteriaBuilder, Root<T> from) {
        List<Predicate> jpaPredicates = new ArrayList<>();
        for (SearchPredicate predicate : joinPredicate.getPredicates()) {
            Predicate jpaPredicate = genericToJpaPredicate(predicate, criteriaBuilder, from);
            if (jpaPredicate != null) {
                jpaPredicates.add(jpaPredicate);
            }
        }

        if (joinPredicate.isAnd()) {
            return criteriaBuilder.and(jpaPredicates.toArray(new Predicate[0]));
        } else if (joinPredicate.isOr()) {
            return criteriaBuilder.or(jpaPredicates.toArray(new Predicate[0]));
        } else {
            return null;
        }
    }

    /**
     * Creates the predicates to filter the search.
     * @param attributePredicate the attribute predicate.
     * @param criteriaBuilder the criteria builder.
     * @param from the from criteria.
     * @return the JPA predicates.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Predicate attributeToJpaPredicate(AbstractAttributePredicate attributePredicate, CriteriaBuilder criteriaBuilder, Root<T> from) {

        // Get the field path.
        Path field = getField(from, attributePredicate.getName());

        // Get the converted value.
        Object convertedValue = convertAttributePredicateValue(attributePredicate, field.getJavaType());

        // Build the JPA predicate
        switch (attributePredicate.getOperator()) {
            case EQUAL:
                if (convertedValue == null) {
                    return criteriaBuilder.isNull(field);
                } else {
                    return criteriaBuilder.equal(field, convertedValue);
                }
            case NOT_EQUAL:
                if (convertedValue == null) {
                    return criteriaBuilder.isNotNull(field);
                } else {
                    return criteriaBuilder.notEqual(field, convertedValue);
                }
            case LIKE:
                return criteriaBuilder.like(criteriaBuilder.lower(field), StringUtils.join(Strings.PERCENT, StringUtils.lowerCase(convertedValue.toString()), Strings.PERCENT));
            case NOT_LIKE:
                return criteriaBuilder.notLike(criteriaBuilder.lower(field), StringUtils.join(Strings.PERCENT, StringUtils.lowerCase(convertedValue.toString()), Strings.PERCENT));
            case STARTS_WITH:
                return criteriaBuilder.like(criteriaBuilder.lower(field), StringUtils.join(StringUtils.lowerCase(convertedValue.toString()), Strings.PERCENT));
            case ENDS_WITH:
                return criteriaBuilder.like(criteriaBuilder.lower(field), StringUtils.join(Strings.PERCENT, StringUtils.lowerCase(convertedValue.toString())));
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(field, (Comparable) convertedValue);
            case GREATER_OR_EQUAL_THAN:
                return criteriaBuilder.greaterThanOrEqualTo(field, (Comparable) convertedValue);
            case LESS_THAN:
                return criteriaBuilder.lessThan(field, (Comparable) convertedValue);
            case LESS_OR_EQUAL_THAN:
                return criteriaBuilder.lessThanOrEqualTo(field, (Comparable) convertedValue);
            case IS_NULL:
                return criteriaBuilder.isNull(field);
            case IS_NOT_NULL:
                return criteriaBuilder.isNotNull(field);
            case IN:
                return field.in((Collection) convertedValue);
            case NOT_IN:
                return criteriaBuilder.not(field.in((Collection) convertedValue));
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
    private <Y> Path<Y> getField(Root<T> from, String attributeName) {
        String[] fields = StringUtils.split(attributeName, Chars.DOT);

        // Iterate on subfields
        Path<Y> result = from.get(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            result = result.get(fields[i]);
        }

        return result;
    }
}
