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

package io.julb.applications.authorizationserver.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.julb.applications.authorizationserver.entities.UserEntity;
import io.julb.applications.authorizationserver.entities.authentication.UserAuthenticationByPasswordEntity;
import io.julb.applications.authorizationserver.entities.mail.UserMailEntity;
import io.julb.applications.authorizationserver.entities.preferences.UserPreferencesEntity;
import io.julb.applications.authorizationserver.entities.profile.UserProfileEntity;
import io.julb.applications.authorizationserver.entities.session.UserSessionEntity;
import io.julb.applications.authorizationserver.services.SignupService;
import io.julb.applications.authorizationserver.services.UserMailService;
import io.julb.applications.authorizationserver.services.UserSessionService;
import io.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import io.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import io.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import io.julb.applications.authorizationserver.services.dto.signup.SignupWithPasswordCreationDTO;
import io.julb.applications.authorizationserver.services.dto.user.UserDTO;
import io.julb.library.utility.constants.Integers;
import io.julb.library.utility.data.search.Searchable;
import io.julb.library.utility.http.HttpHeaderUtility;
import io.julb.springbootstarter.persistence.mongodb.test.base.AbstractMongoDbBaseTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.Iterables;

/**
 * Unit test for the authentication by password class.
 * <P>
 * @author Julb.
 */
@AutoConfigureMockMvc
@ContextConfiguration(initializers = AuthenticationByPasswordControllerTest.Initializer.class)
@Testcontainers
public class AuthenticationByPasswordControllerTest extends AbstractMongoDbBaseTest {

    /**
     * The MongoDB container.
     */
    @Container
    private static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer();

    /**
     * The mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

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
     * The user registered with a password.
     */
    private SignupWithPasswordCreationDTO userVerifiedSignupWithPassword;

    /**
     * The user verified.
     */
    private UserDTO userVerified;

    /**
     * The user registered with a password.
     */
    private SignupWithPasswordCreationDTO userNotVerifiedSignupWithPassword;

    /**
     * The user verified.
     */
    private UserDTO userNotVerified;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupData() {
        // Create user with verified email.
        userVerifiedSignupWithPassword = new SignupWithPasswordCreationDTO();
        userVerifiedSignupWithPassword.setMail("user-verified-with-password@julb.io");
        userVerifiedSignupWithPassword.setPassword("password");
        userVerifiedSignupWithPassword.setProfile(new UserProfileCreationDTO());
        userVerifiedSignupWithPassword.getProfile().setFirstName("UserVerified");
        userVerifiedSignupWithPassword.getProfile().setLastName("WithPassword");
        userVerified = this.signupService.signup(userVerifiedSignupWithPassword);

        UserMailDTO userVerifiedMail = userMailService.findByMail(userVerified.getMail());
        this.userMailService.updateVerifyWithoutToken(userVerified.getId(), userVerifiedMail.getId());

        // Create user with non-verified email.
        userNotVerifiedSignupWithPassword = new SignupWithPasswordCreationDTO();
        userNotVerifiedSignupWithPassword.setMail("user-not-verified-with-password@julb.io");
        userNotVerifiedSignupWithPassword.setPassword("password");
        userNotVerifiedSignupWithPassword.setProfile(new UserProfileCreationDTO());
        userNotVerifiedSignupWithPassword.getProfile().setFirstName("UserNotVerified");
        userNotVerifiedSignupWithPassword.getProfile().setLastName("WithPassword");
        userNotVerified = this.signupService.signup(userNotVerifiedSignupWithPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getEntityClasses() {
        return new Class<?>[] {UserEntity.class, UserAuthenticationByPasswordEntity.class, UserMailEntity.class, UserProfileEntity.class, UserPreferencesEntity.class, UserSessionEntity.class};
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPasswordWithValidUserRememberMe_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                post("/login/password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userVerifiedSignupWithPassword.getMail())
                    .param("password", userVerifiedSignupWithPassword.getPassword())
                    .param("rememberMe", "true")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type", is(HttpHeaderUtility.BEARER)))
            .andExpect(jsonPath("$.accessToken", notNullValue(String.class)))
            .andExpect(jsonPath("$.idToken", notNullValue(String.class)))
            .andExpect(jsonPath("$.expiresIn", notNullValue(Integer.class)))
            .andDo((result) -> {
                // Ensure session is created.
                List<UserSessionDTO> allUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
                Assertions.assertEquals(1, allUserSessions.size());
                
                UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);
                
                Assertions.assertTrue(userSession.getMfaVerified());
                Assertions.assertEquals(TimeUnit.DAYS.toSeconds(Integers.THIRTY), userSession.getDurationInSeconds());
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPasswordWithValidUserNotRememberMe_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                post("/login/password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userVerifiedSignupWithPassword.getMail())
                    .param("password", userVerifiedSignupWithPassword.getPassword())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type", is(HttpHeaderUtility.BEARER)))
            .andExpect(jsonPath("$.accessToken", notNullValue(String.class)))
            .andExpect(jsonPath("$.idToken", notNullValue(String.class)))
            .andExpect(jsonPath("$.expiresIn", notNullValue(Integer.class)))
            .andDo((result) -> {
                // Ensure session is created.
                List<UserSessionDTO> allUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
                Assertions.assertEquals(1, allUserSessions.size());
                
                UserSessionDTO userSession = Iterables.getOnlyElement(allUserSessions);

                Assertions.assertTrue(userSession.getMfaVerified());
                Assertions.assertEquals(TimeUnit.HOURS.toSeconds(Integers.TWO), userSession.getDurationInSeconds());
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPasswordWithValidUserWrongPassword_thenReturn401()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                post("/login/password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userVerifiedSignupWithPassword.getMail())
                    .param("password", "bad-password")
            )
            .andExpect(status().isUnauthorized())
            .andDo((result) -> {
                // Ensure session is created.
                List<UserSessionDTO> allUserSessions = userSessionService.findAll(userVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
                Assertions.assertEquals(0, allUserSessions.size());
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPasswordWithUserEmailNotConfirmed_thenReturn401()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                post("/login/password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userNotVerifiedSignupWithPassword.getMail())
                    .param("password", userNotVerifiedSignupWithPassword.getPassword())
            )
            .andExpect(status().isUnauthorized())
            .andDo((result) -> {
                // Ensure session is created.
                List<UserSessionDTO> allUserSessions = userSessionService.findAll(userNotVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
                Assertions.assertEquals(0, allUserSessions.size());
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPasswordWithUnknownUser_thenReturn401()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                post("/login/password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", "unknown@julb.io")
                    .param("password", "password")
            )
            .andExpect(status().isUnauthorized())
            .andDo((result) -> {
                // Ensure session is created.
                List<UserSessionDTO> allUserSessions = userSessionService.findAll(userNotVerified.getId(), Searchable.empty(), Pageable.unpaged()).getContent();
                Assertions.assertEquals(0, allUserSessions.size());
            });;
        //@formatter:on
    }

    /**
     * Initializer class for the test.
     * <P>
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
