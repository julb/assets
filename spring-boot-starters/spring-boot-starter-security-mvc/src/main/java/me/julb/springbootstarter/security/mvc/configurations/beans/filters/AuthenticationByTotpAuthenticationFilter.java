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

package me.julb.springbootstarter.security.mvc.configurations.beans.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.springbootstarter.security.configurations.beans.authenticationtokens.CustomUsernameTotpAuthenticationToken;
import me.julb.springbootstarter.security.mvc.services.ISecurityService;
import me.julb.springbootstarter.security.services.PasswordEncoderService;

/**
 * The TOTP authentication filter.
 * <br>
 * @author Julb.
 */
@Slf4j
public class AuthenticationByTotpAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements Ordered {

    /**
     * The security service.
     */
    @Autowired
    private ISecurityService securityService;

    /**
     * The URL attribute.
     */
    private static final String URL = "/login/totp";

    /**
     * The DEVICE_ID_PARAMETER attribute.
     */
    private static final String DEVICE_ID_PARAMETER = "deviceId";

    /**
     * The TOTP_PARAMETER attribute.
     */
    private static final String TOTP_PARAMETER = "totp";

    /**
     * The password encoder service.
     */
    @Autowired
    private PasswordEncoderService passwordEncoderService;

    /**
     * Default constructor.
     */
    public AuthenticationByTotpAuthenticationFilter() {
        super();
        this.setPasswordParameter(TOTP_PARAMETER);
        this.setFilterProcessesUrl(URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        // Check if authenticated.
        if (!securityService.isAuthenticated()) {
            throw new BadCredentialsException("User not authenticated.");
        }

        // Check if user has MFA required authority
        AuthenticatedUserDTO connectedUser = securityService.getConnectedUserIdentity();

        // Extract session from token
        LOGGER.info("Trying to authenticate with user/totp");
        String userId = connectedUser.getUserId();
        String sessionId = connectedUser.getSessionId();
        String deviceId = obtainDeviceId(request);
        String totp = passwordEncoderService.hash(obtainPassword(request));

        CustomUsernameTotpAuthenticationToken authRequest = new CustomUsernameTotpAuthenticationToken(userId, sessionId, deviceId, totp);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Obtains the device ID.
     * @param request so that request attributes can be retrieved
     * @return the device ID.
     */
    protected String obtainDeviceId(HttpServletRequest request) {
        return request.getParameter(DEVICE_ID_PARAMETER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return 20;
    }
}
