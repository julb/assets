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

package me.julb.applications.authorizationserver.services;

import javax.validation.constraints.NotNull;

import me.julb.applications.authorizationserver.services.dto.authentication.AbstractUserAuthenticationDTO;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Mono;

/**
 * The generic use for user authentication.
 * <br>
 * @author Julb.
 */
public interface UserAuthenticationGenericService {

    /**
     * Updates the authentication with a successful use.
     * @param userId the user ID.
     * @param id the ID.
     * @return the user authentication updated.
     */
    Mono<AbstractUserAuthenticationDTO> updateSuccessfulUse(@NotNull @Identifier String userId, @NotNull @Identifier String id);

}
