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

package me.julb.springbootstarter.templating.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templateresource.ITemplateResource;

import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.library.utility.exceptions.ResourceNotFoundException;
import me.julb.library.utility.validator.constraints.TemplateRelativePath;
import me.julb.springbootstarter.templating.services.TemplatingService;

/**
 * The templating service.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Slf4j
public class TemplatingServiceImpl implements TemplatingService {

    /**
     * The template engine.
     */
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeContentDTO render(Collection<@NotBlank @TemplateRelativePath String> relativePaths, @NotNull Map<String, ?> parameters) {
        // Try multiple path until finding the good one.
        for (String relativePath : relativePaths) {
            try {
                return render(relativePath, parameters);
            } catch (ResourceNotFoundException e) {
                // NOOP : skipping not found to retry with next path.
            }
        }

        // If we are there, it means that we have Not found for all templates.
        throw new ResourceNotFoundException(ITemplateResource.class, Map.<String, String> of("paths", relativePaths.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeContentDTO render(@NotNull @NotBlank @TemplateRelativePath String relativePath, @NotNull Map<String, ?> parameters) {
        try {
            // Trace input.
            LOGGER.debug("Generating report <{}>.", relativePath);
            for (Map.Entry<String, ?> parameterEntry : parameters.entrySet()) {
                LOGGER.debug(" > Parameter <{}> : <{}>.", parameterEntry.getKey(), parameterEntry.getValue());
            }

            // Add context.
            Context ctx = new Context();
            for (Map.Entry<String, ?> parameterEntry : parameters.entrySet()) {
                ctx.setVariable(parameterEntry.getKey(), parameterEntry.getValue());
            }
            String renderedContent = this.templateEngine.process(relativePath, ctx);

            // Return result.
            String mimeType = Files.probeContentType(new File(relativePath).toPath());
            LOGGER.info("Report <{}> generated successfully.", relativePath);
            return new LargeContentDTO(mimeType, renderedContent);
        } catch (TemplateInputException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                throw new ResourceNotFoundException(ITemplateResource.class, Map.<String, String> of("path", relativePath), e);
            } else {
                throw new InternalServerErrorException(e);
            }
        } catch (IOException | TemplateEngineException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
