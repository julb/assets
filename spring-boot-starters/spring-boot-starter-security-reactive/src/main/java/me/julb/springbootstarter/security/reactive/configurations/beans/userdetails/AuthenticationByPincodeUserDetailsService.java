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
package me.julb.springbootstarter.security.reactive.configurations.beans.userdetails;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import me.julb.springbootstarter.security.reactive.configurations.beans.userdetails.delegates.IAuthenticationByPincodeUserDetailsDelegateService;

import reactor.core.publisher.Mono;

/**
 * A service used to generate user details for a pincode authentication.
 * <br>
 * @author Julb.
 */
public class AuthenticationByPincodeUserDetailsService implements ReactiveUserDetailsService {

    /**
     * The authentication by pincode delegate service.
     */
    private IAuthenticationByPincodeUserDetailsDelegateService authenticationByPincodeUserDetailsDelegateService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserDetails> findByUsername(String email)
        throws UsernameNotFoundException {
        return authenticationByPincodeUserDetailsDelegateService.loadUserDetailsByPincode(email);
    }

    /**
     * Setter for property authenticationByPincodeUserDetailsDelegateService.
     * @param authenticationByPincodeUserDetailsDelegateService New value of property authenticationByPincodeUserDetailsDelegateService.
     */
    public void setAuthenticationByPincodeUserDetailsDelegateService(IAuthenticationByPincodeUserDetailsDelegateService authenticationByPincodeUserDetailsDelegateService) {
        this.authenticationByPincodeUserDetailsDelegateService = authenticationByPincodeUserDetailsDelegateService;
    }
}
