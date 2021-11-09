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

package me.julb.applications.webnotification.controllers;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.webnotification.services.MyWebNotificationService;
import me.julb.applications.webnotification.services.dto.WebNotificationDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The rest controller to manage web notifications.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/my/web-notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class MyWebNotificationController {

    /**
     * The web notification service.
     */
    @Autowired
    private MyWebNotificationService myWebNotificationService;

    // ------------------------------------------ Read methods.

    /**
     * Lists the web notifications.
     * @param searchable the search params.
     * @param pageable the pageable information.
     * @return the web notification paged list.
     */
    @Operation(summary = "list my web notifications")
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public Page<WebNotificationDTO> findAll(Searchable searchable, Pageable pageable) {
        return myWebNotificationService.findAll(searchable, pageable);
    }

    /**
     * Finds a web notification by its ID.
     * @param id the ID of the web notification to fetch.
     * @return the web notification fetched.
     */
    @Operation(summary = "gets one of my web notifications")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public WebNotificationDTO get(@PathVariable @Identifier String id) {
        return myWebNotificationService.findOne(id);
    }

    // ------------------------------------------ Write methods.

    /**
     * Marks a web notification as read.
     */
    @Operation(summary = "mark all my web notifications as read")
    @PutMapping(path = "/attributes/read")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void markAllAsRead() {
        myWebNotificationService.markAllAsRead();
    }

    /**
     * Marks a web notification as read.
     * @param id the ID of the web notification to update.
     * @return the response.
     */
    @Operation(summary = "mark one of my web notifications as read")
    @PutMapping(path = "/{id}/attributes/read")
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public WebNotificationDTO markAsRead(@PathVariable @Identifier String id) {
        return myWebNotificationService.markAsRead(id);
    }

    /**
     * Deletes all my web notifications.
     */
    @Operation(summary = "deletes all my web notifications")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete() {
        myWebNotificationService.deleteAll();
    }

    /**
     * Deletes a web notification.
     * @param id the id of the web notification to delete.
     */
    @Operation(summary = "deletes one of my web notifications")
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('FULLY_AUTHENTICATED')")
    public void delete(@PathVariable String id) {
        myWebNotificationService.delete(id);
    }
    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Overridden methods.
}
