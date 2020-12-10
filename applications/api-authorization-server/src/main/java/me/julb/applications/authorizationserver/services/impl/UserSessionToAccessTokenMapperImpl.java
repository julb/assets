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

package me.julb.applications.authorizationserver.services.impl;

import com.nimbusds.jwt.JWTClaimsSet;

import java.io.IOException;
import java.security.KeyPair;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.authorizationserver.configurations.properties.AccessTokenJwtForgeryProperties;
import me.julb.applications.authorizationserver.configurations.properties.AccessTokenJwtKeyProperties;
import me.julb.applications.authorizationserver.configurations.properties.ApplicationProperties;
import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.mobilephone.UserMobilePhoneEntity;
import me.julb.applications.authorizationserver.entities.profile.UserProfileEntity;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserMobilePhoneRepository;
import me.julb.applications.authorizationserver.repositories.UserProfileRepository;
import me.julb.applications.authorizationserver.services.UserSessionToAccessTokenMapper;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionAccessTokenDTO;
import me.julb.library.utility.constants.JWTClaims;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.http.HttpHeaderUtility;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.library.utility.josejwt.TokenEmitter;
import me.julb.library.utility.josejwt.jwk.impl.ManualAsymmetricJWKProvider;
import me.julb.library.utility.josejwt.keyloader.PEMKeyLoader;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The mapper from a session to an access token.
 * <P>
 * @author Julb.
 */
@Service
@Validated
public class UserSessionToAccessTokenMapperImpl implements UserSessionToAccessTokenMapper {

    /**
     * The token emitter.
     */
    private TokenEmitter tokenEmitter;

    /**
     * The user profile repository.
     */
    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * The user mail repository.
     */
    @Autowired
    private UserMailRepository userMailRepository;

    /**
     * The user mobile phone repository.
     */
    @Autowired
    private UserMobilePhoneRepository userMobilePhoneRepository;

    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Initializes the service.
     * @throws IOException if an error occurs.
     */
    @PostConstruct
    public void init()
        throws IOException {
        AccessTokenJwtKeyProperties accessTokenJwtSignature = applicationProperties.getAccessTokenJwtForgery().getSignature();

        KeyPair keyPair = PEMKeyLoader.loadPasswordProtectedPrivateKey(accessTokenJwtSignature.getKeyPath().getInputStream(), accessTokenJwtSignature.getKeyPassword());

        // @formatter:off
        ManualAsymmetricJWKProvider signatureKey = new ManualAsymmetricJWKProvider.Builder()
            .algorithm(accessTokenJwtSignature.getAlgorithm())
            .keyId(accessTokenJwtSignature.getKeyId())
            .keyPair(keyPair)
            .useForSignature()
            .build();

        this.tokenEmitter = new TokenEmitter()
            .setSignatureJWKProvider(signatureKey);
        
        // @formatter:on
    }

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSessionAccessTokenDTO map(@NotNull UserSessionEntity userSession) {
        // Trademark.
        String tm = TrademarkContextHolder.getTrademark();

        // JWT foregry properties.
        AccessTokenJwtForgeryProperties accessTokenJwtForgery = applicationProperties.getAccessTokenJwtForgery();

        UserEntity user = userSession.getUser();
        UserProfileEntity userProfile = userProfileRepository.findByTmAndUser_Id(tm, user.getId());
        UserMailEntity userMail = userMailRepository.findByTmAndUser_IdAndPrimaryIsTrue(tm, user.getId());
        UserMobilePhoneEntity userMobilePhone = userMobilePhoneRepository.findByTmAndUser_IdAndPrimaryIsTrue(tm, user.getId());

        String expirationDateTime = DateUtility.dateTimePlus(accessTokenJwtForgery.getValidityInSeconds().intValue(), ChronoUnit.SECONDS);

        // Issue date/expiration
        Date issueDate = DateUtility.parseDateTime(DateUtility.dateTimeNow());
        Date expirationDate = DateUtility.parseDateTime(expirationDateTime);

        // @formatter:off
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer(accessTokenJwtForgery.getIssuer())
            .audience(Arrays.asList(tm, accessTokenJwtForgery.getCoreAudience()))
            .jwtID(IdentifierUtility.generateId())
            .subject(user.getId())
            .issueTime(issueDate)
            .expirationTime(expirationDate)
            .claim(JWTClaims.SID, userSession.getId())
            .claim(JWTClaims.NAME, userProfile.getDisplayName())
            .claim(JWTClaims.PREFERRED_USERNAME, userProfile.getDisplayName())
            .claim(JWTClaims.GIVEN_NAME, userProfile.getFirstName())
            .claim(JWTClaims.FAMILY_NAME, userProfile.getLastName())
            .claim(JWTClaims.PICTURE_URL, "https://www.avatar.com") //FIXME
            .claim(JWTClaims.WEBSITE_URL, userProfile.getWebsiteUrl())
            .claim(JWTClaims.MAIL, userMail.getMail())
            .claim(JWTClaims.MAIL_VERIFIED, userMail.getVerified())
            .claim(JWTClaims.PHONE_NUMBER, userMobilePhone != null ? userMobilePhone.getMobilePhone().getE164Number() : Strings.EMPTY)
            .claim(JWTClaims.PHONE_NUMBER_VERIFIED, userMobilePhone != null ? userMobilePhone.getVerified() : Boolean.FALSE.toString())
            .claim(JWTClaims.ORGANIZATION, userProfile.getOrganization())
            .claim(JWTClaims.ORGANIZATION_UNIT, userProfile.getOrganizationUnit())
            .claim(JWTClaims.ROLES, user.getRoles())
            .claim(JWTClaims.MFA_VERIFIED, userSession.getMfaVerified())
            .build();
        // @formatter:on

        // Builds the JWT.
        String jwt = this.tokenEmitter.emit(jwtClaimsSet.toString());

        // Return token.
        UserSessionAccessTokenDTO accessToken = new UserSessionAccessTokenDTO();
        accessToken.setAccessToken(jwt);
        accessToken.setExpiresAt(expirationDateTime);
        accessToken.setExpiresIn(DateUtility.secondsUntil(expirationDateTime));
        accessToken.setType(HttpHeaderUtility.BEARER);
        return accessToken;
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
