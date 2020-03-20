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

package me.julb.applications.disclaimer.services.dto.disclaimer;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.library.persistence.mongodb.entities.message.ShortMessageEntity;
import me.julb.library.utility.validator.constraints.Code;
import me.julb.library.utility.validator.constraints.Tag;

/**
 * The DTO used to create a disclaimer.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class DisclaimerCreationDTO {

    //@formatter:off
     /**
     * The code attribute.
     * -- GETTER --
     * Getter for {@link #code} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #code} property.
     * @param code the value to set.
     */
     //@formatter:on
    @Schema(description = "Code for the disclaimer", required = true)
    @NotNull
    @NotBlank
    @Code
    private String code;

    //@formatter:off
     /**
     * The version attribute.
     * -- GETTER --
     * Getter for {@link #version} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #version} property.
     * @param version the value to set.
     */
     //@formatter:on
    @Schema(description = "Version of the disclaimer", required = true)
    @NotNull
    @Min(1)
    private Integer version;

    //@formatter:off
     /**
     * The localizedTitle attribute.
     * -- GETTER --
     * Getter for {@link #localizedTitle} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedTitle} property.
     * @param localizedTitle the value to set.
     */
     //@formatter:on
    @Schema(description = "Localized title for the disclaimer", required = true)
    @NotNull
    @Valid
    private Map<String, ShortMessageEntity> localizedTitle;

    //@formatter:off
     /**
     * The localizedDocumentLibraryPath attribute.
     * -- GETTER --
     * Getter for {@link #localizedDocumentLibraryPath} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #localizedDocumentLibraryPath} property.
     * @param localizedDocumentLibraryPath the value to set.
     */
     //@formatter:on
    @Schema(description = "Localized path in the document library for the disclaimer", required = true)
    @NotNull
    @Valid
    private Map<String, String> localizedDocumentLibraryPath;

    //@formatter:off
     /**
     * The tags attribute.
     * -- GETTER --
     * Getter for {@link #tags} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tags} property.
     * @param tags the value to set.
     */
     //@formatter:on
    @Schema(description = "Tags to associate to the disclaimer")
    private SortedSet<@NotNull @Tag String> tags = new TreeSet<String>();
}
