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
package io.julb.springbootstarter.security.services.impl;

import io.julb.library.dto.security.AuthenticatedUserIdentityDTO;
import io.julb.library.dto.security.AuthenticatedUserRole;
import io.julb.springbootstarter.security.services.ISecurityService;
import io.julb.springbootstarter.security.services.dto.CustomUserDetails;
import io.julb.springbootstarter.security.utilities.RoleUtility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * The security service implementation.
 * <P>
 * @author Julb.
 */
@Service
public class SecurityService implements ISecurityService {

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails getConnectedUser() {
        if (isAuthenticated()) {
            return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConnectedUserName() {
        UserDetails userDetails = getConnectedUser();
        if (userDetails != null) {
            return userDetails.getUsername();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthenticatedUserIdentityDTO getConnectedUserIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object userDetails = authentication.getPrincipal();
            if (userDetails instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                return customUserDetails.getIdentity();
            } else if (userDetails instanceof UserDetails) {
                // FIXME
                return new AuthenticatedUserIdentityDTO();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConnectedUserId() {
        AuthenticatedUserIdentityDTO identity = getConnectedUserIdentity();
        if (identity != null) {
            return identity.getId();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdministrator() {
        String autorityName = RoleUtility.toAuthorityName(AuthenticatedUserRole.ADMINISTRATOR.toString());
        UserDetails userDetails = getConnectedUser();
        if (userDetails != null) {
            for (GrantedAuthority authority : userDetails.getAuthorities()) {
                if (StringUtils.equalsIgnoreCase(autorityName, authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUser(String userId) {
        return StringUtils.equalsIgnoreCase(this.getConnectedUserId(), userId);
    }
}