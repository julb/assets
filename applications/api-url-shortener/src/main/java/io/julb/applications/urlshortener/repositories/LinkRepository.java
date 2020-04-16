/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package io.julb.applications.urlshortener.repositories;

import io.julb.applications.urlshortener.entities.LinkEntity;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The link repository.
 * <P>
 * @author Julb.
 */
public interface LinkRepository extends MongoRepository<LinkEntity, String>, MongoSpecificationExecutor<LinkEntity> {

    /**
     * Finds a link by trademark and id.
     * @param tm the trademark.
     * @param id the id.
     * @return the link, or <code>null</code> if not exists.
     */
    LinkEntity findByTmAndId(String tm, String id);

    /**
     * Finds a link by trademark, host and URI.
     * @param tm the trademark.
     * @param host the host.
     * @param uri the URI.
     * @return the link if found, <code>null</code> otherwise.
     */
    LinkEntity findByTmAndHostIgnoreCaseAndUriIgnoreCaseAndEnabledIsTrue(String tm, String host, String uri);

    /**
     * Checks if a link already exists.
     * @param tm the trademark.
     * @param host the host.
     * @param uri the URI.
     * @return <code>true</code> if a link exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndHostIgnoreCaseAndUriIgnoreCase(String tm, String host, String uri);

    /**
     * Checks if a link already exists, excluding the given ID.
     * @param tm the trademark.
     * @param id the id.
     * @param host the host.
     * @param uri the URI.
     * @return <code>true</code> if a link exists, <code>false</code> otherwise.
     */
    boolean existsByTmAndIdNotAndHostIgnoreCaseAndUriIgnoreCase(String tm, String id, String host, String uri);

}
