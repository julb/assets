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
package me.julb.springbootstarter.templating.configurations.beans;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import me.julb.library.utility.enums.TemplatingMode;

/**
 * The Templating classpath configuration.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@NoArgsConstructor
public class TemplatingClasspathProperties {

    //@formatter:off
     /**
     * The enabled attribute.
     * -- GETTER --
     * Getter for {@link #enabled} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #enabled} property.
     * @param enabled the value to set.
     */
     //@formatter:on
    private Boolean enabled = false;

    //@formatter:off
     /**
     * The path attribute.
     * -- GETTER --
     * Getter for {@link #path} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #path} property.
     * @param path the value to set.
     */
     //@formatter:on
    private String path;

    //@formatter:off
     /**
     * The modes attribute.
     * -- GETTER --
     * Getter for {@link #modes} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #modes} property.
     * @param modes the value to set.
     */
     //@formatter:on
    private List<TemplatingMode> modes = Arrays.asList(TemplatingMode.HTML, TemplatingMode.TEXT);

}
