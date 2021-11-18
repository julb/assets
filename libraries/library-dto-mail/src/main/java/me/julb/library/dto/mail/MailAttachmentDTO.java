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
package me.julb.library.dto.mail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import me.julb.library.utility.validator.constraints.Base64;
import me.julb.library.utility.validator.constraints.MimeType;

/**
 * DTO to send attachement in a email.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"fileName"})
public class MailAttachmentDTO {

    //@formatter:off
     /**
     * The fileName attribute.
     * -- GETTER --
     * Getter for {@link #fileName} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #fileName} property.
     * @param fileName the value to set.
     */
     //@formatter:on
    @NotBlank
    @NotNull
    private String fileName;

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
    @NotBlank
    @NotNull
    @MimeType
    private String mimeType;

    //@formatter:off
     /**
     * The contentBase64 attribute.
     * -- GETTER --
     * Getter for {@link #contentBase64} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #contentBase64} property.
     * @param contentBase64 the value to set.
     */
     //@formatter:on
    @NotBlank
    @NotNull
    @Base64
    private String contentBase64;

}
