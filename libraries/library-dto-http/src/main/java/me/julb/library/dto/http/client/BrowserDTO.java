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

package me.julb.library.dto.http.client;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The browser.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "version"})
public class BrowserDTO {

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
    @NotBlank
    private String name;

    //@formatter:off
     /**
     * The majorVersion attribute.
     * -- GETTER --
     * Getter for {@link #majorVersion} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #majorVersion} property.
     * @param majorVersion the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    private String majorVersion;

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
    @NotNull
    @NotBlank
    private String version;
}
