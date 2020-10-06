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
package me.julb.springbootstarter.security.services.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.dto.security.TechnicalRole;
import me.julb.library.dto.security.UserRole;
import me.julb.springbootstarter.security.utilities.RoleUtility;

/**
 * The custom user details.
 * <P>
 * @author Julb.
 */
public class CustomUserDetails implements UserDetails {

    /**
     * The authenticated user DTO.
     */
    private AuthenticatedUserDTO authenticatedUser;

    /**
     * Constructor.
     * @param authenticatedUser the authenticated user.
     */
    public CustomUserDetails(AuthenticatedUserDTO authenticatedUser) {
        super();
        this.authenticatedUser = authenticatedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (authenticatedUser.getMfaVerified()) {
            // Add roles.
            for (UserRole role : this.authenticatedUser.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(RoleUtility.toAuthorityName(role.toString())));
            }

            // Add local permissions.
            for (String permission : this.authenticatedUser.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        } else {
            // Add MFA required role
            authorities.add(new SimpleGrantedAuthority(RoleUtility.toAuthorityName(TechnicalRole.MFA_REQUIRED.toString())));
        }

        return authorities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return "N/A";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return this.authenticatedUser.getMail();
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
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets the identity of the authenticated user.
     * @return the identity.
     */
    public AuthenticatedUserDTO getDetails() {
        return this.authenticatedUser;
    }

    /**
     * Returns <code>true</code> if the current user is administrator, <code>false</code> otherwise.
     * @return <code>true</code> if the current user is administrator, <code>false</code> otherwise.
     */
    public boolean isAdministrator() {
        return this.authenticatedUser.getRoles().contains(UserRole.ADMINISTRATOR);
    }
}
