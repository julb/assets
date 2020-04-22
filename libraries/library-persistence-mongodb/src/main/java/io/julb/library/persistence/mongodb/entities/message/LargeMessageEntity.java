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

package io.julb.library.persistence.mongodb.entities.message;

import io.julb.library.dto.simple.content.LargeContentCreationDTO;
import io.julb.library.dto.simple.content.LargeContentDTO;
import io.julb.library.dto.simple.content.LargeContentPatchDTO;
import io.julb.library.dto.simple.content.LargeContentUpdateDTO;
import io.julb.library.mapping.annotations.ObjectMappingFactory;
import io.julb.library.utility.interfaces.Contentable;
import io.julb.library.utility.validator.constraints.LargeContent;
import io.julb.library.utility.validator.constraints.MimeType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The message entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = LargeContentCreationDTO.class, patch = LargeContentPatchDTO.class, read = LargeContentDTO.class, update = LargeContentUpdateDTO.class)
@Getter
@Setter
public class LargeMessageEntity implements Contentable {

    //@formatter:off
     /**
     * The mimeType attribute.
     * -- GETTER --
     * Getter for {@link #mimeType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mimeType} property.
     * @param mimeType the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @MimeType
    private String mimeType;

    //@formatter:off
     /**
     * The content attribute.
     * -- GETTER --
     * Getter for {@link #content} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #content} property.
     * @param content the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @LargeContent
    private String content;
}