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

package me.julb.springbootstarter.templating.notification.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import me.julb.library.dto.notification.events.NotificationDispatchType;
import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.library.dto.simple.content.LargeContentWithSubjectDTO;
import me.julb.library.utility.constants.Chars;
import me.julb.library.utility.exceptions.InternalServerErrorException;
import me.julb.springbootstarter.templating.annotations.ConditionalOnTemplatingEnabled;
import me.julb.springbootstarter.templating.notification.configurations.beans.NotificationTemplatingProperties;
import me.julb.springbootstarter.templating.notification.services.NotificationTemplatingService;
import me.julb.springbootstarter.templating.notification.services.dto.GenerateNotificationContentDTO;
import me.julb.springbootstarter.templating.services.TemplatingService;

/**
 * The templating service.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@ConditionalOnTemplatingEnabled
public class NotificationTemplatingServiceImpl implements NotificationTemplatingService {

    /**
     * The templating service.
     */
    @Autowired
    private TemplatingService templatingService;

    /**
     * The default trademark.
     */
    @Autowired
    private NotificationTemplatingProperties notificationTemplatingProperties;

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeContentDTO render(@NotNull @Valid GenerateNotificationContentDTO generateNotificationContent) {
        // Proceed.
        Collection<String> templateRelativePaths = toResolvablePaths(generateNotificationContent);
        Map<String, Object> parameters = generateNotificationContent.getParameters();
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        // Render content.
        LargeContentDTO render = templatingService.render(templateRelativePaths, parameters);

        // For mail, subject is the first line and content the 3rd line.
        if (generateNotificationContent.getType().hasSubject()) {
            try (BufferedReader reader = new BufferedReader(new StringReader(render.getContent()))) {
                // Read subject
                String subject = reader.readLine();

                // Empty line
                reader.readLine();

                // Content
                StringBuffer contentStringBuffer = new StringBuffer();
                String line = reader.readLine();
                while (line != null) {
                    contentStringBuffer.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }
                String content = contentStringBuffer.toString();

                LargeContentWithSubjectDTO largeContentWithSubject = new LargeContentWithSubjectDTO();
                largeContentWithSubject.setMimeType(render.getMimeType());
                largeContentWithSubject.setSubject(subject);
                largeContentWithSubject.setContent(content);
                return largeContentWithSubject;
            } catch (IOException e) {
                throw new InternalServerErrorException(e);
            }
        } else {
            return render;
        }
    }

    /**
     * Gets the resource paths.
     * @param generateNotificationContent the DTO to generate content.
     * @return the resource paths.
     */
    protected List<String> toResolvablePaths(GenerateNotificationContentDTO generateNotificationContent) {
        // The file name.
        String fileName = StringUtils.join(generateNotificationContent.getName(), Chars.DOT, generateNotificationContent.getType().templatingMode().fileExtension());

        List<String> paths = new ArrayList<String>();
        // 1. Paths with language + country locale.
        //@formatter:off
        paths.add(
            toPath(
                generateNotificationContent.getTm(), 
                StringUtils.join(generateNotificationContent.getLocale().getLanguage(), Chars.DASH, generateNotificationContent.getLocale().getCountry()).toLowerCase(),
                generateNotificationContent.getType(), 
                fileName
            )
        );
        //@formatter:on

        // 2. Paths with language locale.
        //@formatter:off
        paths.add(
            toPath(
                generateNotificationContent.getTm(), 
                generateNotificationContent.getLocale().getLanguage().toLowerCase(),
                generateNotificationContent.getType(), 
                fileName
            )
        );
        //@formatter:on

        if (notificationTemplatingProperties.getDefaultLocale() != null) {
            // 3. Paths with default language + country locale.
            //@formatter:off
            paths.add(
                toPath(
                    generateNotificationContent.getTm(), 
                    StringUtils.join(notificationTemplatingProperties.getDefaultLocale().getLanguage(), Chars.DASH, notificationTemplatingProperties.getDefaultLocale().getCountry()).toLowerCase(),
                    generateNotificationContent.getType(), 
                    fileName
                )
            );
            //@formatter:on

            // 4. Paths with default language locale.
            //@formatter:off
            paths.add(
                toPath(
                    generateNotificationContent.getTm(), 
                    notificationTemplatingProperties.getDefaultLocale().getLanguage().toLowerCase(),
                    generateNotificationContent.getType(), 
                    fileName
                )
            );
            //@formatter:on
        }

        if (StringUtils.isNotBlank(notificationTemplatingProperties.getDefaultTrademark()) && !StringUtils.equalsIgnoreCase(generateNotificationContent.getTm(), notificationTemplatingProperties.getDefaultTrademark())) {
            String defaultTrademark = notificationTemplatingProperties.getDefaultTrademark();

            // 5. Paths with default trademark + language + country locale.
            //@formatter:off
            paths.add(
                toPath(
                    defaultTrademark,
                    StringUtils.join(generateNotificationContent.getLocale().getLanguage(), Chars.DASH, generateNotificationContent.getLocale().getCountry()).toLowerCase(),
                    generateNotificationContent.getType(),
                    fileName
                )
            );
            //@formatter:on

            // 6. Paths with default trademark + language locale.
            //@formatter:off
            paths.add(
                toPath(
                    defaultTrademark,
                    generateNotificationContent.getLocale().getLanguage().toLowerCase(),
                    generateNotificationContent.getType(),
                    fileName
                )
            );
            //@formatter:on

            if (notificationTemplatingProperties.getDefaultLocale() != null) {
                // 7. Paths with default language + country locale.
                //@formatter:off
                paths.add(
                    toPath(
                        defaultTrademark,
                        StringUtils.join(notificationTemplatingProperties.getDefaultLocale().getLanguage(), Chars.DASH, notificationTemplatingProperties.getDefaultLocale().getCountry()).toLowerCase(),
                        generateNotificationContent.getType(), 
                        fileName
                    )
                );
                //@formatter:on

                // 8. Paths with default language locale.
                //@formatter:off
                paths.add(
                    toPath(
                        defaultTrademark,
                        notificationTemplatingProperties.getDefaultLocale().getLanguage().toLowerCase(),
                        generateNotificationContent.getType(), 
                        fileName
                    )
                );
                //@formatter:on
            }
        }
        return paths;
    }

    /**
     * Builds a path for the resource.
     * @param trademark the trademark.
     * @param language the language.
     * @param notificationDispatchType the notification dispatch type.
     * @param fileName the file name.
     * @return the path.
     */
    protected String toPath(String trademark, String language, NotificationDispatchType notificationDispatchType, String fileName) {
        //@formatter:off
        return StringUtils.join(new String[] {
            trademark,
            language.toLowerCase(),
            notificationDispatchType.toString().toLowerCase(),
            fileName,
        }, Chars.SLASH);
        //@formatter:on
    }
}
