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

package me.julb.applications.webnotification.services;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import me.julb.applications.webnotification.services.dto.WebNotificationDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The web notification service.
 * <br>
 * @author Julb.
 */
public interface MyWebNotificationService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the available web notifications (paged).
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return a paged list of web notifications.
     */
    Page<WebNotificationDTO> findAll(@NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Gets a web notification through its ID.
     * @param id the web notification identifier.
     * @return the web notification.
     */
    WebNotificationDTO findOne(@NotNull @Identifier String id);

    // ------------------------------------------ Write methods.

    /**
     * Mark all connected user notifications as read.
     */
    void markAllAsRead();

    /**
     * Marks a connected user web notification as read.
     * @param id the web notification identifier.
     * @return the updated web notification.
     */
    WebNotificationDTO markAsRead(@NotNull @Identifier String id);

    /**
     * Deletes all user web notifications.
     */
    void deleteAll();

    /**
     * Deletes a web notification.
     * @param id the id of the web notification to delete.
     */
    void delete(@NotNull @Identifier String id);

}
