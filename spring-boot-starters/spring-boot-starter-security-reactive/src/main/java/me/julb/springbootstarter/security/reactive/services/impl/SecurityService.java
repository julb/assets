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
package me.julb.springbootstarter.security.reactive.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.dto.security.LocalAuthenticatedUserDTO;
import me.julb.library.dto.security.UserRole;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.springbootstarter.security.reactive.services.ISecurityService;
import me.julb.springbootstarter.security.services.dto.CustomUserDetails;
import me.julb.springbootstarter.security.utilities.RoleUtility;

import reactor.core.publisher.Mono;

/**
 * The security service implementation.
 * <br>
 * @author Julb.
 */
@Service
public class SecurityService implements ISecurityService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDetails> getConnectedUser() {
        return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated())
                    .map(authentication -> (UserDetails) authentication.getPrincipal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<String> getConnectedUserName() {
        return getConnectedUser().map(UserDetails::getUsername);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<AuthenticatedUserDTO> getConnectedUserIdentity() {
        return getConnectedUser().map(userDetails -> {
            if (userDetails instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getDetails();
            } else {
                return new LocalAuthenticatedUserDTO(userDetails.getUsername());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserRefDTO> getConnectedUserRefIdentity() {
        return getConnectedUserIdentity().map(connectedUser -> {
            UserRefDTO userRefDto = new UserRefDTO();
            userRefDto.setDisplayName(connectedUser.getDisplayName());
            userRefDto.setE164Number(connectedUser.getE164Number());
            userRefDto.setFirstName(connectedUser.getFirstName());
            userRefDto.setId(connectedUser.getUserId());
            userRefDto.setLastName(connectedUser.getLastName());
            userRefDto.setLocale(connectedUser.getLocale());
            userRefDto.setMail(connectedUser.getMail());
            return userRefDto;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<String> getConnectedUserId() {
        return getConnectedUserIdentity().map(AuthenticatedUserDTO::getUserId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> isAuthenticated() {
        return ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .map(authentication -> !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated())
                    .switchIfEmpty(Mono.just(false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> isAdministrator() {
        String autorityName = RoleUtility.toAuthorityName(UserRole.ADMINISTRATOR.toString());
        return getConnectedUser()
                    .flatMapIterable(UserDetails::getAuthorities)
                    .any(authority -> StringUtils.equalsIgnoreCase(autorityName, authority.getAuthority()))
                    .switchIfEmpty(Mono.just(false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Boolean> isUser(String userId) {
        return getConnectedUserId().map(connectedUserId -> StringUtils.equalsIgnoreCase(connectedUserId, userId));
    }
}
