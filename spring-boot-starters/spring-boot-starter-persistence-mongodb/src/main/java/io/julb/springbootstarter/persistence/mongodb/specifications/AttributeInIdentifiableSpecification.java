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
package io.julb.springbootstarter.persistence.mongodb.specifications;

import io.julb.library.utility.interfaces.IIdentifiable;
import io.julb.library.utility.interfaces.IdentifiableUtility;

import java.util.Collection;

/**
 * Specification on identifiers "in".
 * <P>
 * @author Julb.
 */
public class AttributeInIdentifiableSpecification<T> extends AttributeInSpecification<T> {

    /**
     * Default constructor.
     * @param attributeName the attribute name.
     * @param identifiableObjects the identifiable objects.
     */
    public AttributeInIdentifiableSpecification(String attributeName, Collection<? extends IIdentifiable> identifiableObjects) {
        super(attributeName, IdentifiableUtility.mapAsString(identifiableObjects));
    }
}
