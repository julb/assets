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

package me.julb.applications.webanalytics.controllers;

import io.swagger.v3.oas.annotations.Operation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.webanalytics.controllers.params.AnalyticsRequestParams;
import me.julb.applications.webanalytics.services.dto.WebAnalyticsEventDTO;
import me.julb.library.dto.http.client.BrowserDTO;
import me.julb.library.dto.http.client.DeviceDTO;
import me.julb.library.dto.http.client.OperatingSystemDTO;
import me.julb.library.utility.http.HttpServletRequestUtility;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.messaging.builders.WebAnalyticsAsyncMessageBuilder;
import me.julb.springbootstarter.messaging.services.AsyncMessagePosterService;

/**
 * The REST controller to collect the analytics.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping("/collect")
public class CollectController {
    /**
     * The async message poster service.
     */
    @Autowired
    private AsyncMessagePosterService asyncMessagePosterService;

    /**
     * This method enables the collection of a navigation event.
     * @param analyticsRequestParams the params containing the navigation information.
     * @param httpServletRequest the HTTP servlet request.
     */
    @Operation(summary = "collect a web analytics event")
    @GetMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void collect(@Valid AnalyticsRequestParams analyticsRequestParams, HttpServletRequest httpServletRequest) {
        // Builds the event.
        WebAnalyticsEventDTO event = new WebAnalyticsEventDTO();
        event.setApplicationName(analyticsRequestParams.getAn());
        event.setApplicationVersion(analyticsRequestParams.getAv());
        event.setDataSource(analyticsRequestParams.getDs());
        event.setDocumentEncoding(analyticsRequestParams.getDe());
        event.setDocumentHostname(analyticsRequestParams.getDh());
        event.setDocumentLocation(analyticsRequestParams.getDl());
        event.setDocumentPath(analyticsRequestParams.getDp());
        event.setDocumentReferer(analyticsRequestParams.getDr());
        event.setDocumentTitle(analyticsRequestParams.getDt());
        event.setGeographicalLocation(analyticsRequestParams.getGeoid());
        event.setHitType(analyticsRequestParams.getT());
        event.setNonInteractionHit(analyticsRequestParams.getNi() != null ? BooleanUtils.toBooleanObject(analyticsRequestParams.getNi()) : Boolean.FALSE);
        event.setQueueTime(analyticsRequestParams.getQt());
        event.setScreenResolution(analyticsRequestParams.getSr());
        event.setScreenColor(analyticsRequestParams.getSd());
        event.setTm(TrademarkContextHolder.getTrademark());
        event.setUserAgent(analyticsRequestParams.getUa());

        // Browser
        BrowserDTO browser = HttpServletRequestUtility.getBrowser(analyticsRequestParams.getUa());
        if (browser != null) {
            event.setUserBrowserName(browser.getName());
            event.setUserBrowserMajorVersion(browser.getMajorVersion());
            event.setUserBrowserVersion(browser.getVersion());
        }

        // Device
        DeviceDTO device = HttpServletRequestUtility.getDevice(analyticsRequestParams.getUa());
        if (device != null) {
            event.setUserDeviceName(device.getType());
        }

        // IP
        event.setUserIpv4Address(HttpServletRequestUtility.getUserIpv4Address(httpServletRequest));

        // OS
        OperatingSystemDTO operatingSystem = HttpServletRequestUtility.getOperatingSystem(analyticsRequestParams.getUa());
        if (operatingSystem != null) {
            event.setUserOperatingSystemName(operatingSystem.getName());
            event.setUserOperatingSystemMajorVersion(operatingSystem.getMajorVersion());
            event.setUserOperatingSystemVersion(operatingSystem.getVersion());
        }

        event.setUserLanguage(analyticsRequestParams.getUl());
        event.setViewportSize(analyticsRequestParams.getVp());
        event.setVisitorId(analyticsRequestParams.getUid());

        //@formatter:off
        asyncMessagePosterService.postWebAnalyticsMessage( 
            new WebAnalyticsAsyncMessageBuilder<WebAnalyticsEventDTO>()
                .level(analyticsRequestParams.getL())
                .body(event)
                .build()
        );
        //@formatter:on
    }

}
