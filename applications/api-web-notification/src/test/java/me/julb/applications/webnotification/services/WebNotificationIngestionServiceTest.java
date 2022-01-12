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

package me.julb.applications.webnotification.services;

import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.Iterables;
import org.testcontainers.utility.DockerImageName;

import me.julb.applications.webnotification.entities.WebNotificationEntity;
import me.julb.applications.webnotification.repositories.WebNotificationRepository;
import me.julb.library.dto.notification.events.NotificationKind;
import me.julb.library.dto.notification.events.WebNotificationPriority;
import me.julb.library.dto.simple.user.UserRefDTO;
import me.julb.library.dto.webnotification.WebNotificationMessageDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.persistence.mongodb.reactive.test.base.AbstractMongoDbReactiveBaseTest;
import me.julb.springbootstarter.test.security.annotations.WithMockUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Unit test for the {@link CollectController} class.
 * <br>
 * @author Julb.
 */
@Import(TestChannelBinderConfiguration.class)
@ContextConfiguration(initializers = WebNotificationIngestionServiceTest.Initializer.class)
@Testcontainers
public class WebNotificationIngestionServiceTest extends AbstractMongoDbReactiveBaseTest {

    /**
     * The MongoDB container.
     */
    @Container
    private static final MongoDBContainer MONGODB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo").withTag("4.4"));

    /**
     * The web notification ingestion service.
     */
    @Autowired
    private WebNotificationIngestionService webNotificationIngestionService;

    /**
     * The web notification repository.
     */
    @Autowired
    private WebNotificationRepository webNotificationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Flux<Class<?>> getEntityClasses() {
        return Flux.just(WebNotificationEntity.class);
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenIngestingWebNotificationMessage_thenCreateWebNotifications()
        throws Exception {
        WebNotificationMessageDTO message = new WebNotificationMessageDTO();
        message.setExpiryDateTime(DateUtility.dateTimePlus(3, ChronoUnit.DAYS));
        message.setKind(NotificationKind.TRIGGER_MAIL_VERIFY);
        message.getParameters().put("a", Map.of("b", "c"));
        message.getParameters().put("b", Map.of("d", Map.of("d", "e")));
        message.setPriority(WebNotificationPriority.L2_MEDIUM);

        char character = 'a';
        for (int i = 1; i <= Integers.THREE; i++) {
            // Generate
            String generatedName = StringUtils.repeat(character, Integers.EIGHT);

            // Create user ref.
            UserRefDTO user = new UserRefDTO();
            user.setDisplayName("DP" + generatedName);
            user.setFirstName("FN" + generatedName);
            user.setId(IdentifierUtility.generateId());
            user.setLastName("LN" + generatedName);
            user.setLocale(Locale.getDefault());
            user.setMail(generatedName + "@local");
            user.setE164Number("+33123456789");
            message.getUsers().add(user);

            // Increment.
            character++;
        }

        // Ingest message.
        webNotificationIngestionService.ingest(message).flatMap((x) -> {
            // Check it has been created.
            UserRefDTO firstUser = Iterables.getFirst(message.getUsers(), null);
            return webNotificationRepository.findByTmAndUserId(TM, firstUser.getId()).flatMap(webNotification -> {
                Assertions.assertEquals(message.getKind().category(), webNotification.getBusinessCategory());
                Assertions.assertEquals(message.getKind(), webNotification.getKind());
                Assertions.assertEquals(message.getPriority(), webNotification.getPriority());
                Assertions.assertEquals(false, webNotification.getRead());
                Assertions.assertEquals(message.getParameters().keySet(), webNotification.getParameters().keySet());
                Assertions.assertEquals(firstUser.getDisplayName(), webNotification.getUser().getDisplayName());
                Assertions.assertEquals(firstUser.getFirstName(), webNotification.getUser().getFirstName());
                Assertions.assertEquals(firstUser.getId(), webNotification.getUser().getId());
                Assertions.assertEquals(firstUser.getLastName(), webNotification.getUser().getLastName());
                Assertions.assertEquals(firstUser.getMail(), webNotification.getUser().getMail());
                return Mono.empty();
            }).then();
        })
        .contextWrite(Context.of(ContextConstants.TRADEMARK, TM))
        .block();
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
