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

package me.julb.functions;

import java.util.Optional;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import me.julb.library.dto.simple.content.LargeContentDTO;
import me.julb.springbootstarter.templating.notification.services.NotificationTemplatingService;
import me.julb.springbootstarter.templating.notification.services.dto.GenerateNotificationContentDTO;

/**
 * The function to generate the notification content.
 * <br>
 * @author Julb.
 */
@Slf4j
@Validated
public class GenerateNotificationContentFunction implements Function<GenerateNotificationContentDTO, Optional<LargeContentDTO>> {

    /**
     * The templating service.
     */
    @Autowired
    private NotificationTemplatingService notificationTemplatingService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LargeContentDTO> apply(GenerateNotificationContentDTO generateNotificationContent) {
        try {
            LOGGER.debug("Received invokation to generate notification content with body: {}.", generateNotificationContent.toString());
            LargeContentDTO content = notificationTemplatingService.render(generateNotificationContent);
            LOGGER.debug("Method invoked successfully.");
            return Optional.of(content);
        } catch (Exception e) {
            LOGGER.error("Fail to invoke function due to the following exception: {}.", e.getMessage());
            LOGGER.debug("Stracktrace output:", e);
            return Optional.empty();
        }
    }
}
