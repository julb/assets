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

package me.julb.applications.configurations.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import me.julb.applications.configuration.controllers.PropertiesController;
import me.julb.springbootstarter.test.base.AbstractBaseTest;
import me.julb.springbootstarter.test.security.annotations.WithMockUser;

/**
 * Unit test for the {@link PropertiesController} class.
 * <br>
 * @author Julb.
 */
@AutoConfigureMockMvc
public class PropertiesControllerTest extends AbstractBaseTest {

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
    public void whenGetProperties_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                get("/properties").contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isOk())
            .andDo((result) -> {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, String>>() {});
                Assertions.assertEquals(3, map.size());
                Assertions.assertEquals("Testio-Value-a", map.get("a.property"));
                Assertions.assertEquals("Testio-Value-b", map.get("b.property"));
                Assertions.assertEquals("$$REDACTED$$", map.get("c.property"));
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenGetPropertiesWithPrefix_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                get("/properties?prefix=a").contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isOk())
            .andDo((result) -> {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, String>>() {});
                Assertions.assertEquals(1, map.size());
                Assertions.assertEquals("Testio-Value-a", map.get("a.property"));
            });
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    @WithMockUser
    public void whenGetPropertiesWithUnexistingPrefix_thenReturn200()
        throws Exception {

        //@formatter:off
        mockMvc
            .perform(
                get("/properties?prefix=zzz").contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().isOk())
            .andDo((result) -> {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, String>>() {});
                Assertions.assertEquals(0, map.size());
            });
        //@formatter:on
    }
}
