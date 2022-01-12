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

package me.julb.applications.jwks.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import me.julb.library.utility.constants.CustomHttpHeaders;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Unit test for the {@link KeyController} class.
 * <br>
 * @author Julb.
 */
@AutoConfigureWebTestClient
public class KeyControllerTest extends AbstractBaseTest {

    /**
     * The mock MVC.
     */
    @Autowired
    private WebTestClient webTestClient;

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetAsymmetric_thenReturnValidKeyset()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/asymmetric-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.keys.length()").isEqualTo(2);
        //@formatter:on

        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/asymmetric-key/keys")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.keys.length()").isEqualTo(2);
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetAsymmetric_thenReturnValidKey()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/asymmetric-key/keys/first-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.kty").isEqualTo("RSA")
                .jsonPath("$.use").isEqualTo("sig")
                .jsonPath("$.kid").isEqualTo("first-key")
                .jsonPath("$.alg").isEqualTo("RS384");
        //@formatter:on
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/asymmetric-key/keys/second-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.kty").isEqualTo("EC")
                .jsonPath("$.use").isEqualTo("sig")
                .jsonPath("$.kid").isEqualTo("second-key")
                .jsonPath("$.alg").isEqualTo("ES384");
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingUnknownKeyOfKeysetAsymmetric_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/asymmetric-key/keys/unknown-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isNotFound();
        //  @formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetSymmetricPublicOnly_thenReturnEmptyKeyset()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-pub")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.keys.length()").isEqualTo(0);
        //@formatter:on

        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-pub/keys")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.keys.length()").isEqualTo(0);
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeyOfKeysetSymmetricPublicOnly_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-pub/keys/first-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isNotFound();
        //  @formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetSymmetric_thenReturnValidKeyset()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-prv")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.keys.length()").isEqualTo(1);
        //@formatter:on

        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-prv/keys")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.keys.length()").isEqualTo(1);
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfKeysetSymmetric_thenReturnValidKey()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-prv/keys/first-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isOk()
            .expectBody()
                .jsonPath("$.kty").isEqualTo("oct")
                .jsonPath("$.use").isEqualTo("enc")
                .jsonPath("$.kid").isEqualTo("first-key")
                .jsonPath("$.alg").isEqualTo("DIR");
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingUnknownKeyOfKeysetSymmetric_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/symmetric-key-prv/keys/unknown-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isNotFound();
        //  @formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenGettingKeysOfUnknownKeyset_thenReturnNotFound()
        throws Exception {
        //@formatter:off
        webTestClient
            .get()
            .uri("/keysets/unknown-key")
            .accept(MediaType.APPLICATION_JSON)
            .header(CustomHttpHeaders.X_JULB_TM, TM)
            .exchange()
            .expectStatus()
                .isNotFound();
        //  @formatter:on
    }

}
