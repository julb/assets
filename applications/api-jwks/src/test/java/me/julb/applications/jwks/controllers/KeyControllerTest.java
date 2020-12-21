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

package me.julb.applications.jwks.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Unit test for the {@link KeyController} class.
 * <P>
 * @author Julb.
 */
@AutoConfigureMockMvc
public class KeyControllerTest extends AbstractBaseTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetAsymmetric_thenReturnValidKeyset()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/asymmetric-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("keys", hasSize(2)));
        //@formatter:on

        //@formatter:off
        mockMvc
            .perform(get("/keysets/asymmetric-key/keys").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("keys", hasSize(2)));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetAsymmetric_thenReturnValidKey()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/asymmetric-key/keys/first-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("kty", is("RSA")))
            .andExpect(jsonPath("use", is("sig")))
            .andExpect(jsonPath("kid", is("first-key")))
            .andExpect(jsonPath("alg", is("RS384")));
        //@formatter:on
        //@formatter:off
        mockMvc
            .perform(get("/keysets/asymmetric-key/keys/second-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("kty", is("EC")))
            .andExpect(jsonPath("use", is("sig")))
            .andExpect(jsonPath("kid", is("second-key")))
            .andExpect(jsonPath("alg", is("ES384")));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingUnknownKeyOfKeysetAsymmetric_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/asymmetric-key/keys/unknown-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetSymmetricPublicOnly_thenReturnEmptyKeyset()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-pub").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("keys", hasSize(0)));
        //@formatter:on

        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-pub/keys").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("keys", hasSize(0)));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeyOfKeysetSymmetricPublicOnly_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-pub/keys/first-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetSymmetric_thenReturnValidKeyset()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-prv").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("keys", hasSize(1)));
        //@formatter:on

        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-prv/keys").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("keys", hasSize(1)));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetSymmetric_thenReturnValidKey()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-prv/keys/first-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("kty", is("oct")))
            .andExpect(jsonPath("use", is("enc")))
            .andExpect(jsonPath("kid", is("first-key")))
            .andExpect(jsonPath("alg", is("DIR")));;
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingUnknownKeyOfKeysetSymmetric_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/symmetric-key-prv/keys/unknown-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfUnknownKeyset_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        mockMvc
            .perform(get("/keysets/unknown-key").contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
        //@formatter:on
    }

}
