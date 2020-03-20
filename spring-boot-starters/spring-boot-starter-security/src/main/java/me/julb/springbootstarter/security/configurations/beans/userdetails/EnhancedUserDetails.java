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

package me.julb.springbootstarter.security.configurations.beans.userdetails;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The user details with multiple passwords.
 * <P>
 * @author Julb.
 */
public interface EnhancedUserDetails extends UserDetails {

    /**
     * Gets the passwords.
     * @return the passwords.
     */
    String[] getPasswords();

    /**
     * Returns <code>true</code> if MFA is enabled for this authentication, <code>false</code> otherwise.
     * @return <code>true</code> if MFA is enabled for this authentication, <code>false</code> otherwise.
     */
    Boolean getMfaEnabled();

    /**
     * Gets the session ID to redeem.
     * @return the session ID to redeem.
     */
    String getMfaSessionId();

    /**
     * {@inheritDoc}
     */
    @Override
    default String getPassword() {
        if (ArrayUtils.isNotEmpty(this.getPasswords())) {
            return this.getPasswords()[0];
        } else {
            return null;
        }
    }
}
