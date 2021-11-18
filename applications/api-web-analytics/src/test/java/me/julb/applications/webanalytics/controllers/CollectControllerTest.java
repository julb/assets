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

package me.julb.applications.webanalytics.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.web.servlet.MockMvc;

import me.julb.springbootstarter.test.messaging.base.AbstractMessagingBaseTest;
import me.julb.springbootstarter.test.security.annotations.WithMockUser;
import net.javacrumbs.jsonunit.JsonAssert;

/**
 * Unit test for the {@link CollectController} class.
 * <br>
 * @author Julb.
 */
@AutoConfigureMockMvc
public class CollectControllerTest extends AbstractMessagingBaseTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenCollect_thenReturn204()
        throws Exception {
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
                Message<byte[]> message = output.receive();
                String payload = new String(message.getPayload());
                JsonAssert.assertJsonPartEquals("test", payload, "body.applicationName");
                JsonAssert.assertJsonPartEquals("1.0.0", payload, "body.applicationVersion");
                JsonAssert.assertJsonPartEquals("pageview", payload, "body.hitType");
                JsonAssert.assertJsonPartEquals("app", payload, "body.dataSource");
                JsonAssert.assertJsonPartEquals("abcdefgh", payload, "body.visitorId");
                JsonAssert.assertJsonPartEquals("ERROR", payload, "level");
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
        //@formatter:off
        mockMvc
            .perform(get("/collect").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest())
            .andDo((handler) -> {
                Assertions.assertNull(output.receive());
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenCollectNotAuthenticated_thenReturn401()
        throws Exception {
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
                Assertions.assertNull(output.receive());
            });
        //@formatter:on
    }
}
