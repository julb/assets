/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package me.julb.springbootstarter.templating.configurations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.enums.TemplatingMode;
import me.julb.springbootstarter.templating.annotations.ConditionalOnTemplatingEnabled;
import me.julb.springbootstarter.templating.configurations.beans.TemplatingClasspathProperties;
import me.julb.springbootstarter.templating.configurations.beans.TemplatingFileSystemProperties;
import me.julb.springbootstarter.templating.configurations.beans.TemplatingProperties;

/**
 * The templating configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(TemplatingProperties.class)
@ConditionalOnTemplatingEnabled
public class TemplatingConfiguration {

    /**
     * The templating engine.
     * @param templatingProperties the templating properties
     * @return the templating engine.
     */
    @Bean
    public TemplateEngine templatingEngine(TemplatingProperties templatingProperties) {
        final TemplateEngine templateEngine = new TemplateEngine();
        int order = 1;

        // Add file template resolvers.
        if (templatingProperties.getFileSystem().getEnabled()) {
            Assert.isTrue(StringUtils.isNotBlank(templatingProperties.getFileSystem().getPath()), "templating.fileSystem.path must not be null");
            for (AbstractTemplateResolver templateResolver : fileSystemTemplateResolvers(templatingProperties.getFileSystem())) {
                templateResolver.setOrder(order++);
                templateEngine.addTemplateResolver(templateResolver);
            }
        }

        // Add classpath template resolvers.
        if (templatingProperties.getClasspath().getEnabled()) {
            Assert.isTrue(StringUtils.isNotBlank(templatingProperties.getClasspath().getPath()), "templating.classpath.path must not be null");
            for (AbstractTemplateResolver templateResolver : classpathTemplateResolvers(templatingProperties.getClasspath())) {
                templateResolver.setOrder(order++);
                templateEngine.addTemplateResolver(templateResolver);
            }
        }

        // Assert they are one template resolver
        Assert.notEmpty(templateEngine.getTemplateResolvers(), "At least, one of templating.[classpath|filesystem] should be enabled.");

        return templateEngine;
    }

    /**
     * The file system template resolvers.
     * @param templatingClasspathProperties the properties to configure classpath.
     * @return the template resolvers.
     */
    private Collection<? extends AbstractTemplateResolver> fileSystemTemplateResolvers(TemplatingFileSystemProperties templatingFileSytemProperties) {
        Collection<AbstractTemplateResolver> templateResolvers = new ArrayList<>();

        for (TemplatingMode templatingMode : templatingFileSytemProperties.getModes()) {
            FileTemplateResolver templateResolver = new FileTemplateResolver();
            templateResolver.setPrefix(StringUtils.appendIfMissing(templatingFileSytemProperties.getPath(), Strings.SLASH));
            templateResolver.setSuffix(Chars.DOT + templatingMode.fileExtension());
            templateResolver.setTemplateMode(templatingModeToThymeleafTemplateMode(templatingMode));
            templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            templateResolver.setCacheable(false);
            templateResolvers.add(templateResolver);
        }

        return templateResolvers;
    }

    /**
     * The classpath template resolvers.
     * @param templatingClasspathProperties the properties to configure classpath.
     * @return the template resolvers.
     */
    private Collection<? extends AbstractTemplateResolver> classpathTemplateResolvers(TemplatingClasspathProperties templatingClasspathProperties) {
        Collection<AbstractTemplateResolver> templateResolvers = new ArrayList<>();

        for (TemplatingMode templatingMode : templatingClasspathProperties.getModes()) {
            ClassLoaderTemplateResolver htmlTemplateResolver = new ClassLoaderTemplateResolver();
            htmlTemplateResolver.setPrefix(StringUtils.appendIfMissing(templatingClasspathProperties.getPath(), Strings.SLASH));
            htmlTemplateResolver.setSuffix(Chars.DOT + templatingMode.fileExtension());
            htmlTemplateResolver.setTemplateMode(templatingModeToThymeleafTemplateMode(templatingMode));
            htmlTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            htmlTemplateResolver.setCacheable(false);
            templateResolvers.add(htmlTemplateResolver);
        }

        return templateResolvers;
    }

    /**
     * Gets the thymeleaf template mode.
     * @param mode the templating mode.
     * @return the thymeleaf template mode.
     */
    private static TemplateMode templatingModeToThymeleafTemplateMode(TemplatingMode mode) {
        switch (mode) {
            case HTML:
                return TemplateMode.HTML;
            case TEXT:
                return TemplateMode.TEXT;
            default:
                throw new UnsupportedOperationException(mode.toString());
        }
    }
}
