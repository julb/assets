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

package io.julb.springbootstarter.security.configurations.beans.filters;

import io.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomUsernamePincodeAuthenticationToken;
import io.julb.springbootstarter.security.services.PasswordEncoderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The pincode authentication filter.
 * <P>
 * @author Julb.
 */
public class AuthenticationByPincodeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationByPincodeAuthenticationFilter.class);

    /**
     * The URL attribute.
     */
    private static final String URL = "/login/pincode";

    /**
     * The MAIL_PARAMETER attribute.
     */
    private static final String MAIL_PARAMETER = "mail";

    /**
     * The PINCODE_PARAMETER attribute.
     */
    private static final String PINCODE_PARAMETER = "pincode";

    /**
     * The password encoder service.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * Default constructor.
     */
    public AuthenticationByPincodeAuthenticationFilter() {
        super();
        this.setUsernameParameter(MAIL_PARAMETER);
        this.setPasswordParameter(PINCODE_PARAMETER);
        this.setFilterProcessesUrl(URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        LOGGER.info("Trying to authenticate with user/pincode");
        String obtainUsername = obtainUsername(request);
        String obtainPassword = passwordEncoderService.hash(obtainPassword(request));
        CustomUsernamePincodeAuthenticationToken authRequest = new CustomUsernamePincodeAuthenticationToken(obtainUsername, obtainPassword);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
