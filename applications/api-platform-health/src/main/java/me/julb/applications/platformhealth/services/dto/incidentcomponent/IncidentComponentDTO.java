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

package me.julb.applications.platformhealth.services.dto.incidentcomponent;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.platformhealth.services.dto.component.ComponentDTO;
import me.julb.applications.platformhealth.services.dto.incident.IncidentComponentImpactLevel;

/**
 * The DTO used to return a component impacted by an incident with impact level.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class IncidentComponentDTO {

    //@formatter:off
     /**
     * The component attribute.
     * -- GETTER --
     * Getter for {@link #component} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #component} property.
     * @param component the value to set.
     */
     //@formatter:on
    @Schema(description = "Impacted component")
    private ComponentDTO component;

    //@formatter:off
     /**
     * The impactLevel attribute.
     * -- GETTER --
     * Getter for {@link #impactLevel} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #impactLevel} property.
     * @param impactLevel the value to set.
     */
     //@formatter:on
    @Schema(description = "Impact level on the component")
    private IncidentComponentImpactLevel impactLevel;

}
