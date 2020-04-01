/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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

package io.julb.springbootstarter.messaging.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.julb.library.utility.date.DateUtility;
import io.julb.library.utility.identifier.IdentifierUtility;
import io.julb.springbootstarter.messaging.processors.AsyncMessageProducerProcessor;
import io.julb.springbootstarter.messaging.services.dto.UnitTestAsyncMessageDTO;

import java.util.concurrent.BlockingQueue;

import javax.validation.ConstraintViolationException;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for class {@link AsyncPosterMessageService}.
 * <P>
 * @author Julb.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AsyncMessagePosterServiceTest {

    /**
     * The async message poster service.
     */
    @Autowired
    private IAsyncMessagePosterService asyncMessagePosterService;

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
    @SuppressWarnings("unchecked")
    @Test
    public void whenPostingMessage_thenMessageSent()
        throws Exception {
        BlockingQueue<Message<?>> queueWithMessages = collector.forChannel(asyncMessaceProducerProcessor.mainProducer());

        // Post message.
        UnitTestAsyncMessageDTO dto = new UnitTestAsyncMessageDTO();
        dto.setId(IdentifierUtility.generateId());
        dto.setVersion(1);
        dto.setTimestamp(DateUtility.dateTimeNow());
        dto.setAdditionalValue("some.value");
        asyncMessagePosterService.postMessage("routing.key.value", dto);

        Message<String> message = (Message<String>) queueWithMessages.poll();
        JSONObject payload = new JSONObject(message.getPayload());

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
    @Test(expected = ConstraintViolationException.class)
    public void whenPostingNullRoutingKey_thenThrowConstraintViolationException()
        throws Exception {
        UnitTestAsyncMessageDTO dto = new UnitTestAsyncMessageDTO();
        dto.setId(IdentifierUtility.generateId());
        dto.setVersion(1);
        dto.setTimestamp(DateUtility.dateTimeNow());
        dto.setAdditionalValue("some.value");
        asyncMessagePosterService.postMessage(null, dto);
    }

    /**
     * Unit test method.
     */
    @Test(expected = ConstraintViolationException.class)
    public void whenPostingNullAsyncMessage_thenThrowConstraintViolationException()
        throws Exception {
        asyncMessagePosterService.postMessage("some.routing.key", null);
    }
}
