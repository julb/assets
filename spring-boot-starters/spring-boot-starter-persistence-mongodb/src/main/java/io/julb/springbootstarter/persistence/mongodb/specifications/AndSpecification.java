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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * The search specification that generates a MongoDB query from a searchable object.
 * <P>
 * @author Julb.
 */
public class AndSpecification<T> implements ISpecification<T> {

    /**
     * The specifications.
     */
    private ISpecification<T>[] specifications;

    /**
     * Default constructor.
     * @param specifications the specifications to join.
     */
    @SafeVarargs
    public AndSpecification(ISpecification<T>... specifications) {
        super();
        this.specifications = specifications;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Criteria> toCriteria(Class<T> clazz) {
        List<Criteria> criterias = new ArrayList<Criteria>();
        for (ISpecification<T> specification : specifications) {
            Optional<Criteria> criteria = specification.toCriteria(clazz);
            if (criteria.isPresent()) {
                criterias.add(criteria.get());
            }
        }

        return Optional.of(new Criteria().andOperator(criterias.toArray(new Criteria[0])));
    }
}
