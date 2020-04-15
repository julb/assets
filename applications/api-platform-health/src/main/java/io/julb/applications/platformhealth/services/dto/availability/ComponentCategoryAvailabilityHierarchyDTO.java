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

package io.julb.applications.platformhealth.services.dto.availability;

import io.julb.applications.platformhealth.services.dto.componentcategory.ComponentCategoryDTO;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The component availability.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class ComponentCategoryAvailabilityHierarchyDTO extends ComponentCategoryDTO {

    //@formatter:off
     /**
     * The availabilityStatus attribute.
     * -- GETTER --
     * Getter for {@link #availabilityStatus} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #availabilityStatus} property.
     * @param availabilityStatus the value to set.
     */
     //@formatter:on
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UP;

    //@formatter:off
     /**
     * The incidentsInProgressCount attribute.
     * -- GETTER --
     * Getter for {@link #incidentsInProgressCount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #incidentsInProgressCount} property.
     * @param incidentsInProgressCount the value to set.
     */
     //@formatter:on
    private Long incidentsInProgressCount = 0L;

    //@formatter:off
     /**
     * The plannedMaintenancesInProgressCount attribute.
     * -- GETTER --
     * Getter for {@link #plannedMaintenancesInProgressCount} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #plannedMaintenancesInProgressCount} property.
     * @param plannedMaintenancesInProgressCount the value to set.
     */
     //@formatter:on
    private Long plannedMaintenancesInProgressCount = 0L;

    //@formatter:off
     /**
     * The componentsAvailability attribute.
     * -- GETTER --
     * Getter for {@link #componentsAvailability} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #componentsAvailability} property.
     * @param componentsAvailability the value to set.
     */
     //@formatter:on
    private List<ComponentAvailabilityDTO> componentsAvailability = new ArrayList<>();
}
