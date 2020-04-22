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
package io.julb.applications.bookmark.repositories.specifications;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.julb.applications.bookmark.entities.AbstractItemEntity;
import io.julb.applications.bookmark.services.dto.ItemType;
import io.julb.springbootstarter.persistence.mongodb.specifications.ISpecification;

import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Specification on items.
 * <P>
 * @author Julb.
 */
public class ItemOfGivenTypeSpecification<T extends AbstractItemEntity> implements ISpecification<T> {

    /**
     * The item type.
     */
    private ItemType type;

    /**
     * Default constructor.
     * @param type the type.
     */
    public ItemOfGivenTypeSpecification(ItemType type) {
        super();
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Criteria> toCriteria(Class<T> rootClass) {
        return Optional.of(where("type").is(this.type));
    }
}
