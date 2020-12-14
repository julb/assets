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

package me.julb.springbootstarter.security.configurations.beans.providers;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import me.julb.springbootstarter.security.configurations.beans.userdetails.EnhancedUserDetails;

/**
 * The base DAO authentication provider supporting multiple passwords.
 * <P>
 * @author Julb.
 */
public class AbstractDaoAuthenticationProvider extends DaoAuthenticationProvider {
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Overridden methods.

    /**
     * {@inheritDoc}
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        if (userDetails instanceof EnhancedUserDetails) {
            // If no credentials passed, reject request.
            if (authentication.getCredentials() == null) {
                logger.debug("Authentication failed: no credentials provided");
                throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }

            // Presented TOTP.
            String presentedCredentials = authentication.getCredentials().toString();

            // Passwords to check.
            String[] passwords = ((EnhancedUserDetails) userDetails).getPasswords();

            // Check if any matches.
            boolean anyMatches = false;
            for (String password : passwords) {
                if (getPasswordEncoder().matches(presentedCredentials, password)) {
                    anyMatches = true;
                }
            }

            if (!anyMatches) {
                logger.debug("Authentication failed: password does not match stored value");
                throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        } else {
            super.additionalAuthenticationChecks(userDetails, authentication);
        }

    }
}
