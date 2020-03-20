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

package me.julb.applications.authorizationserver.services.dto.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationCredentialsDTO;
import me.julb.library.dto.security.TechnicalRole;
import me.julb.library.dto.security.UserRole;
import me.julb.springbootstarter.security.configurations.beans.userdetails.EnhancedUserDetails;
import me.julb.springbootstarter.security.utilities.RoleUtility;

/**
 * The user details holding authentication.
 * <P>
 * @author Julb.
 */
public class UserAuthenticationUserDetailsDTO implements EnhancedUserDetails {

    /**
     * The credentials.
     */
    private UserAuthenticationCredentialsDTO credentials;

    /**
     * The MFA session ID.
     */
    private String mfaSessionId;

    /**
     * Default constructor.
     * @param credentials the credentials.
     */
    public UserAuthenticationUserDetailsDTO(UserAuthenticationCredentialsDTO credentials) {
        super();
        this.credentials = credentials;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return this.credentials.getUser().getMail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return this.credentials.getUser().getEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.credentials.getUser().getAccountNonLocked();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentials.getCredentialsNonExpired();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roles = new HashSet<>();

        if (getMfaEnabled()) {
            roles.add(TechnicalRole.MFA_REQUIRED.toString());
        } else {
            roles.add(TechnicalRole.FULLY_AUTHENTICATED.toString());
            for (UserRole role : this.credentials.getUser().getRoles()) {
                roles.add(role.toString());
            }
        }

        //@formatter:off
        return roles.stream()
            .map(RoleUtility::toAuthorityName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        //@formatter:on
    }

    /**
     * Getter for property credentials.
     * @return Value of property credentials.
     */
    public UserAuthenticationCredentialsDTO getCredentials() {
        return credentials;
    }

    /**
     * Gets the possible passwords for this authentication.
     * @return the valid passwords for this authentication.
     */
    @Override
    public String[] getPasswords() {
        return this.credentials.getCredentials();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getMfaEnabled() {
        return this.credentials.getUserAuthentication().getMfaEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMfaSessionId() {
        return this.mfaSessionId;
    }

    /**
     * Setter for property mfaSessionId.
     * @param mfaSessionId New value of property mfaSessionId.
     */
    public void setMfaSessionId(String mfaSessionId) {
        this.mfaSessionId = mfaSessionId;
    }
}
