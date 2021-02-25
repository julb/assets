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
package me.julb.springbootstarter.security.configurations.beans.userdetails;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import me.julb.library.dto.security.AuthenticatedUserDTO;
import me.julb.library.dto.security.UserRole;
import me.julb.library.utility.constants.JWTClaims;
import me.julb.library.utility.josejwt.TokenReceiver;
import me.julb.library.utility.josejwt.exceptions.JOSEJWTException;
import me.julb.library.utility.josejwt.exceptions.JOSEJWTExceptionConverterUtility;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.security.configurations.beans.userdetails.delegates.IAuthenticationUserDetailsPermissionDelegateService;
import me.julb.springbootstarter.security.configurations.properties.SecurityJwtProperties;
import me.julb.springbootstarter.security.services.dto.CustomUserDetails;

/**
 * A service used to pre-authenticate users according to the given header.
 * <P>
 * @author Julb.
 */
@Slf4j
public class AuthenticationByJwtUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    /**
     * The security JWT properties.
     */
    @Autowired
    private SecurityJwtProperties securityJwtProperties;

    /**
     * The authentication by JWT delegate service.
     */
    private IAuthenticationUserDetailsPermissionDelegateService authenticationByJwtUserDetailsDelegateService;

    /**
     * The token receiver operation.
     */
    private TokenReceiver tokenReceiver;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken arg0)
        throws UsernameNotFoundException {
        try {
            LOGGER.debug("Entering authentication with bearer token.");
            String tm = TrademarkContextHolder.getTrademark();

            String token = (String) arg0.getPrincipal();

            // Check token.
            if (StringUtils.isNotBlank(token)) {
                // Verify token.
                String payload = this.tokenReceiver.receive(token, securityJwtProperties.getIssuer(), tm);
                JSONObject payloadAsJson = new JSONObject(payload);

                // Return JWT.
                AuthenticatedUserDTO dto = new AuthenticatedUserDTO();
                dto.setDisplayName(payloadAsJson.getString(JWTClaims.PREFERRED_USERNAME));
                dto.setFirstName(payloadAsJson.getString(JWTClaims.GIVEN_NAME));
                dto.setLastName(payloadAsJson.getString(JWTClaims.FAMILY_NAME));
                dto.setLocale(Locale.forLanguageTag(payloadAsJson.getString(JWTClaims.LOCALE)));
                dto.setMail(payloadAsJson.getString(JWTClaims.MAIL));
                dto.setMailVerified(payloadAsJson.getBoolean(JWTClaims.MAIL_VERIFIED));
                dto.setMobilePhone(payloadAsJson.getString(JWTClaims.PHONE_NUMBER));
                dto.setMobilePhoneVerified(payloadAsJson.getBoolean(JWTClaims.PHONE_NUMBER_VERIFIED));
                dto.setMfaVerified(payloadAsJson.getBoolean(JWTClaims.MFA_VERIFIED));
                dto.setName(payloadAsJson.getString(JWTClaims.NAME));
                dto.setOrganization(payloadAsJson.optString(JWTClaims.ORGANIZATION));
                dto.setOrganizationUnit(payloadAsJson.optString(JWTClaims.ORGANIZATION_UNIT));
                dto.setPictureUrl(payloadAsJson.optString(JWTClaims.PICTURE_URL));
                dto.setSessionId(payloadAsJson.getString(JWTClaims.SID));
                dto.setUserId(payloadAsJson.getString(JWTClaims.SUB));
                dto.setWebsiteUrl(payloadAsJson.optString(JWTClaims.WEBSITE_URL));

                // Depends on MFA verified
                for (Object role : payloadAsJson.getJSONArray(JWTClaims.ROLES).toList()) {
                    dto.getRoles().add(UserRole.valueOf((String) role));
                }

                if (authenticationByJwtUserDetailsDelegateService != null) {
                    dto.getPermissions().addAll(authenticationByJwtUserDetailsDelegateService.loadPermissions(dto));
                }

                // Return the user details.
                return new CustomUserDetails(dto);
            } else {
                return null;
            }
        } catch (JOSEJWTException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UsernameNotFoundException("Unable to validate JWT.", JOSEJWTExceptionConverterUtility.convertJOSEJWTException(e));
        }
    }

    /**
     * Setter for property authenticationByJwtUserDetailsDelegateService.
     * @param authenticationByJwtUserDetailsDelegateService New value of property authenticationByJwtUserDetailsDelegateService.
     */
    public void setAuthenticationByJwtUserDetailsDelegateService(IAuthenticationUserDetailsPermissionDelegateService authenticationByJwtUserDetailsDelegateService) {
        this.authenticationByJwtUserDetailsDelegateService = authenticationByJwtUserDetailsDelegateService;
    }

    /**
     * Setter for property tokenReceiver.
     * @param tokenReceiver New value of property tokenReceiver.
     */
    public void setTokenReceiver(TokenReceiver tokenReceiver) {
        this.tokenReceiver = tokenReceiver;
    }
}
