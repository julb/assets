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
import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.applications.authorizationserver.entities.profile.UserProfileEntity;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.repositories.UserMailRepository;
import me.julb.applications.authorizationserver.repositories.UserMobilePhoneRepository;
import me.julb.applications.authorizationserver.repositories.UserPreferencesRepository;
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
import me.julb.springbootstarter.core.context.ContextConstants;

import reactor.core.publisher.Mono;

/**
 * The mapper from a session to an access token.
 * <br>
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
     * The user preferences repository.
     */
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

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
        if (applicationProperties.getAccessTokenJwtForgery() != null) {
            AccessTokenJwtKeyProperties accessTokenJwtSignature = applicationProperties.getAccessTokenJwtForgery().getSignature();

            KeyPair keyPair = PEMKeyLoader.loadPasswordProtectedPrivateKey(accessTokenJwtSignature.getKeyPath().getInputStream(), accessTokenJwtSignature.getKeyPassword());

            // @formatter:off
            ManualAsymmetricJWKProvider signatureKey = new ManualAsymmetricJWKProvider.Builder()
                .algorithm(accessTokenJwtSignature.getAlgorithm())
                .keyId(accessTokenJwtSignature.getKeyId())
                .keyPair(keyPair)
                .useForSignature()
                .build();
            // @formatter:on

            this.tokenEmitter = new TokenEmitter().setSignatureJWKProvider(signatureKey);
        }
    }

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<UserSessionAccessTokenDTO> map(@NotNull UserSessionEntity userSession) {
        if (this.tokenEmitter == null) {
            return Mono.error(new UnsupportedOperationException());
        }

        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);

            // JWT foregry properties.
            AccessTokenJwtForgeryProperties accessTokenJwtForgery = applicationProperties.getAccessTokenJwtForgery();

            UserEntity user = userSession.getUser();

            return Mono.zip(
                userProfileRepository.findByTmAndUser_Id(tm, user.getId()), 
                userMailRepository.findByTmAndUser_IdAndPrimaryIsTrue(tm, user.getId()), 
                userMobilePhoneRepository.findByTmAndUser_IdAndPrimaryIsTrue(tm, user.getId()), 
                userPreferencesRepository.findByTmAndUser_Id(tm, user.getId())
            ).map(quadruple -> {
                UserProfileEntity userProfile = quadruple.getT1();
                UserMailEntity userMail = quadruple.getT2();
                UserMobilePhoneEntity userMobilePhone = quadruple.getT3();
                UserPreferencesEntity userPreferences = quadruple.getT4();

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
                    .claim(JWTClaims.LOCALE, userPreferences.getLanguage().toLanguageTag())
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
            });
        });
    }

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Private methods.

}
