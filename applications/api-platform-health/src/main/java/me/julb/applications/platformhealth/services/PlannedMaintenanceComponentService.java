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

package me.julb.applications.platformhealth.services;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import me.julb.applications.platformhealth.services.dto.plannedmaintenancecomponent.PlannedMaintenanceComponentDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The planned maintenance service.
 * <br>
 * @author Julb.
 */
public interface PlannedMaintenanceComponentService {

    // ------------------------------------------ Read methods.

    /**
     * Gets the components linked to the planned maintenance (paged).
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param searchable the searchable information.
     * @param pageable the pageable information.
     * @return a paged list of components.
     */
    Flux<PlannedMaintenanceComponentDTO> findAll(@NotNull @Identifier String plannedMaintenanceId, @NotNull Searchable searchable, @NotNull Pageable pageable);

    /**
     * Check if the given component is associated to the given planned maintenance.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param componentId the component ID.
     * @return <code>true</code> if the link exists, <code>false</code> otherwise.
     */
    Mono<PlannedMaintenanceComponentDTO> findOne(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId);

    // ------------------------------------------ Write methods.

    /**
     * Links the planned maintenance to a component.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param componentId the component ID.
     * @return the link between planned maintenance and component created.
     */
    Mono<PlannedMaintenanceComponentDTO> create(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId);

    /**
     * Remove all links to components for a planned maintenance.
     * @param plannedMaintenanceId the planned maintenance ID.
     */
    Mono<Void> delete(@NotNull @Identifier String plannedMaintenanceId);

    /**
     * Deletes a component.
     * @param plannedMaintenanceId the planned maintenance ID.
     * @param componentId the component ID.
     */
    Mono<Void> delete(@NotNull @Identifier String plannedMaintenanceId, @NotNull @Identifier String componentId);

}
