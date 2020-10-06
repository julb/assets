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

package me.julb.springbootstarter.security.configurations.beans.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import me.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomUsernamePasswordAuthenticationToken;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The paassword authentication filter.
 * <P>
 * @author Julb.
 */
public class AuthenticationByPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements Ordered {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationByPasswordAuthenticationFilter.class);

    /**
     * The URL attribute.
     */
    private static final String URL = "/login/password";

    /**
     * The MAIL_PARAMETER attribute.
     */
    private static final String MAIL_PARAMETER = "mail";

    /**
     * The PASSWORD_PARAMETER attribute.
     */
    private static final String PASSWORD_PARAMETER = "password";

    /**
     * The password encoder service.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * Default constructor.
     */
    public AuthenticationByPasswordAuthenticationFilter() {
        super();
        this.setUsernameParameter(MAIL_PARAMETER);
        this.setPasswordParameter(PASSWORD_PARAMETER);
        this.setFilterProcessesUrl(URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        LOGGER.info("Trying to authenticate a user with mail and password.");
        String obtainUsername = obtainUsername(request);
        String obtainPassword = passwordEncoderService.hash(obtainPassword(request));
        CustomUsernamePasswordAuthenticationToken authRequest = new CustomUsernamePasswordAuthenticationToken(obtainUsername, obtainPassword);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 20;
    }
}
