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

package io.julb.applications.bookmark.services.dto.item;

import io.julb.applications.bookmark.services.dto.ItemType;
import io.julb.library.persistence.mongodb.entities.message.ShortMessageEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;

/**
 * The DTO used to patch an item.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public abstract class AbstractItemPatchDTO {

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
    @Schema(description = "Localized name for the item")
    @Valid
    private Map<String, ShortMessageEntity> localizedName = new HashMap<String, ShortMessageEntity>();

    /**
     * Gets the item type.
     * @return the item type.
     */
    public abstract ItemType getType();
}
