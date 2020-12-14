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
package me.julb.springbootstarter.security.configurations.beans.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsHandlerDelegate;

/**
 * A custom handler for successful authentication.
 * <P>
 * @author Julb.
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * The authentication user details delegate service.
     */
    private IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException,
        ServletException {
        if (authenticationUserDetailsDelegateService != null) {
            authenticationUserDetailsDelegateService.onAuthenticationSuccess(request, response, authentication);
        }
    }

    /**
     * Setter for property authenticationUserDetailsDelegateService.
     * @param authenticationUserDetailsDelegateService New value of property authenticationUserDetailsDelegateService.
     */
    public void setAuthenticationUserDetailsDelegateService(IAuthenticationUserDetailsHandlerDelegate authenticationUserDetailsDelegateService) {
        this.authenticationUserDetailsDelegateService = authenticationUserDetailsDelegateService;
    }
}
