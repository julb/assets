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
package io.julb.springbootstarter.web.resolvers.search;

import io.julb.library.utility.constants.Chars;
import io.julb.library.utility.data.search.SearchRequest;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.data.search.predicates.SearchPredicate;
import io.julb.library.utility.data.search.predicates.attributes.MultipleValuesAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.NoValueAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.OperatorAttributePredicate;
import io.julb.library.utility.data.search.predicates.attributes.SingleValueAttributePredicate;
import io.julb.library.utility.data.search.predicates.joins.AbstractJoinPredicate;
import io.julb.library.utility.data.search.predicates.joins.AndPredicate;
import io.julb.library.utility.data.search.predicates.joins.OrPredicate;
import io.julb.library.utility.data.search.predicates.modifiers.NotPredicate;
import io.julb.springbootstarter.web.resolvers.search.exceptions.SearchQueryParseException;
import io.julb.springbootstarter.web.resolvers.search.exceptions.SearchTermSearchQueryParseException;
import io.julb.springbootstarter.web.resolvers.search.exceptions.UnknownOperatorSearchQueryParseException;
import io.julb.springbootstarter.web.resolvers.search.exceptions.UnknownQueryNodeSearchQueryParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode.Modifier;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.springframework.core.MethodParameter;

/**
 * A search query parser.
 * <P>
 * @author Julb.
 */
abstract class SearchableQueryParser {

    /**
     * The pattern to search for element in search terms.
     */
    private static final Pattern SEARCH_TERM_PATTERN = Pattern.compile("^(?<attributeName>[a-zA-Z0-9\\.]+)(\\|(?<attributeOperator>[a-zA-Z][a-zA-Z]))?$");

    /**
     * Asserts uniqueness of all {@link Searchable} parameters of the method of the given {@link MethodParameter}.
     * @param parameter must not be {@literal null}.
     */
    public static Searchable parse(String searchQuery) {
        try {
            // Empty query => Empty search request.
            if (StringUtils.isBlank(searchQuery)) {
                return new SearchRequest();
            }

            // Parse using lucene parser.
            StandardSyntaxParser standardQueryParser = new StandardSyntaxParser();
            QueryNode queryNode = standardQueryParser.parse(searchQuery, null);
            SearchPredicate predicate = parseQueryNode(searchQuery, queryNode);
            return new SearchRequest(predicate);
        } catch (QueryNodeParseException e) {
            throw new SearchQueryParseException(searchQuery, e);
        }
    }

    /**
     * Parse a Lucene Query node and transform it to a predicate.
     * @param searchQuery the original search query.
     * @param queryNode the query node.
     * @return the predicate.
     */
    protected static SearchPredicate parseQueryNode(String searchQuery, QueryNode queryNode) {
        if (queryNode instanceof FieldQueryNode) {
            FieldQueryNode fieldQueryNode = (FieldQueryNode) queryNode;
            return searchTermToPredicate(searchQuery, fieldQueryNode.getFieldAsString(), fieldQueryNode.getTextAsString());
        } else if (queryNode instanceof BooleanQueryNode) {
            AbstractJoinPredicate joinPredicate = null;
            if (queryNode instanceof OrQueryNode) {
                joinPredicate = new OrPredicate();
            } else {
                joinPredicate = new AndPredicate();
            }
            BooleanQueryNode booleanQueryNode = (BooleanQueryNode) queryNode;
            for (QueryNode childQueryNode : booleanQueryNode.getChildren()) {
                joinPredicate.addPredicate(parseQueryNode(searchQuery, childQueryNode));
            }
            return joinPredicate;
        } else if (queryNode instanceof GroupQueryNode) {
            GroupQueryNode groupQueryNode = (GroupQueryNode) queryNode;
            return parseQueryNode(searchQuery, groupQueryNode.getChild());
        } else if (queryNode instanceof ModifierQueryNode) {
            ModifierQueryNode modifierQueryNode = (ModifierQueryNode) queryNode;
            SearchPredicate parsedQueryNode = parseQueryNode(searchQuery, modifierQueryNode.getChild());
            if (modifierQueryNode.getModifier().equals(Modifier.MOD_NOT)) {
                return new NotPredicate(parsedQueryNode);
            } else {
                return parsedQueryNode;
            }
        } else {
            throw new UnknownQueryNodeSearchQueryParseException(searchQuery, queryNode.toString());
        }

    }

    /**
     * Builds an attribute predicate from a search term.
     * @param originalSearchQuery the original search query.
     * @param term the search term.
     * @return the attribute predicate.
     */
    protected static SearchPredicate searchTermToPredicate(String originalSearchQuery, String fieldName, String fieldValue) {
        String term = StringUtils.join(new String[] {fieldName, fieldValue}, Chars.SEMICOLON);
        Matcher matcher = SEARCH_TERM_PATTERN.matcher(fieldName);
        if (matcher.matches()) {
            String attributeName = matcher.group("attributeName");
            String operatorShortName = matcher.group("attributeOperator");

            // Extract operator reference.
            OperatorAttributePredicate operator = OperatorAttributePredicate.EQUAL; // Default.
            if (operatorShortName != null) {
                operator = OperatorAttributePredicate.fromShortName(operatorShortName);
                if (operator == null) {
                    throw new UnknownOperatorSearchQueryParseException(originalSearchQuery, operatorShortName);
                }
            }

            SearchPredicate predicate = null;
            switch (operator) {
                case EQUAL:
                case NOT_EQUAL:
                case GREATER_OR_EQUAL_THAN:
                case GREATER_THAN:
                case LESS_OR_EQUAL_THAN:
                case LESS_THAN:
                case STARTS_WITH:
                case ENDS_WITH:
                case LIKE:
                case NOT_LIKE:
                    if (StringUtils.isBlank(fieldValue)) {
                        throw new SearchTermSearchQueryParseException(originalSearchQuery, term);
                    }
                    SingleValueAttributePredicate singleValueAttributePredicate = new SingleValueAttributePredicate();
                    singleValueAttributePredicate.setName(attributeName);
                    singleValueAttributePredicate.setOperator(operator);
                    singleValueAttributePredicate.setValue(fieldValue);
                    predicate = singleValueAttributePredicate;
                    break;
                case IN:
                case NOT_IN:
                    if (StringUtils.isBlank(fieldValue)) {
                        throw new SearchTermSearchQueryParseException(originalSearchQuery, term);
                    }
                    MultipleValuesAttributePredicate multipleValuesAttributePredicate = new MultipleValuesAttributePredicate();
                    multipleValuesAttributePredicate.setName(attributeName);
                    multipleValuesAttributePredicate.setOperator(operator);
                    multipleValuesAttributePredicate.setValue(StringUtils.split(fieldValue, Chars.COMMA));
                    predicate = multipleValuesAttributePredicate;
                    break;
                case IS_NULL:
                case IS_NOT_NULL:
                    if (StringUtils.isNotBlank(fieldValue)) {
                        throw new SearchTermSearchQueryParseException(originalSearchQuery, term);
                    }
                    NoValueAttributePredicate noValueAttributePredicate = new NoValueAttributePredicate();
                    noValueAttributePredicate.setName(attributeName);
                    noValueAttributePredicate.setOperator(operator);
                    predicate = noValueAttributePredicate;
                    break;
                default:
                    throw new UnknownOperatorSearchQueryParseException(originalSearchQuery, operator.shortName());
            }

            return predicate;
        } else {
            throw new SearchTermSearchQueryParseException(originalSearchQuery, term);
        }
    }
}
