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

package me.julb.applications.announcement.services.impl;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import me.julb.applications.announcement.configurations.properties.ApplicationProperties;
import me.julb.applications.announcement.repositories.AnnouncementRepository;
import me.julb.applications.announcement.services.AnnouncementService;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.core.context.ContextConstants;

import reactor.util.context.Context;

/**
 * The auto-cleaning service implementation.
 * <br>
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

            AtomicLong count = new AtomicLong();

            // Find announcements.
            announcementRepository.findByVisibilityDateTime_ToLessThanEqualOrderByTmAsc(dateTimeThresold).flatMap(expiredAnnouncement -> {
                return announcementService.delete(expiredAnnouncement.getId())
                    .doOnTerminate(count::incrementAndGet)
                    .contextWrite(Context.of(ContextConstants.TRADEMARK, expiredAnnouncement.getTm()));
            })
            .doOnComplete(() -> {
                long numberOfElements = count.get();
                if (numberOfElements > 0) {
                    LOGGER.info("{} expired announcements have been successfully deleted.", count.get());
                } else {
                    LOGGER.info("No expired announcements to delete.");
                }
                LOGGER.info("END - Operation completed.");
            })
            .subscribe();
        } else {
            LOGGER.info("The cleaning threshold is set to <{}> so skipping the autoclean.", thresholdInDays);
            LOGGER.info("END - Operation completed.");
        }
    }
}
