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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.ping.configurations.properties.ApplicationProperties;
import me.julb.applications.ping.configurations.properties.TargetProperties;
import me.julb.applications.ping.consumers.ApiPingTargetFeignClient;
import me.julb.applications.ping.services.PingTargetService;
import me.julb.applications.ping.services.dto.PingTargetAllDTO;
import me.julb.applications.ping.services.dto.PingTargetDTO;
import me.julb.library.utility.enums.HealthStatus;
import me.julb.library.utility.exceptions.AbstractRemoteSystemException;

import reactor.core.publisher.Mono;

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
     * The targetFeignClients attribute.
     */
    @Autowired
    private Map<String, ApiPingTargetFeignClient> apiPingTargetFeignClients;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<PingTargetAllDTO> pingAll() {
        LOGGER.debug("Pinging all targets.");

        PingTargetAllDTO pingAll = new PingTargetAllDTO();

        // Local metadata
        pingAll.getMetadata().putAll(applicationProperties.getLocal().getMetadata());

        // Ping targets.
        for (TargetProperties target : applicationProperties.getTargets()) {
            LOGGER.debug("> Pinging URL <{}>.", target.getEndpoint().getUrl());

            // Find target feign client.
            ApiPingTargetFeignClient apiPingTargetFeignClient = apiPingTargetFeignClients.get(target.getId());

            PingTargetDTO pingTarget = new PingTargetDTO();
            pingTarget.getMetadata().putAll(target.getMetadata());

            // Stop watch.
            StopWatch createStarted = StopWatch.createStarted();
            try {
                // Perform ping.
                apiPingTargetFeignClient.ping();

                // Perform the call.
                pingTarget.setResponseStatusCode(HttpStatus.OK.value());
                pingTarget.setStatus(HealthStatus.UP);

                LOGGER.debug("> Ping successful.");
            } catch (AbstractRemoteSystemException e) {
                // Call has failed.
                pingTarget.setResponseStatusCode(e.getHttpResponseStatusCode());
                pingTarget.setStatus(HealthStatus.DOWN);

                LOGGER.error("> Ping failed to <{}>.", target.getEndpoint().getUrl());
                LOGGER.error("> Stacktrace is below.", e);
            } finally {
                // Stop the watch and get time.
                createStarted.stop();
                pingTarget.setResponseTimeMilliseconds(createStarted.getTime());
            }

            // Add to result.
            pingAll.getRemotes().add(pingTarget);
        }

        // All target pinged.
        LOGGER.debug("All targets have been pinged.");

        return Mono.just(pingAll);
    }
}
