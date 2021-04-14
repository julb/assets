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
package me.julb.springbootstarter.security.services;

import org.springframework.security.core.userdetails.UserDetails;

import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.dto.simple.user.UserRefDTO;

/**
 * The security service description.
 * <P>
 * @author Julb.
 */
public interface ISecurityService {

    /**
     * Gets the current connected user.
     * @return the current connected user.
     */
    UserDetails getConnectedUser();

    /**
     * Gets the current connected user.
     * @return the current connected user.
     */
    String getConnectedUserName();

    /**
     * Gets the current connected user.
     * @return the current connected user.
     */
    AuthenticatedUserDTO getConnectedUserIdentity();

    /**
     * Gets the current connected user.
     * @return the current connected user.
     */
    UserRefDTO getConnectedUserRefIdentity();

    /**
     * Returns the identifier of the connected user, or null if the user is not connected.
     * @return the ID of the connected user.
     */
    String getConnectedUserId();

    /**
     * Returns <code>true</code> if the connected user is authenticated, <code>false</code> otherwise.
     * @return <code>true</code> if the user is authenticated, <code>false</code> otherwise.
     */
    boolean isAuthenticated();

    /**
     * Returns <code>true</code> if the connected user is an administrator, <code>false</code> otherwise.
     * @return <code>true</code> if the connected user is an administrator, <code>false</code> otherwise.
     */
    boolean isAdministrator();

    /**
     * Returns true if the connected user is the required user.
     * @param userId the ID of the user to check.
     * @return <code>true</code> if the user is the connected user.
     */
    boolean isUser(String userId);

}
