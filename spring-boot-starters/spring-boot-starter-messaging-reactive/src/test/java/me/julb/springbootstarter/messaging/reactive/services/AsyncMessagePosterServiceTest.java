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

package me.julb.springbootstarter.messaging.reactive.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.validation.ConstraintViolationException;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.messaging.Message;

import me.julb.library.utility.date.DateUtility;
import me.julb.library.utility.identifier.IdentifierUtility;
import me.julb.springbootstarter.messaging.reactive.services.dto.UnitTestAsyncMessageDTO;
import me.julb.springbootstarter.test.messaging.base.AbstractMessagingBaseTest;

/**
 * Unit test for class {@link AsyncPosterMessageService}.
 * <br>
 * @author Julb.
 */
@SpringBootApplication
@ConditionalOnNotWebApplication
public class AsyncMessagePosterServiceTest extends AbstractMessagingBaseTest {

    /**
     * The async message poster service.
     */
    @Autowired
    private AsyncMessagePosterService asyncMessagePosterService;

    /**
     * Unit test method.
     */
    @Test
    public void whenPostingMessage_thenMessageSent()
        throws Exception {

        // Post message.
        UnitTestAsyncMessageDTO dto = new UnitTestAsyncMessageDTO();
        dto.setId(IdentifierUtility.generateId());
        dto.setVersion(1);
        dto.setTimestamp(DateUtility.dateTimeNow());
        dto.setAdditionalValue("some.value");
        asyncMessagePosterService.postMessage("routing.key.value", dto).subscribe();

        Message<byte[]> message = output.receive();

        JSONObject payload = new JSONObject(new String(message.getPayload()));

        //@formatter:off
        assertThat(message.getHeaders().get("routingKey"), is("routing.key.value"));
        assertThat(payload.getString("id"), is(dto.getId()));
        assertThat(payload.getInt("version"), is(dto.getVersion()));
        assertThat(payload.getString("timestamp"), is(dto.getTimestamp()));
        assertThat(payload.getString("additionalValue"), is(dto.getAdditionalValue()));
        //@formatter:on
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenPostingNullRoutingKey_thenThrowConstraintViolationException()
        throws Exception {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            UnitTestAsyncMessageDTO dto = new UnitTestAsyncMessageDTO();
            dto.setId(IdentifierUtility.generateId());
            dto.setVersion(1);
            dto.setTimestamp(DateUtility.dateTimeNow());
            dto.setAdditionalValue("some.value");
            asyncMessagePosterService.postMessage(null, dto);
        });
    }

    /**
     * Unit test method.
     */
    @Test
    public void whenPostingNullAsyncMessage_thenThrowConstraintViolationException()
        throws Exception {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            asyncMessagePosterService.postMessage("some.routing.key", null);
        });
    }
}
