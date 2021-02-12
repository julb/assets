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

package me.julb.functions.dto;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.validator.constraints.Trademark;
import me.julb.springbootstarter.templating.configurations.beans.TemplatingMode;

/**
 * The DTO to generate the notification content.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public class GenerateNotificationContentDTO {

    //@formatter:off
     /**
     * The trademark attribute.
     * -- GETTER --
     * Getter for {@link #trademark} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #trademark} property.
     * @param trademark the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Trademark
    private String trademark;

    //@formatter:off
     /**
     * The locale attribute.
     * -- GETTER --
     * Getter for {@link #locale} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #locale} property.
     * @param locale the value to set.
     */
     //@formatter:on
    @NotNull
    @Pattern(regexp = "^[a-z\\-]+$")
    @Size(min = 2, max = 5)
    private String locale;

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    @NotNull
    @Pattern(regexp = "^[a-z0-9][a-z0-9.\\-]*[a-z0-9]$")
    private String name;

    //@formatter:off
     /**
     * The templatingMode attribute.
     * -- GETTER --
     * Getter for {@link #templatingMode} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #templatingMode} property.
     * @param templatingMode the value to set.
     */
     //@formatter:on
    @NotNull
    private TemplatingMode templatingMode;

    //@formatter:off
     /**
     * The parameters attribute.
     * -- GETTER --
     * Getter for {@link #parameters} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #parameters} property.
     * @param parameters the value to set.
     */
     //@formatter:on
    private Map<String, Object> parameters;

    // ------------------------------------------ Utility methods.

    // ------------------------------------------ Read methods.

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Overridden methods.

    /**
     * Gets the resource path.
     * @return the resource path.
     */
    public String toPath() {
        //@formatter:off
        return StringUtils.join(new String[] {
            trademark,
            locale,
            StringUtils.join(name, Chars.DOT, templatingMode.fileExtension()),
        }, Chars.SLASH);
        //@formatter:on
    }
}
