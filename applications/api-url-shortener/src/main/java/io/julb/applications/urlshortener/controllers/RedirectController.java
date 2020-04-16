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

package io.julb.applications.urlshortener.controllers;

import io.julb.applications.urlshortener.annotations.ShortURLURI;
import io.julb.applications.urlshortener.entities.LinkEntity;
import io.julb.applications.urlshortener.repositories.LinkRepository;
import io.julb.applications.urlshortener.services.HostService;
import io.julb.applications.urlshortener.services.dto.ShortUrlHitAnalyticsEventDTO;
import io.julb.library.dto.messaging.events.WebAnalyticsAsyncMessageLevel;
import io.julb.library.utility.constants.Chars;
import io.julb.library.utility.constants.CustomHttpHeaders;
import io.julb.library.utility.exceptions.InternalServerErrorException;
import io.julb.library.utility.exceptions.ResourceNotFoundException;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;
import io.julb.springbootstarter.messaging.builders.WebAnalyticsAsyncMessageBuilder;
import io.julb.springbootstarter.messaging.services.IAsyncMessagePosterService;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The rest controller to redirect the user.
 * <P>
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
     * The link repository.
     */
    @Autowired
    private LinkRepository linkRepository;

    /**
     * The async message poster service.
     */
    @Autowired
    private IAsyncMessagePosterService asyncMessagePosterService;

    // ------------------------------------------ Read methods.

    /**
     * Redirects the user to the target URL.
     * @param uri the URI.
     * @param httpServletRequest the HTTP servlet request.
     * @param httpServletResponse the HTTP servlet response.
     */
    @Operation(summary = "redirects the request to the target URL")
    @GetMapping
    @PreAuthorize("permitAll()")
    public void redirect(@RequestParam("uri") @NotNull @NotBlank @ShortURLURI String uri, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            String tm = TrademarkContextHolder.getTrademark();
            String host = httpServletRequest.getServerName();

            // Check if host exists.
            if (!hostService.exists(host)) {
                throw new ResourceNotFoundException(String.class, host);
            }

            // Finds the link.
            LinkEntity link = linkRepository.findByTmAndHostIgnoreCaseAndUriIgnoreCaseAndEnabledIsTrue(tm, host, uri);
            if (link == null) {
                throw new ResourceNotFoundException(LinkEntity.class, Map.<String, String> of("host", host, "uri", uri));
            }

            // Collect the analytics.
            ShortUrlHitAnalyticsEventDTO event = new ShortUrlHitAnalyticsEventDTO();
            event.setDocumentHostname(host);
            event.setDocumentLocation(httpServletRequest.getRequestURL().toString() + Chars.QUESTION_MARK + httpServletRequest.getQueryString());
            event.setDocumentPath(httpServletRequest.getRequestURI() + Chars.QUESTION_MARK + httpServletRequest.getQueryString());
            event.setDocumentReferer(httpServletRequest.getHeader(HttpHeaders.REFERER));
            event.setLinkId(link.getId());
            event.setTm(tm);
            event.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
            event.setUserIpv4(getUserIpv4Address(httpServletRequest));
            event.setUserLanguage(LocaleContextHolder.getLocale().toLanguageTag());

            //@formatter:off
            asyncMessagePosterService.postWebAnalyticsMessage( 
                new WebAnalyticsAsyncMessageBuilder<ShortUrlHitAnalyticsEventDTO>()
                    .level(WebAnalyticsAsyncMessageLevel.INFO)
                    .body(event)
                    .build()
            );
            //@formatter:on

            // Redirect.
            httpServletResponse.sendRedirect(link.getTargetUrl());
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    // ------------------------------------------ Read methods.
    // ------------------------------------------ Private methods.

    /**
     * Gets the user IPV4 address.
     * @param httpServletRequest the request.
     * @return the IPV4 address if available.
     */
    private String getUserIpv4Address(HttpServletRequest httpServletRequest) {
        String ipv4 = httpServletRequest.getHeader(CustomHttpHeaders.X_REAL_IP);
        if (ipv4 == null) {
            ipv4 = httpServletRequest.getHeader(CustomHttpHeaders.X_FORWARDED_FOR);
        }
        if (ipv4 == null) {
            ipv4 = httpServletRequest.getRemoteAddr();
        }
        return ipv4;
    }
}
