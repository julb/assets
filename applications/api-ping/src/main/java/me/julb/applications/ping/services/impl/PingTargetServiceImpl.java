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

package me.julb.applications.ping.services.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import me.julb.applications.ping.configurations.properties.ApplicationProperties;
import me.julb.applications.ping.services.PingTargetService;
import me.julb.applications.ping.services.dto.PingTargetAllDTO;
import me.julb.applications.ping.services.dto.PingTargetDTO;
import me.julb.library.utility.enums.HealthStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * The service to ping all targets.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
public class PingTargetServiceImpl implements PingTargetService {
    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * The API web client attribute.
     */
    @Autowired
    private Map<String, WebClient> apiPingWebClients;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PingTargetAllDTO> pingAll() {
        return Flux.fromIterable(applicationProperties.getTargets())
                    .doFirst(() -> LOGGER.debug("Pinging all targets."))
                    .flatMap(target -> {
                        return Mono.deferContextual(ctx -> {
                            LOGGER.debug("> Pinging URL <{}>.", target.getEndpoint().getUrl());
                            
                            // Find target feign client.
                            WebClient apiPingTargetWebClient = apiPingWebClients.get(target.getId());

                            // Perform ping.
                            return apiPingTargetWebClient.get()
                                .uri(uriBuilder -> uriBuilder.path("/ping").build())
                                .accept(MediaType.APPLICATION_JSON)
                                .exchangeToMono((clientResponse) -> {
                                    StopWatch stopWatch = ctx.get("stopWatch");
                                    stopWatch.stop();
        
                                    PingTargetDTO pingTarget = new PingTargetDTO();
                                    pingTarget.getMetadata().putAll(target.getMetadata());
                                    pingTarget.setResponseStatusCode(clientResponse.rawStatusCode());
                                    pingTarget.setResponseTimeMilliseconds(stopWatch.getTime());
        
                                    if(clientResponse.statusCode().is2xxSuccessful()) {
                                        LOGGER.debug("> Ping successful.");
                                        pingTarget.setStatus(HealthStatus.UP);
                                    } else {
                                        LOGGER.error("> Ping failed to <{}>.", target.getEndpoint().getUrl());
                                        LOGGER.error("> Stacktrace is below.", clientResponse.createException().subscribe());
                                        pingTarget.setStatus(HealthStatus.DOWN);
                                    }
        
                                    return Mono.just(pingTarget);
                                });
                        }).contextWrite(Context.of("stopWatch", StopWatch.createStarted()));
                    })
                    .reduceWith(() -> {
                        PingTargetAllDTO pingAll = new PingTargetAllDTO();
                        pingAll.getMetadata().putAll(applicationProperties.getLocal().getMetadata());
                        return pingAll;
                    }, (all, target) -> {
                        all.getRemotes().add(target);
                        return all;
                    })
                    .doOnTerminate(() -> LOGGER.debug("All targets have been pinged."));
    }
}
