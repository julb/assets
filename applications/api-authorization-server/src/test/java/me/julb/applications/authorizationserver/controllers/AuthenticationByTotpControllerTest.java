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

package me.julb.applications.authorizationserver.controllers;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByTotpEntity;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.applications.authorizationserver.entities.profile.UserProfileEntity;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.services.SignupService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPasswordService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByPincodeService;
import me.julb.applications.authorizationserver.services.UserAuthenticationByTotpService;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPasswordPatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodeDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByPincodePatchDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpCreationDTO;
import me.julb.applications.authorizationserver.services.dto.authentication.UserAuthenticationByTotpWithRawSecretDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.persistence.mongodb.reactive.test.base.AbstractMongoDbReactiveBaseTest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Unit test for the authentication by password class.
 * <br>
 * @author Julb.
 */
@Import({TestChannelBinderConfiguration.class})
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = AuthenticationByTotpControllerTest.Initializer.class)
@Testcontainers
public class AuthenticationByTotpControllerTest extends AbstractMongoDbReactiveBaseTest {

    /**
     * The MongoDB container.
     */
    @Container
    private static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo").withTag("4.4"));

    /**
     * The web test client.
     */
    @Autowired
    private WebTestClient webTestClient;

    /**
     * The signup service.
     */
    @Autowired
    private SignupService signupService;

    /**
     * The user mail service.
     */
    @Autowired
    private UserMailService userMailService;

    /**
     * The user session service.
     */
    @Autowired
    private UserSessionService userSessionService;

    /**
     * The user authentication by password service.
     */
    @Autowired
    private UserAuthenticationByPasswordService userAuthenticationByPasswordService;

    /**
     * The user authentication by pincode service.
     */
    @Autowired
    private UserAuthenticationByPincodeService userAuthenticationByPincodeService;

    /**
     * The user authentication by TOTP service.
     */
    @Autowired
    private UserAuthenticationByTotpService userAuthenticationByTotpService;

    /**
     * The user registered with a password.
     */
    private SignupWithPasswordCreationDTO userVerifiedSignupWithPassword;

    /**
     * The pincode.
     */
    private UserAuthenticationByPincodeCreationDTO userVerifiedPincode;

    /**
     * The user verified.
     */
    private UserDTO userVerified;

    /**
     * The device.
     */
    private UserAuthenticationByTotpWithRawSecretDTO userAuthenticationByTotp;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> setupData() {
        // Create user with verified email.
        userVerifiedSignupWithPassword = new SignupWithPasswordCreationDTO();
        userVerifiedSignupWithPassword.setMail("user-verified-with-password@julb.io");
        userVerifiedSignupWithPassword.setPassword("password");
        userVerifiedSignupWithPassword.setProfile(new UserProfileCreationDTO());
        userVerifiedSignupWithPassword.getProfile().setFirstName("UserVerified");
        userVerifiedSignupWithPassword.getProfile().setLastName("WithPassword");

        return this.signupService.signup(userVerifiedSignupWithPassword).flatMap(userVerified -> {
            this.userVerified = userVerified;
            return userMailService.findByMail(userVerified.getMail()).flatMap(userVerifiedMail -> {
                return this.userMailService.updateVerifyWithoutToken(userVerified.getId(), userVerifiedMail.getId()).flatMap(userMail -> {
                    // Add pincode
                    userVerifiedPincode = new UserAuthenticationByPincodeCreationDTO();
                    userVerifiedPincode.setPincode("00000000");
                    Mono<UserAuthenticationByPincodeDTO> userAuthenticationByPinCodeMono = userAuthenticationByPincodeService.create(userVerified.getId(), userVerifiedPincode);

                    // Add device TOTP
                    UserAuthenticationByTotpCreationDTO authenticationCreationDTO = new UserAuthenticationByTotpCreationDTO();
                    authenticationCreationDTO.setName("user-device");
                    Mono<UserAuthenticationByTotpWithRawSecretDTO> userAuthenticationByTotpMono = userAuthenticationByTotpService.create(userVerified.getId(), authenticationCreationDTO);

                    return Mono.zip(userAuthenticationByPinCodeMono, userAuthenticationByTotpMono).flatMap(tuple -> {
                        this.userAuthenticationByTotp = tuple.getT2();

                        // Enable MFA for password authentication and pincode authentication.
                        UserAuthenticationByPasswordPatchDTO authenticationByPasswordPatchDTO = new UserAuthenticationByPasswordPatchDTO();
                        authenticationByPasswordPatchDTO.setMfaEnabled(true);
                        Mono<UserAuthenticationByPasswordDTO> userAuthenticationByPasswordPatchMono = userAuthenticationByPasswordService.patch(userVerified.getId(), authenticationByPasswordPatchDTO);

                        UserAuthenticationByPincodePatchDTO authenticationByPincodePatchDTO = new UserAuthenticationByPincodePatchDTO();
                        authenticationByPincodePatchDTO.setMfaEnabled(true);
                        Mono<UserAuthenticationByPincodeDTO> userAuthenticationByPincodePatchMono = userAuthenticationByPincodeService.patch(userVerified.getId(), authenticationByPincodePatchDTO);

                        return Mono.zip(userAuthenticationByPasswordPatchMono, userAuthenticationByPincodePatchMono).then();
                    });
                });
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<Class<?>> getEntityClasses() {
        //@formatter:off
        return Flux.just(
            UserEntity.class, 
            UserAuthenticationByPasswordEntity.class, 
            UserAuthenticationByPincodeEntity.class, 
            UserAuthenticationByTotpEntity.class, 
            UserMailEntity.class, 
            UserProfileEntity.class, 
            UserPreferencesEntity.class, 
            UserSessionEntity.class
        );
        //@formatter:on
    }

    // /**
    //  * Unit test method.
    //  */
    // @Test
    // public void whenAuthenticateByPasswordWithValidUserRememberMeMfaEnabled_thenReturn200()
    //     throws Exception {

    //     //@formatter:off
    //     webTestClient
    //         .post()
    //             .uri(uriBuilder -> uriBuilder
    //                 .path("/login/password")
    //                 .queryParam("mail", userVerifiedSignupWithPassword.getMail())
    //                 .queryParam("password", userVerifiedSignupWithPassword.getPassword())
    //                 .queryParam("rememberMe", "true")
    //                 .build()
    //             )
    //             .accept(MediaType.APPLICATION_FORM_URLENCODED)
    //             .exchange()
    //             .expectStatus()
    //                 .isOk()
    //             .expectBody()
    //                 .jsonPath("$.type").isEqualTo(HttpHeaderUtility.BEARER)
    //                 .jsonPath("$.accessToken").isNotEmpty()
    //                 .jsonPath("$.idToken").isNotEmpty()
    //                 .jsonPath("$.expiresIn").isNotEmpty()
    //                 .consumeWith(body -> {
    //                     // Ensure session is created.
    //                     userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).collectList().flatMap(allUserSessions -> {
    //                         // Ensure session is created.
    //                         Assertions.assertEquals(1, allUserSessions.size());
    //                         UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);
                            
    //                         // Assert session MFA is not verified.
    //                         Assertions.assertFalse(userSession.getMfaVerified());

    //                         // Get access token.
    //                         JSONObject jwtAccessToken = new JSONObject(new String(body.getResponseBody()));
    //                         String accessToken = jwtAccessToken.getString("accessToken");
                            
    //                         // Generate TOTP code.
    //                         String validTotpCode = TotpUtility.generateValidTotp(userAuthenticationByTotp.getRawSecret());

    //                         // Send TOTP.
    //                         webTestClient
    //                             .post()
    //                                 .uri(uriBuilder -> uriBuilder
    //                                     .path("/login/totp")
    //                                     .queryParam("deviceId", userAuthenticationByTotp.getId())
    //                                     .queryParam("totp", validTotpCode)
    //                                     .build()
    //                                 )
    //                                 .accept(MediaType.APPLICATION_FORM_URLENCODED)
    //                                 .header(HttpHeaders.AUTHORIZATION, HttpHeaderUtility.toBearerToken(accessToken))
    //                                 .exchange()
    //                                 .expectStatus()
    //                                     .isOk()
    //                                 .expectBody()
    //                                     .jsonPath("$.type").isEqualTo(HttpHeaderUtility.BEARER)
    //                                     .jsonPath("$.accessToken").isNotEmpty()
    //                                     .jsonPath("$.expiresIn").isNumber()
    //                                     .consumeWith(totpBody -> {

    //                                     });

    //                             .andExpect(status().isOk())
    //                             .andExpect(jsonPath("$.type", is(HttpHeaderUtility.BEARER)))
    //                             .andExpect(jsonPath("$.accessToken", notNullValue(String.class)))
    //                             .andExpect(jsonPath("$.expiresIn", notNullValue(Integer.class)))
    //                             .andDo((resultTotp) -> {
    //                                 List<UserSessionDTO> totpUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
    //                                 Assertions.assertEquals(1, totpUserSessions.size());
    //                                 UserSessionDTO totpUserSession = Iterables.getOnlyElement(totpUserSessions);
                                    
    //                                 // Assert session MFA is not verified.
    //                                 Assertions.assertTrue(totpUserSession.getMfaVerified());
    //                             });

    //                     }).block();
    //                 });
    //     //@formatter:on
    // }

    // /**
    //  * Unit test method.
    //  */
    // @Test
    // public void whenAuthenticateWithMfaEnabledAndFetchTotps_thenReturn200()
    //     throws Exception {

    //     //@formatter:off
    //     mockMvc
    //         .perform(
    //             post("/login/password")
    //                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    //                 .param("mail", userVerifiedSignupWithPassword.getMail())
    //                 .param("password", userVerifiedSignupWithPassword.getPassword()) 
    //                 .param("rememberMe", "true")
    //         )
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$.type", is(HttpHeaderUtility.BEARER)))
    //         .andExpect(jsonPath("$.accessToken", notNullValue(String.class)))
    //         .andExpect(jsonPath("$.idToken", notNullValue(String.class)))
    //         .andExpect(jsonPath("$.expiresIn", notNullValue(Integer.class)))
    //         .andDo((result) -> {
    //             // Ensure session is created.
    //             List<UserSessionDTO> allUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
    //             Assertions.assertEquals(1, allUserSessions.size());
    //             UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);
                
    //             // Assert session MFA is not verified.
    //             Assertions.assertFalse(userSession.getMfaVerified());
                
    //             // Get access token.
    //             JSONObject jwtAccessToken = new JSONObject(result.getResponse().getContentAsString());
    //             String accessToken = jwtAccessToken.getString("accessToken");
                
    //             // Send TOTP.
    //             mockMvc
    //                 .perform(
    //                     get("/my/authentications/type/totp")
    //                         .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    //                         .header(HttpHeaders.AUTHORIZATION, HttpHeaderUtility.toBearerToken(accessToken))
    //                 )
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.totalElements", is(1)));
    //         });
    //     //@formatter:on
    // }

    // /**
    //  * Unit test method.
    //  */
    // @Test
    // public void whenAuthenticateByPincodeWithValidUserRememberMeMfaEnabled_thenReturn200()
    //     throws Exception {

    //     //@formatter:off
    //     mockMvc
    //         .perform(
    //             post("/login/pincode")
    //                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    //                 .param("mail", userVerified.getMail())
    //                 .param("pincode", userVerifiedPincode.getPincode())
    //                 .param("rememberMe", "true")
    //         )
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$.type", is(HttpHeaderUtility.BEARER)))
    //         .andExpect(jsonPath("$.accessToken", notNullValue(String.class)))
    //         .andExpect(jsonPath("$.idToken", notNullValue(String.class)))
    //         .andExpect(jsonPath("$.expiresIn", notNullValue(Integer.class)))
    //         .andDo((result) -> {
    //             // Ensure session is created.
    //             List<UserSessionDTO> allUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
    //             Assertions.assertEquals(1, allUserSessions.size());
    //             UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);
                
    //             // Assert session MFA is not verified.
    //             Assertions.assertFalse(userSession.getMfaVerified());
                
    //             // Get access token.
    //             JSONObject jwtAccessToken = new JSONObject(result.getResponse().getContentAsString());
    //             String accessToken = jwtAccessToken.getString("accessToken");
                
    //             // Generate TOTP code.
    //             String validTotpCode = TotpUtility.generateValidTotp(userAuthenticationByTotp.getRawSecret());
                
    //             // Send TOTP.
    //             mockMvc
    //                 .perform(
    //                     post("/login/totp")
    //                         .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    //                         .header(HttpHeaders.AUTHORIZATION, HttpHeaderUtility.toBearerToken(accessToken))
    //                         .param("deviceId", userAuthenticationByTotp.getId())
    //                         .param("totp", validTotpCode)
    //                         .locale(Locale.getDefault())
    //                 )
    //                 .andExpect(status().isOk())
    //                 .andExpect(jsonPath("$.type", is(HttpHeaderUtility.BEARER)))
    //                 .andExpect(jsonPath("$.accessToken", notNullValue(String.class)))
    //                 .andExpect(jsonPath("$.expiresIn", notNullValue(Integer.class)))
    //                 .andDo((resultTotp) -> {
    //                     List<UserSessionDTO> totpUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
    //                     Assertions.assertEquals(1, totpUserSessions.size());
    //                     UserSessionDTO totpUserSession = Iterables.getOnlyElement(totpUserSessions);
                        
    //                     // Assert session MFA is not verified.
    //                     Assertions.assertTrue(totpUserSession.getMfaVerified());
    //                 });
                
    //         });
    //     //@formatter:on
    // }

    /**
     * Unit test method.
     */
    @Test
    public void whenVerifyingTotpUnauthenticated_thenReturn401()
        throws Exception {
        //@formatter:off
        webTestClient
            .post()
            .uri(uriBuilder -> uriBuilder
                .path("/login/totp")
                .queryParam("deviceId", IdentifierUtility.generateId())
                .queryParam("totp", "000000")
                .build()
            )
            .accept(MediaType.APPLICATION_FORM_URLENCODED)
            .header(HttpHeaders.ACCEPT_LANGUAGE, Locale.getDefault().toLanguageTag())
            .exchange()
            .expectStatus()
                .isUnauthorized();
        //@formatter:on
    }

    /**
     * Initializer class for the test.
     * <br>
     * @author Julb.
     */
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            //@formatter:off
            TestPropertyValues
                .of(
                    String.format("spring.data.mongodb.uri=%s", MONGODB_CONTAINER.getReplicaSetUrl())
                )
                .applyTo(configurableApplicationContext);
            //@formatter:on
        }
    }
}
