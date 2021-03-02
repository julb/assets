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

package me.julb.applications.webnotification.services.impl;

import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import me.julb.applications.webnotification.configurations.properties.ApplicationProperties;
import me.julb.applications.webnotification.entities.WebNotificationEntity;
import me.julb.applications.webnotification.repositories.WebNotificationRepository;
import me.julb.applications.webnotification.services.MyWebNotificationService;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;

/**
 * The auto-cleaning service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Slf4j
public class AutoCleaningServiceImpl {

    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * The web notification repository.
     */
    @Autowired
    private WebNotificationRepository webNotificationRepository;

    /**
     * The web notification service.
     */
    @Autowired
    private MyWebNotificationService webNotificationService;

    /**
     * Launch the automatic cleaning of the web notifications.
     */
    @Scheduled(fixedRateString = "P1D")
    public void launchAutocleanWebNotifications() {
        LOGGER.info("START - Cleaning of the web notifications.");

        Long thresholdInDays = applicationProperties.getAutoCleaning().getExpiryThresholdInDays();
        if (thresholdInDays > 0) {
            LOGGER.info("The cleaning threshold in days is <{}>.", thresholdInDays);

            // Get the date time threshold.
            String dateTimeThresold = DateUtility.dateTimeMinus(thresholdInDays.intValue(), ChronoUnit.DAYS);

            // Find web notifications.
            List<WebNotificationEntity> expiredWebNotifications = webNotificationRepository.findByExpiryDateTimeLessThanEqualOrderByTmAsc(dateTimeThresold);

            if (expiredWebNotifications.isEmpty()) {
                LOGGER.info("No web notifications to delete.");

            } else {
                LOGGER.info("There are <{}> web notifications to delete.", expiredWebNotifications.size());

                // Cleaning
                for (WebNotificationEntity webNotification : expiredWebNotifications) {
                    TrademarkContextHolder.setTrademark(webNotification.getTm());
                    webNotificationService.delete(webNotification.getId());
                }
                TrademarkContextHolder.unsetTrademark();

                LOGGER.info("The web notifications have been successfully deleted.");
            }

        } else {
            LOGGER.info("The cleaning threshold is set to <{}> so skipping the autoclean.", thresholdInDays);
        }

        LOGGER.info("END - Operation completed.");
    }
}
