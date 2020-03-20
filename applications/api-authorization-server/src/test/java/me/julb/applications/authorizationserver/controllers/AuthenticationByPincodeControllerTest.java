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

package me.julb.applications.authorizationserver.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
import me.julb.applications.authorizationserver.services.dto.mail.UserMailDTO;
import me.julb.applications.authorizationserver.services.dto.profile.UserProfileCreationDTO;
import me.julb.applications.authorizationserver.services.dto.session.UserSessionDTO;
import me.julb.applications.authorizationserver.services.dto.signup.SignupWithPincodeCreationDTO;
import me.julb.applications.authorizationserver.services.dto.user.UserDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.http.HttpHeaderUtility;
import me.julb.springbootstarter.persistence.mongodb.test.base.AbstractMongoDbBaseTest;

/**
 * Unit test for the authentication by pincode class.
 * <P>
 * @author Julb.
 */
@Import(TestChannelBinderConfiguration.class)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = AuthenticationByPincodeControllerTest.Initializer.class)
@Testcontainers
public class AuthenticationByPincodeControllerTest extends AbstractMongoDbBaseTest {

    /**
     * The MongoDB container.
     */
    @Container
    private static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo").withTag("4.0.10"));

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
     * The user verified.
     */
    private UserDTO userNotVerified;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupData() {
        // Create user with verified email.
        userVerifiedSignupWithPincode = new SignupWithPincodeCreationDTO();
        userVerifiedSignupWithPincode.setMail("user-verified-with-pincode@julb.io");
        userVerifiedSignupWithPincode.setPincode("00000000");
        userVerifiedSignupWithPincode.setProfile(new UserProfileCreationDTO());
        userVerifiedSignupWithPincode.getProfile().setFirstName("UserVerified");
        userVerifiedSignupWithPincode.getProfile().setLastName("WithPincode");
        userVerified = this.signupService.signup(userVerifiedSignupWithPincode);

        UserMailDTO userVerifiedMail = userMailService.findByMail(userVerified.getMail());
        this.userMailService.updateVerifyWithoutToken(userVerified.getId(), userVerifiedMail.getId());

        // Create user with non-verified email.
        userNotVerifiedSignupWithPincode = new SignupWithPincodeCreationDTO();
        userNotVerifiedSignupWithPincode.setMail("user-not-verified-with-pincode@julb.io");
        userNotVerifiedSignupWithPincode.setPincode("00000000");
        userNotVerifiedSignupWithPincode.setProfile(new UserProfileCreationDTO());
        userNotVerifiedSignupWithPincode.getProfile().setFirstName("UserNotVerified");
        userNotVerifiedSignupWithPincode.getProfile().setLastName("WithPincode");
        userNotVerified = this.signupService.signup(userNotVerifiedSignupWithPincode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getEntityClasses() {
        return new Class<?>[] {UserEntity.class, UserAuthenticationByPincodeEntity.class, UserMailEntity.class, UserProfileEntity.class, UserPreferencesEntity.class, UserSessionEntity.class};
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenAuthenticateByPincodeWithValidUserRememberMe_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                post("/login/pincode")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userVerifiedSignupWithPincode.getMail())
                    .param("pincode", userVerifiedSignupWithPincode.getPincode())
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
    public void whenAuthenticateByPincodeWithValidUserNotRememberMe_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                post("/login/pincode")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userVerifiedSignupWithPincode.getMail())
                    .param("pincode", userVerifiedSignupWithPincode.getPincode())
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
    public void whenAuthenticateByPincodeWithValidUserWrongPincode_thenReturn401()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                post("/login/pincode")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userVerifiedSignupWithPincode.getMail())
                    .param("pincode", "99999999")
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
    public void whenAuthenticateByPincodeWithUserEmailNotConfirmed_thenReturn401()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                post("/login/pincode")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", userNotVerifiedSignupWithPincode.getMail())
                    .param("pincode", userNotVerifiedSignupWithPincode.getPincode())
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
    public void whenAuthenticateByPincodeWithUnknownUser_thenReturn401()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                post("/login/pincode")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("mail", "unknown@julb.io")
                    .param("pincode", "00000000")
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
