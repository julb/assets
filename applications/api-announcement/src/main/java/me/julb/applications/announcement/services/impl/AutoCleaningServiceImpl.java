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

package me.julb.applications.announcement.services.impl;

import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import me.julb.applications.announcement.configurations.properties.ApplicationProperties;
import me.julb.applications.announcement.entities.AnnouncementEntity;
import me.julb.applications.announcement.repositories.AnnouncementRepository;
import me.julb.applications.announcement.services.AnnouncementService;
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
     * The announcement repository.
     */
    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * The announcement service.
     */
    @Autowired
    private AnnouncementService announcementService;

    /**
     * Launch the automatic cleaning of the announcements.
     */
    @Scheduled(fixedRateString = "P1D")
    public void launchAutocleanAnnouncement() {
        LOGGER.info("START - Cleaning of the announcements.");

        Long thresholdInDays = applicationProperties.getAutoCleaning().getAnnouncementThresholdInDays();
        if (thresholdInDays > 0) {
            LOGGER.info("The cleaning threshold in days is <{}>.", thresholdInDays);

            // Get the date time threshold.
            String dateTimeThresold = DateUtility.dateTimeMinus(thresholdInDays.intValue(), ChronoUnit.DAYS);

            // Find announcements.
            List<AnnouncementEntity> expiredAnnouncements = announcementRepository.findByVisibilityDateTime_ToLessThanEqualOrderByTmAsc(dateTimeThresold);

            if (expiredAnnouncements.isEmpty()) {
                LOGGER.info("No announcements to delete.");

            } else {
                LOGGER.info("There are <{}> announcements to delete.", expiredAnnouncements.size());

                // Cleaning
                for (AnnouncementEntity announcement : expiredAnnouncements) {
                    TrademarkContextHolder.setTrademark(announcement.getTm());
                    announcementService.delete(announcement.getId());
                }
                TrademarkContextHolder.unsetTrademark();

                LOGGER.info("The announcements have been successfully deleted.");
            }

        } else {
            LOGGER.info("The cleaning threshold is set to <{}> so skipping the autoclean.", thresholdInDays);
        }

        LOGGER.info("END - Operation completed.");
    }
}
