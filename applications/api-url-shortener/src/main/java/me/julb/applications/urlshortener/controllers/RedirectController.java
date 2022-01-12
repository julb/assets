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

package me.julb.applications.urlshortener.controllers;

import java.net.URI;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import me.julb.applications.urlshortener.annotations.ShortURLURI;
import me.julb.applications.urlshortener.repositories.LinkRepository;
import me.julb.applications.urlshortener.services.HostService;
import me.julb.applications.urlshortener.services.LinkService;
import me.julb.applications.urlshortener.services.dto.ShortUrlHitAnalyticsEventDTO;
import me.julb.library.dto.messaging.events.WebAnalyticsAsyncMessageLevel;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;
import me.julb.springbootstarter.messaging.reactive.builders.WebAnalyticsAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.reactive.services.AsyncMessagePosterService;
import me.julb.springbootstarter.web.reactive.utility.ServerHttpRequestUtility;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

/**
 * The rest controller to redirect the user.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/.redirect", produces = MediaType.APPLICATION_JSON_VALUE)
public class RedirectController {

    /**
     * The host service.
     */
    @Autowired
    private HostService hostService;

    /**
     * The link service.
     */
    @Autowired
    private LinkService linkService;

    /**
     * The link repository.
     */
    @Autowired
    private LinkRepository linkRepository;

    /**
     * The async message poster service.
     */
    @Autowired
    private AsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    /**
     * Redirects the user to the target URL.
     * @param uri the URI.
     * @param exchange the exchange.
     */
    @Operation(summary = "redirects the request to the target URL")
    @GetMapping
    @PreAuthorize("permitAll()")
    public Mono<Void> redirect(@RequestParam("uri") @NotNull @NotBlank @ShortURLURI String uri, ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            CustomLocaleContext localeContext = ctx.get(ContextConstants.LOCALE);

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            String host = request.getURI().getHost();

            // Check if host exists.
            return hostService.exists(host).flatMap(hostExists -> {
                if (!hostExists) {
                    return Mono.error(new ResourceNotFoundException(String.class, host));
                }

                return  linkRepository.findByTmAndHostIgnoreCaseAndUriIgnoreCaseAndEnabledIsTrue(tm, host, uri)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException(String.class, host)))
                    .flatMap(link -> {
                        // Collect the analytics.
                        ShortUrlHitAnalyticsEventDTO event = new ShortUrlHitAnalyticsEventDTO();
                        event.setDocumentHostname(host);
                        event.setDocumentLocation(request.getURI().toString());
                        event.setDocumentPath(request.getURI().getPath() + Chars.QUESTION_MARK + request.getURI().getQuery());
                        event.setDocumentReferer(request.getHeaders().getFirst(HttpHeaders.REFERER));
                        event.setLinkId(link.getId());
                        event.setTm(tm);
                        event.setUserAgent(request.getHeaders().getFirst(HttpHeaders.USER_AGENT));
                        event.setUserIpv4Address(ServerHttpRequestUtility.getUserIpAddress(request));
                        event.setUserLanguage(localeContext.getLocale().toLanguageTag());

                        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                        response.getHeaders().setLocation(URI.create(link.getTargetUrl()));

                        //@formatter:off
                        return asyncMessagePosterService.postWebAnalyticsMessage(
                            new WebAnalyticsAsyncMessageBuilder<ShortUrlHitAnalyticsEventDTO>()
                                .level(WebAnalyticsAsyncMessageLevel.INFO)
                                .body(event)
                                .build()
                        )
                        .then(linkService.incrementNumberOfHits(link.getId()))
                        .then(response.setComplete());
                        //@formatter:on
                    });
            });
        });
    }

    // ------------------------------------------ Read methods.
    // ------------------------------------------ Private methods.

}
