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

package me.julb.applications.announcement.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import me.julb.applications.announcement.entities.AnnouncementEntity;
import me.julb.applications.announcement.services.AnnouncementService;
import me.julb.applications.announcement.services.dto.AnnouncementCreationDTO;
import me.julb.applications.announcement.services.dto.AnnouncementLevel;
import me.julb.library.dto.simple.content.LargeContentCreationDTO;
import me.julb.library.dto.simple.interval.date.DateTimeIntervalCreationDTO;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.persistence.mongodb.test.base.AbstractMongoDbBaseTest;
import me.julb.springbootstarter.test.security.annotations.WithMockUser;

/**
 * Unit test for the {@link CollectController} class.
 * <P>
 * @author Julb.
 */
@Import(TestChannelBinderConfiguration.class)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = AnnouncementControllerTest.Initializer.class)
@Testcontainers
public class AnnouncementControllerTest extends AbstractMongoDbBaseTest {

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
     * The announcement service.
     */
    @Autowired
    private AnnouncementService announcementService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupData() {
        // Create announcement.
        AnnouncementCreationDTO creationDTO = new AnnouncementCreationDTO();
        creationDTO.setLevel(AnnouncementLevel.INFO);
        creationDTO.setLocalizedMessage(new HashMap<String, LargeContentCreationDTO>());
        LargeContentCreationDTO value = new LargeContentCreationDTO();
        value.setMimeType(MediaType.TEXT_PLAIN_VALUE);
        value.setContent("Hello !");
        creationDTO.getLocalizedMessage().put("fr", value);
        creationDTO.setTags(new TreeSet<>(Set.of("tag1", "tag2", "tag3")));
        creationDTO.setVisibilityDateTime(new DateTimeIntervalCreationDTO());
        creationDTO.getVisibilityDateTime().setFrom(DateUtility.dateTimeNow());
        creationDTO.getVisibilityDateTime().setTo(DateUtility.dateTimePlus(1, ChronoUnit.DAYS));
        announcementService.create(creationDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getEntityClasses() {
        return new Class<?>[] {AnnouncementEntity.class};
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenFindAll_thenReturn200()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(
                get("/announcements")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isOk())
            .andDo((handler) -> {
                Assertions.assertEquals(1, 1);
            });
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
