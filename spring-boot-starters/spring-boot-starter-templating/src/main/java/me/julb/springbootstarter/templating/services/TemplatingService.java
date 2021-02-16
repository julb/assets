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

package me.julb.springbootstarter.templating.services;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.utility.validator.constraints.TemplateRelativePath;

/**
 * The templating service.
 * <P>
 * @author Julb.
 */
public interface TemplatingService {

    /**
     * Render the template with given parameters.
     * @param relativePaths the possible paths of the template to render, ordered.
     * @param parameters the parameters.
     * @return the rendered content.
     */
    LargeContentDTO render(Collection<@NotBlank @TemplateRelativePath String> relativePaths, @NotNull Map<String, ?> parameters);

    /**
     * Render the template with given parameters.
     * @param relativePath the path of the template to render.
     * @param parameters the parameters.
     * @return the rendered content.
     */
    LargeContentDTO render(@NotNull @NotBlank @TemplateRelativePath String relativePath, @NotNull Map<String, ?> parameters);
}
