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

package me.julb.springbootstarter.googlechat.repositories.impl;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import me.julb.library.dto.googlechat.GoogleChatMessageDTO;
import me.julb.springbootstarter.consumer.reactive.decoders.WebClientExceptionConverter;
import me.julb.springbootstarter.googlechat.annotations.ConditionalOnGoogleChatEnabled;
import me.julb.springbootstarter.googlechat.repositories.GoogleChatRepository;

import reactor.core.publisher.Mono;

/**
 * The Google chat repository.
 * <br>
 *
 * @author Julb.
 */
@Component
@Validated
@ConditionalOnGoogleChatEnabled
public class GoogleChatRepositoryImpl implements GoogleChatRepository {

    /**
     * The rest template to invoke Google Chat service.
     */
    @Autowired
    protected WebClient googleChatWebClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTextMessage(@NotNull @NotBlank String spaceId, @NotNull @NotBlank String key, @NotNull @NotBlank String token, String threadKey, @NotNull @NotBlank String textMessage) {
        GoogleChatTextBodyDTO body = new GoogleChatTextBodyDTO(textMessage);

        // Execute call.
        try {
            //@formatter:off
            googleChatWebClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/spaces/{spaceId}/messages")
                    .queryParam("key", key)
                    .queryParam("token", token)
                    .queryParamIfPresent("threadKey", Optional.ofNullable(threadKey))
                    .build(Map.of("spaceId", spaceId))
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
            //@formatter:on
        } catch (WebClientException e) {
            throw WebClientExceptionConverter.convert(e);
        }
    }
}
