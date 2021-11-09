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

package me.julb.applications.bookmark.services.dto.object;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import me.julb.applications.bookmark.services.dto.ItemType;
import me.julb.applications.bookmark.services.dto.item.AbstractItemCreationDTO;
import me.julb.library.utility.validator.constraints.HTTPLink;
import me.julb.library.utility.validator.constraints.Identifier;

/**
 * The DTO used to create a object link.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class ObjectLinkCreationDTO extends AbstractItemCreationDTO {

    //@formatter:off
     /**
     * The objectType attribute.
     * -- GETTER --
     * Getter for {@link #objectType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #objectType} property.
     * @param objectType the value to set.
     */
     //@formatter:on
    @Schema(description = "Object type for the link", required = true)
    @NotNull
    @NotBlank
    private String objectType;

    //@formatter:off
     /**
     * The objectId attribute.
     * -- GETTER --
     * Getter for {@link #objectId} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #objectId} property.
     * @param objectId the value to set.
     */
     //@formatter:on
    @Schema(description = "Object ID for the link", required = true)
    @NotNull
    @NotBlank
    @Identifier
    private String objectId;

    //@formatter:off
     /**
     * The url attribute.
     * -- GETTER --
     * Getter for {@link #url} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #url} property.
     * @param url the value to set.
     */
     //@formatter:on
    @Schema(description = "URL for the link", required = true)
    @NotNull
    @NotBlank
    @HTTPLink
    private String url;

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemType getType() {
        return ItemType.OBJECT_LINK;
    }
}
