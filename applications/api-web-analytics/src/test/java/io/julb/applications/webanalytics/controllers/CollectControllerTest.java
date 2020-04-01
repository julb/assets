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

package io.julb.applications.webanalytics.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.julb.springbootstarter.messaging.processors.AsyncMessageProducerProcessor;
import io.julb.springbootstarter.test.security.annotations.WithMockUser;

import java.util.concurrent.BlockingQueue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import net.javacrumbs.jsonunit.JsonAssert;

/**
 * Unit test for the {@link CollectController} class.
 * <P>
 * @author Julb.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("TEST")
@SpringBootTest
@AutoConfigureMockMvc
public class CollectControllerTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * The processor to get the reference to the producer.
     */
    @Autowired
    private AsyncMessageProducerProcessor asyncMessaceProducerProcessor;

    /**
     * The message collector.
     */
    @Autowired
    private MessageCollector collector;

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenCollect_thenReturn204()
        throws Exception {
        BlockingQueue<Message<?>> queueWithMessages = collector.forChannel(asyncMessaceProducerProcessor.mainProducer());

        //@formatter:off
        mockMvc
            .perform(
                get("/collect")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("an", "test")
                    .queryParam("av", "1.0.0")
                    .queryParam("t", "pageview")
                    .queryParam("ds", "app")
                    .queryParam("uid", "abcdefgh")
                    .queryParam("l", "ERROR")
            )
            .andExpect(status().isNoContent())
            .andDo((handler) -> {
                Assert.assertEquals(1, queueWithMessages.size());
                @SuppressWarnings("unchecked")
                Message<String> message = (Message<String>) queueWithMessages.poll();
                JsonAssert.assertJsonPartEquals("test", message.getPayload(), "body.applicationName");
                JsonAssert.assertJsonPartEquals("1.0.0", message.getPayload(), "body.applicationVersion");
                JsonAssert.assertJsonPartEquals("pageview", message.getPayload(), "body.hitType");
                JsonAssert.assertJsonPartEquals("app", message.getPayload(), "body.dataSource");
                JsonAssert.assertJsonPartEquals("abcdefgh", message.getPayload(), "body.visitorId");
                JsonAssert.assertJsonPartEquals("ERROR", message.getPayload(), "level");
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenCollectWithMissingParams_thenReturn400()
        throws Exception {
        BlockingQueue<Message<?>> queueWithMessages = collector.forChannel(asyncMessaceProducerProcessor.mainProducer());

        //@formatter:off
        mockMvc
            .perform(get("/collect").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest())
            .andDo((handler) -> {
                Assert.assertEquals(0, queueWithMessages.size());
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCollectNotAuthenticated_thenReturn401()
        throws Exception {
        BlockingQueue<Message<?>> queueWithMessages = collector.forChannel(asyncMessaceProducerProcessor.mainProducer());

        //@formatter:off
        mockMvc
            .perform(
                get("/collect")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("an", "test")
                    .queryParam("av", "1.0.0")
                    .queryParam("t", "pageview")
                    .queryParam("ds", "app")
                    .queryParam("uid", "abcdefgh")
                    .queryParam("l", "ERROR")
            )
            .andExpect(status().isUnauthorized())
            .andDo((handler) -> {
                Assert.assertEquals(0, queueWithMessages.size());
            });
        //@formatter:on
    }

}
