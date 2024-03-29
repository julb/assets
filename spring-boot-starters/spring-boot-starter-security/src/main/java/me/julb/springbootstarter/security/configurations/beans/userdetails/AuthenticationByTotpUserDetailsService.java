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
package me.julb.springbootstarter.security.configurations.beans.userdetails;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Integers;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationByTotpUserDetailsDelegateService;

/**
 * A service used to generate user details for a TOTP authentication.
 * <br>
 * @author Julb.
 */
public class AuthenticationByTotpUserDetailsService implements UserDetailsService {

    /**
     * The authentication by TOTP delegate service.
     */
    private IAuthenticationByTotpUserDetailsDelegateService authenticationByTotpUserDetailsDelegateService;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String userName)
        throws UsernameNotFoundException {
        // Extract username information.
        String[] totpUserNameParts = StringUtils.split(userName, Chars.DOT);
        if (totpUserNameParts.length != Integers.THREE) {
            throw new UsernameNotFoundException(userName);
        }

        // Call service.
        String userId = totpUserNameParts[0];
        String sessionId = totpUserNameParts[1];
        String deviceId = totpUserNameParts[2];
        return authenticationByTotpUserDetailsDelegateService.loadUserDetailsByTotp(userId, sessionId, deviceId);

    }

    /**
     * Setter for property authenticationByTotpUserDetailsDelegateService.
     * @param authenticationByTotpUserDetailsDelegateService New value of property authenticationByTotpUserDetailsDelegateService.
     */
    public void setAuthenticationByTotpUserDetailsDelegateService(IAuthenticationByTotpUserDetailsDelegateService authenticationByTotpUserDetailsDelegateService) {
        this.authenticationByTotpUserDetailsDelegateService = authenticationByTotpUserDetailsDelegateService;
    }
}
