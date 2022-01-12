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

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.Iterables;
import org.testcontainers.utility.DockerImageName;

import me.julb.applications.authorizationserver.entities.UserEntity;
import me.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPincodeEntity;
import me.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import me.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import me.julb.applications.authorizationserver.entities.profile.UserProfileEntity;
import me.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import me.julb.applications.authorizationserver.services.SignupService;
import me.julb.applications.authorizationserver.services.UserMailService;
import me.julb.applications.authorizationserver.services.UserSessionService;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.http.HttpHeaderUtility;
import me.julb.springbootstarter.persistence.mongodb.reactive.test.base.AbstractMongoDbReactiveBaseTest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Unit test for the authentication by pincode class.
 * <br>
 * @author Julb.
 */
@Import(TestChannelBinderConfiguration.class)
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = AuthenticationByPincodeControllerTest.Initializer.class)
@Testcontainers
public class AuthenticationByPincodeControllerTest extends AbstractMongoDbReactiveBaseTest {

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
     * The user registered with a pincode.
     */
    private SignupWithPincodeCreationDTO userVerifiedSignupWithPincode;

    /**
     * The user verified.
     */
    private UserDTO userVerified;

    /**
     * The user registered with a pincode.
     */
    private SignupWithPincodeCreationDTO userNotVerifiedSignupWithPincode;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> setupData() {
        // Create user with verified email.
        userVerifiedSignupWithPincode = new SignupWithPincodeCreationDTO();
        userVerifiedSignupWithPincode.setMail("user-verified-with-pincode@julb.io");
        userVerifiedSignupWithPincode.setPincode("00000000");
        userVerifiedSignupWithPincode.setProfile(new UserProfileCreationDTO());
        userVerifiedSignupWithPincode.getProfile().setFirstName("UserVerified");
        userVerifiedSignupWithPincode.getProfile().setLastName("WithPincode");

        // Create user with non-verified email.
        userNotVerifiedSignupWithPincode = new SignupWithPincodeCreationDTO();
        userNotVerifiedSignupWithPincode.setMail("user-not-verified-with-pincode@julb.io");
        userNotVerifiedSignupWithPincode.setPincode("00000000");
        userNotVerifiedSignupWithPincode.setProfile(new UserProfileCreationDTO());
        userNotVerifiedSignupWithPincode.getProfile().setFirstName("UserNotVerified");
        userNotVerifiedSignupWithPincode.getProfile().setLastName("WithPincode");

        Mono<Void> userVerifiedMono = this.signupService.signup(userVerifiedSignupWithPincode).flatMap(userVerified -> {
            this.userVerified = userVerified;
            return userMailService.findByMail(userVerified.getMail()).flatMap(userVerifiedMail -> {
                return this.userMailService.updateVerifyWithoutToken(userVerified.getId(), userVerifiedMail.getId()).then();
            });
        });

        
        Mono<Void> userNotVerifiedMono = this.signupService.signup(userNotVerifiedSignupWithPincode).then();

        return Mono.zip(userVerifiedMono, userNotVerifiedMono).then();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<Class<?>> getEntityClasses() {
        return Flux.just(UserEntity.class, UserAuthenticationByPincodeEntity.class, UserMailEntity.class, UserProfileEntity.class, UserPreferencesEntity.class, UserSessionEntity.class);
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPincodeWithValidUserRememberMe_thenReturn200()
        throws Exception {

        //@formatter:off
        webTestClient
            .post()
                .uri(uriBuilder -> uriBuilder
                    .path("/login/pincode")
                    .queryParam("mail", userVerifiedSignupWithPincode.getMail())
                    .queryParam("pincode", userVerifiedSignupWithPincode.getPincode())
                    .queryParam("rememberMe", "true")
                    .build()
                )
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBody()
                    .jsonPath("$.type").isEqualTo(HttpHeaderUtility.BEARER)
                    .jsonPath("$.accessToken").isNotEmpty()
                    .jsonPath("$.idToken").isNotEmpty()
                    .jsonPath("$.expiresIn").isNotEmpty()
                    .consumeWith(consumer -> {
                        // Ensure session is created.
                        userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).collectList().flatMap(allUserSessions -> {
                            Assertions.assertEquals(1, allUserSessions.size());
                        
                            UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);
                            
                            Assertions.assertTrue(userSession.getMfaVerified());
                            Assertions.assertEquals(TimeUnit.DAYS.toSeconds(Integers.THIRTY), userSession.getDurationInSeconds());

                            return Mono.empty();
                        }).block();
                    });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPincodeWithValidUserNotRememberMe_thenReturn200()
        throws Exception {

        //@formatter:off
        webTestClient
            .post()
                .uri(uriBuilder -> uriBuilder
                    .path("/login/pincode")
                    .queryParam("mail", userVerifiedSignupWithPincode.getMail())
                    .queryParam("pincode", userVerifiedSignupWithPincode.getPincode())
                    .build()
                )
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBody()
                    .jsonPath("$.type").isEqualTo(HttpHeaderUtility.BEARER)
                    .jsonPath("$.accessToken").isNotEmpty()
                    .jsonPath("$.idToken").isNotEmpty()
                    .jsonPath("$.expiresIn").isNotEmpty()
                    .consumeWith(consumer -> {
                        // Ensure session is created.
                        userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).collectList().flatMap(allUserSessions -> {
                            Assertions.assertEquals(1, allUserSessions.size());
                        
                            UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);
                            
                            Assertions.assertTrue(userSession.getMfaVerified());
                            Assertions.assertEquals(TimeUnit.HOURS.toSeconds(Integers.TWO), userSession.getDurationInSeconds());

                            return Mono.empty();
                        }).block();
                    });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPincodeWithValidUserWrongPincode_thenReturn401()
        throws Exception {

        //@formatter:off
        webTestClient
            .post()
                .uri(uriBuilder -> uriBuilder
                    .path("/login/pincode")
                    .queryParam("mail", userVerifiedSignupWithPincode.getMail())
                    .queryParam("pincode", "99999999")
                    .build()
                )
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus()
                    .isUnauthorized()
                .expectBody()
                    .consumeWith(consumer -> {
                        // Ensure session is created.
                        userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).collectList().flatMap(allUserSessions -> {
                            Assertions.assertEquals(0, allUserSessions.size());

                            return Mono.empty();
                        }).block();
                    });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPincodeWithUserEmailNotConfirmed_thenReturn401()
        throws Exception {
        //@formatter:off
        webTestClient
            .post()
                .uri(uriBuilder -> uriBuilder
                    .path("/login/pincode")
                    .queryParam("mail", userNotVerifiedSignupWithPincode.getMail())
                    .queryParam("pincode", userNotVerifiedSignupWithPincode.getPincode())
                    .build()
                )
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus()
                    .isUnauthorized()
                .expectBody()
                    .consumeWith(consumer -> {
                        // Ensure session is created.
                        userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).collectList().flatMap(allUserSessions -> {
                            Assertions.assertEquals(0, allUserSessions.size());

                            return Mono.empty();
                        }).block();
                    });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPincodeWithUnknownUser_thenReturn401()
        throws Exception {
        //@formatter:off
        webTestClient
            .post()
                .uri(uriBuilder -> uriBuilder
                    .path("/login/pincode")
                    .queryParam("mail", "unknown@julb.io")
                    .queryParam("pincode", "99999999")
                    .build()
                )
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus()
                    .isUnauthorized()
                .expectBody()
                    .consumeWith(consumer -> {
                        // Ensure session is created.
                        userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).collectList().flatMap(allUserSessions -> {
                            Assertions.assertEquals(0, allUserSessions.size());

                            return Mono.empty();
                        }).block();
                    });
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
