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
package io.julb.springbootstarter.security.services.dto;

import io.julb.library.dto.security.AuthenticatedUserDTO;
import io.julb.library.dto.security.AuthenticatedUserIdentityDTO;
import io.julb.library.dto.security.AuthenticatedUserRole;
import io.julb.library.dto.security.AuthenticatedUserTokenType;
import io.julb.springbootstarter.security.utilities.RoleUtility;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

        // Add the roles.
        for (AuthenticatedUserRole role : this.authenticatedUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(RoleUtility.toAuthorityName(role.toString())));
        }
        authorities.add(new SimpleGrantedAuthority(RoleUtility.toAuthorityName(this.authenticatedUser.getType().toString())));

        // Add the permissions.
        for (String permission : this.authenticatedUser.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(permission));
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
        return this.authenticatedUser.getIdentity().getUserName();
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
        return this.authenticatedUser.getEnabled();
    }

    /**
     * Gets the identity of the authenticated user.
     * @return the identity.
     */
    public AuthenticatedUserIdentityDTO getIdentity() {
        return this.authenticatedUser.getIdentity();
    }

    /**
     * Returns <code>true</code> if the current user is administrator, <code>false</code> otherwise.
     * @return <code>true</code> if the current user is administrator, <code>false</code> otherwise.
     */
    public boolean isAdministrator() {
        return this.authenticatedUser.getRoles().contains(AuthenticatedUserRole.ADMINISTRATOR);
    }

    /**
     * Returns <code>true</code> if the current user is developer, <code>false</code> otherwise.
     * @return <code>true</code> if the current user is developer, <code>false</code> otherwise.
     */
    public boolean isDeveloper() {
        return this.authenticatedUser.getRoles().contains(AuthenticatedUserRole.DEVELOPER);
    }

    /**
     * Returns <code>true</code> if the current user is a U2M, <code>false</code> otherwise.
     * @return <code>true</code> if the current user is a U2M, <code>false</code> otherwise.
     */
    public boolean isUser2Machine() {
        return AuthenticatedUserTokenType.U2M.equals(this.authenticatedUser.getType());
    }

    /**
     * Returns <code>true</code> if the current user is a U2M, <code>false</code> otherwise.
     * @return <code>true</code> if the current user is a U2M, <code>false</code> otherwise.
     */
    public boolean isMachine2Machine() {
        return AuthenticatedUserTokenType.M2M.equals(this.authenticatedUser.getType());
    }
}
