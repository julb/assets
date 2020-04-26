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

package io.julb.applications.authorizationserver.services;

import io.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The my session service.
 * <P>
 * @author Julb.
 */
public interface MySessionService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available sessions (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of sessions.
     */
    Page<UserSessionDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a session through its ID.
     * @param id the session identifier.
     * @return the session.
     */
    UserSessionDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Deletes all sessions of the user.
     */
    void delete();

    /**
     * Deletes a session.
     * @param id the id of the session to delete.
     */
    void delete(@NotNull @Identifier String id);
}
