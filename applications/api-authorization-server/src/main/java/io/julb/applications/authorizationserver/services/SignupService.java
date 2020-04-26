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

import io.julb.applications.authorizationserver.services.dto.signup.SignupWithInviteDTO;
import io.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import io.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import io.julb.applications.authorizationserver.services.dto.user.UserDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The signup service.
 * <P>
 * @author Julb.
 */
public interface SignupService {

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * Sign-up the user basing on invite data.
     * @param signupWithInvite the DTO to invite a user.
     * @return the created user.
     */
    UserDTO signup(@NotNull @Valid SignupWithInviteDTO signupWithInvite);

    /**
     * Sign-up the user with password data.
     * @param signupWithPassword the DTO to sign-up a user.
     * @return the created user.
     */
    UserDTO signup(@NotNull @Valid SignupWithPasswordCreationDTO signupWithPassword);

    /**
     * Sign-up the user with pincode data.
     * @param signupWithPincode the DTO to sign-up a user.
     * @return the created user.
     */
    UserDTO signup(@NotNull @Valid SignupWithPincodeCreationDTO signupWithPincode);

}
