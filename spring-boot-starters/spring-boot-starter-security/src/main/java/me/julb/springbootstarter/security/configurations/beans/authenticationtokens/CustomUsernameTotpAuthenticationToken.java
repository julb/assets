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

package me.julb.springbootstarter.security.configurations.beans.authenticationtokens;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import me.julb.library.utility.constants.Chars;

/**
 * A authentication token used to authenticate a user with its TOTP.
 * <br>
 * @author Julb.
 */
public class CustomUsernameTotpAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * Default constructor.
     * @param userId the user id.
     * @param sessionId the session id.
     * @param deviceId the deviceId.
     * @param totp the totp.
     */
    public CustomUsernameTotpAuthenticationToken(String userId, String sessionId, String deviceId, String totp) {
        super(StringUtils.join(new String[] {userId, sessionId, deviceId}, Chars.DOT), totp);
    }

}
