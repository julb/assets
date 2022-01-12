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

package me.julb.applications.platformhealth.services.impl;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.services.ICSFeedService;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.library.dto.icsfeed.ICSFeedDTO;
import me.julb.library.dto.icsfeed.ICSFeedEventDTO;
import me.julb.library.dto.simple.interval.date.DateTimeIntervalDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;
import me.julb.springbootstarter.core.messages.MessageSourceService;
import me.julb.springbootstarter.core.rendering.ContentRenderService;

import reactor.core.publisher.Mono;

/**
 * The ICS feed service.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ICSFeedServiceImpl implements ICSFeedService {

    /**
     * The message resource.
     */
    @Autowired
    private MessageSourceService messageSourceService;

    /**
     * The content render service.
     */
    @Autowired
    private ContentRenderService contentRenderService;

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<ICSFeedDTO> buildPlannedMaintenancesFeed() {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            CustomLocaleContext localeContext = ctx.get(ContextConstants.LOCALE);
            Locale locale = localeContext.getLocale();

            // Add items.
            String dateTimeThreshold = DateUtility.dateTimeMinus(Integers.ONE, ChronoUnit.YEARS);

            // Get planned maintenances not DRAFT created in the last 30 days.
            return plannedMaintenanceRepository.findByTmAndLastUpdatedAtGreaterThanEqualAndStatusNotInOrderByLastUpdatedAtDesc(tm, dateTimeThreshold, Arrays.asList(PlannedMaintenanceStatus.DRAFT))
                .map(plannedMaintenance -> {
                    ICSFeedEventDTO event = new ICSFeedEventDTO();
                    event.setId(plannedMaintenance.getId());
                    event.setTitle(contentRenderService.renderToText(tm, localeContext, plannedMaintenance.getLocalizedTitle()));
                    event.setDescription(contentRenderService.renderToText(tm, localeContext, plannedMaintenance.getLocalizedMessage()));
                    event.setDateTimeInterval(new DateTimeIntervalDTO(plannedMaintenance.getSlotDateTime().getFrom(), plannedMaintenance.getSlotDateTime().getTo()));
                    return event;
                })
                .reduceWith(() -> {
                    // Build RSS feed.
                    ICSFeedDTO icsFeed = new ICSFeedDTO();
                    icsFeed.setTitle(messageSourceService.getMessage(tm, "platform-health.ics.feed.planned-maintenances.title", new Object[] {tm}, locale));
                    return icsFeed;
                }, (initial, itemToAdd) -> {
                    initial.getEvents().add(itemToAdd);
                    return initial;
                });
        });
    }
}
