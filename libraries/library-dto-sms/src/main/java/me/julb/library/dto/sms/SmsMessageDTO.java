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
package me.julb.library.dto.sms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO to send a SMS.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class SmsMessageDTO {

    //@formatter:off
     /**
     * The e164Number attribute.
     * -- GETTER --
     * Getter for {@link #e164Number} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #e164Number} property.
     * @param e164Number the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String e164Number;

    //@formatter:off
     /**
     * The text attribute.
     * -- GETTER --
     * Getter for {@link #text} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #text} property.
     * @param text the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String text;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        //@formatter:off
        return String.format("To: <%s>, Message: %s", 
            e164Number,
            text
        );
        //@formatter:on
    }

}
