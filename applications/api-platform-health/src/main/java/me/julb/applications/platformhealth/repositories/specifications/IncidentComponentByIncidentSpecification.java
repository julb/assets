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
package me.julb.applications.platformhealth.repositories.specifications;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;

import me.julb.applications.platformhealth.entities.IncidentEntity;
import me.julb.applications.platformhealth.entities.IncidentComponentEntity;
import me.julb.springbootstarter.persistence.mongodb.reactive.specifications.ISpecification;

/**
 * Specification on incident component.
 * <br>
 * @author Julb.
 */
public class IncidentComponentByIncidentSpecification implements ISpecification<IncidentComponentEntity> {

    /**
     * The incident.
     */
    private IncidentEntity incident;

    /**
     * Default constructor.
     * @param incident the incident.
     */
    public IncidentComponentByIncidentSpecification(IncidentEntity incident) {
        super();
        this.incident = incident;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Criteria> toCriteria(Class<IncidentComponentEntity> rootClass) {
        return Optional.of(where("incident").is(this.incident.getId()));
    }
}