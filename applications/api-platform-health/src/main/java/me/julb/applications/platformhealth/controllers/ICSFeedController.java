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

package me.julb.applications.platformhealth.controllers;

import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.platformhealth.services.ICSFeedService;
import me.julb.library.dto.icsfeed.ICSFeedDTO;
import me.julb.library.utility.constants.MediaType;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.ics.service.ICSFeedWriterService;

/**
 * The REST controller to serve ICS feeds.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/ics-feeds", produces = "text/calendar")
public class ICSFeedController {

    /**
     * The ICS Feed service.
     */
    @Autowired
    private ICSFeedService icsFeedService;

    /**
     * The ICS Feed writer service.
     */
    @Autowired
    private ICSFeedWriterService icsFeedWriterService;

    /**
     * Writes the ICS feed for the planned maintenances.
     * @param httpServletResponse the HTTP servlet response
     */
    @Operation(summary = "get the ICS feed to have updates of the platform planned maintenances")
    @GetMapping("/planned-maintenances")
    @PreAuthorize("permitAll()")
    public void writePlannedMaintenancesFeed(HttpServletResponse httpServletResponse) {
        try {
            // ICS Feed.
            ICSFeedDTO icsFeed = icsFeedService.buildPlannedMaintenancesFeed();

            // Write feed content.
            httpServletResponse.setContentType(MediaType.TEXT_CALENDAR);
            icsFeedWriterService.write(icsFeed, httpServletResponse.getOutputStream());
            httpServletResponse.flushBuffer();
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
