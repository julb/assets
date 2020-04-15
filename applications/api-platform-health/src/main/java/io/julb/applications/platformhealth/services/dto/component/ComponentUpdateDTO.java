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

package io.julb.applications.platformhealth.services.dto.component;

import io.julb.library.dto.simple.content.LargeContentUpdateDTO;
import io.julb.library.dto.simple.content.ShortContentUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to update an component.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class ComponentUpdateDTO {

    //@formatter:off
     /**
     * The localizedName attribute.
     * -- GETTER --
     * Getter for {@link #localizedName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedName} property.
     * @param localizedName the value to set.
     */
     //@formatter:on
    @Schema(description = "Localized name for the component", required = true)
    @NotNull
    @Size(min = 1)
    @Valid
    private Map<String, ShortContentUpdateDTO> localizedName;

   //@formatter:off
    /**
    * The localizedDescription attribute.
    * -- GETTER --
    * Getter for {@link #localizedDescription} property.
    * @return the value.
    * -- SETTER --
    * Setter for {@link #localizedDescription} property.
    * @param localizedDescription the value to set.
    */
    //@formatter:on
    @Schema(description = "Localized description for the component", required = true)
    @NotNull
    @Size(min = 1)
    @Valid
    private Map<String, LargeContentUpdateDTO> localizedDescription;

    //@formatter:off
     /**
     * The position attribute.
     * -- GETTER --
     * Getter for {@link #position} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #position} property.
     * @param position the value to set.
     */
     //@formatter:on
    @Schema(description = "Position for the component", required = true)
    @Min(0)
    @NotNull
    private Integer position;
}
