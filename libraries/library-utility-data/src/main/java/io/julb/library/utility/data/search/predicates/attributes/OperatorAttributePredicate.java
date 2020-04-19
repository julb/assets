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
package io.julb.library.utility.data.search.predicates.attributes;

import org.apache.commons.lang3.StringUtils;

/**
 * The search operator.
 * <P>
 * @author Julb.
 */
public enum OperatorAttributePredicate {

    /**
     * The equal operator.
     */
    EQUAL("eq"),

    /**
     * The notEqual operator.
     */
    NOT_EQUAL("ne"),

    /**
     * The like operator.
     */
    LIKE("lk"),

    /**
     * The notLike operator.
     */
    NOT_LIKE("nl"),

    /**
     * The startsWith operator.
     */
    STARTS_WITH("sw"),

    /**
     * The endsWith operator.
     */
    ENDS_WITH("ew"),

    /**
     * The greaterThan operator.
     */
    GREATER_THAN("gt"),

    /**
     * The greaterOrEqualThan operator.
     */
    GREATER_OR_EQUAL_THAN("ge"),

    /**
     * The lessThan operator.
     */
    LESS_THAN("lt"),

    /**
     * The lessOrEqualThan operator.
     */
    LESS_OR_EQUAL_THAN("le"),

    /**
     * The isNull operator.
     */
    IS_NULL("nu"),

    /**
     * The isNull operator.
     */
    IS_NOT_NULL("nn"),

    /**
     * The in operator.
     */
    IN("in"),

    /**
     * The notIn operator.
     */
    NOT_IN("ni");

    //@formatter:off
     /**
     * The shortName attribute.
     * -- GETTER --
     * Getter for {@link #shortName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #shortName} property.
     * @param shortName the value to set.
     */
     //@formatter:on
    private String shortName;

    /**
     * Constructor.
     * @param shortName the short name.
     */
    private OperatorAttributePredicate(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Getter for property shortName.
     * @return Value of property shortName.
     */
    public String shortName() {
        return shortName;
    }

    /**
     * Builds an operator attribute from the short name.
     * @param shortName the short name.
     * @return the operator attribute.
     */
    public static OperatorAttributePredicate fromShortName(String shortName) {
        if (shortName == null) {
            return null;
        }
        for (OperatorAttributePredicate operator : OperatorAttributePredicate.values()) {
            if (StringUtils.equalsIgnoreCase(shortName, operator.shortName())) {
                return operator;
            }
        }
        return null;
    }
}
