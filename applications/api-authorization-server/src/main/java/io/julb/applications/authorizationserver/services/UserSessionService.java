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

import io.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFirstCreationDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenFromIdTokenCreationDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionCreationDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionCredentialsDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionPatchDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionUpdateDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionWithRawIdTokenDTO;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.validator.constraints.Identifier;
import io.julb.library.utility.validator.constraints.SecureIdToken;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The user session service.
 * <P>
 * @author Julb.
 */
public interface UserSessionService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available sessions (paged).
     * @param userId the user identifier.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of sessions.
     */
    Page<UserSessionDTO> findAll(@NotNull @Identifier String userId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a session through its ID.
     * @param userId the user identifier.
     * @param id the session identifier.
     * @return the session.
     */
    UserSessionDTO findOne(@NotNull @Identifier String userId, @NotNull @Identifier String id);

    /**
     * Gets the credentials of the user.
     * @param rawIdToken the raw ID token
     * @return the DTO holding the credentials.
     */
    UserSessionCredentialsDTO findOneCredentials(@NotNull @NotBlank @SecureIdToken String rawIdToken);

    // ------------------------------------------ Write methods.

    /**
     * Creates a session.
     * @param userId the user identifier.
     * @param sessionCreationDTO the DTO to create a session.
     * @return the created session.
     */
    UserSessionWithRawIdTokenDTO create(@NotNull @Identifier String userId, @NotNull @Valid UserSessionCreationDTO sessionCreationDTO);

    /**
     * Generates an access token from an id token.
     * @param accessTokenCreation the access token creation.
     * @return the access token.
     */
    UserSessionAccessTokenDTO createAccessTokenFromIdToken(@NotNull @Valid UserSessionAccessTokenFromIdTokenCreationDTO accessTokenCreation);

    /**
     * Generates an access token for a user.
     * @param userId the user identifier.
     * @param id the session identifier.
     * @param accessTokenCreation the access token creation.
     * @return the access token.
     */
    UserSessionAccessTokenDTO createAccessTokenFirst(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionAccessTokenFirstCreationDTO accessTokenCreation);

    /**
     * Updates a session.
     * @param userId the user identifier.
     * @param id the session identifier.
     * @param sessionUpdateDTO the DTO to update a session.
     * @return the updated session.
     */
    UserSessionDTO update(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionUpdateDTO sessionUpdateDTO);

    /**
     * Patches a session.
     * @param userId the user identifier.
     * @param id the session identifier.
     * @param sessionPatchDTO the DTO to update a session.
     * @return the updated session.
     */
    UserSessionDTO patch(@NotNull @Identifier String userId, @NotNull @Identifier String id, @NotNull @Valid UserSessionPatchDTO sessionPatchDTO);

    /**
     * Mark MFA as verified on a session.
     * @param userId the user identifier.
     * @param id the session identifier.
     * @return the updated session.
     */
    UserSessionDTO markMfaAsVerified(@NotNull @Identifier String userId, @NotNull @Identifier String id);

    /**
     * Deletes all sessions of a user.
     * @param userId the user identifier.
     */
    void delete(@NotNull @Identifier String userId);

    /**
     * Deletes a session.
     * @param userId the user identifier.
     * @param id the id of the session to delete.
     */
    void delete(@NotNull @Identifier String userId, @NotNull @Identifier String id);

}
