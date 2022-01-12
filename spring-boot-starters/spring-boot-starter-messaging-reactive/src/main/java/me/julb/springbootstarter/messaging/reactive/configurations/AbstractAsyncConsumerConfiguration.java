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

package me.julb.springbootstarter.messaging.reactive.configurations;


import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import me.julb.library.dto.messaging.message.AsyncMessageDTO;
import me.julb.library.utility.constants.CustomMessagingHeaders;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * The local async configuration.
 * <br>
 * @author Julb.
 */
@Slf4j
public abstract class AbstractAsyncConsumerConfiguration {

    /**
     * Processes a message.
     * @param <T> the message type.
     * @param message the message to process.
     * @param handler the handler to process the message.
     */
    protected <T extends Message<? extends AsyncMessageDTO<?>>> void onReceive(T message, Function<T, Mono<Void>> handler) {
        handler.apply(message)
            .contextWrite(Context.of(ContextConstants.TRADEMARK, message.getHeaders().get(CustomMessagingHeaders.X_JULB_TM, String.class)))
            .doFirst(() -> LOGGER.debug("Receiving message <{}>.", message.getPayload().getId()))
            .doOnTerminate(() -> LOGGER.debug("Message <{}> processed successfully.", message.getPayload().getId()))
            .block();
    }
}
