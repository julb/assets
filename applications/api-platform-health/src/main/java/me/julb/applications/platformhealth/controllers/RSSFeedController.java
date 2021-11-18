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

package me.julb.applications.platformhealth.controllers;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import me.julb.applications.platformhealth.services.RSSFeedService;
import me.julb.springbootstarter.web.views.rssfeed.RSSFeedView;

/**
 * The REST controller to serve RSS feeds.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/rss-feeds", produces = MediaType.APPLICATION_JSON_VALUE)
public class RSSFeedController {

    /**
     * The RSS Feed service.
     */
    @Autowired
    private RSSFeedService rssFeedService;

    /**
     * Gets the RSS feed for the incidents
     * @return the view for the RSS feed.
     */
    @Operation(summary = "get the RSS feed to have updates of the platform incidents")
    @GetMapping("/incidents")
    @PreAuthorize("permitAll()")
    public View getIncidentsFeed() {
        return new RSSFeedView(rssFeedService.buildIncidentsFeed());
    }

    /**
     * Gets the RSS feed for the planned maintenances.
     * @return the view for the RSS feed.
     */
    @Operation(summary = "get the RSS feed to have updates of the platform planned maintenances")
    @GetMapping("/planned-maintenances")
    @PreAuthorize("permitAll()")
    public View getPlannedMaintenancesFeed() {
        return new RSSFeedView(rssFeedService.buildPlannedMaintenancesFeed());
    }
}
