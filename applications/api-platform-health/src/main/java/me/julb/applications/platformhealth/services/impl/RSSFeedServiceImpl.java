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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import me.julb.applications.platformhealth.repositories.IncidentHistoryRepository;
import me.julb.applications.platformhealth.repositories.IncidentRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceHistoryRepository;
import me.julb.applications.platformhealth.repositories.PlannedMaintenanceRepository;
import me.julb.applications.platformhealth.services.RSSFeedService;
import me.julb.applications.platformhealth.services.dto.incident.IncidentStatus;
import me.julb.applications.platformhealth.services.dto.plannedmaintenance.PlannedMaintenanceStatus;
import me.julb.library.dto.rssfeed.RSSFeedDTO;
import me.julb.library.dto.rssfeed.RSSFeedItemDTO;
import me.julb.library.utility.constants.HTMLTags;
import me.julb.library.utility.constants.Integers;
import me.julb.library.utility.constants.Strings;
import me.julb.library.utility.date.DateUtility;
import me.julb.springbootstarter.core.context.ContextConstants;
import me.julb.springbootstarter.core.context.configs.ContextConfigSourceService;
import me.julb.springbootstarter.core.localization.CustomLocaleContext;
import me.julb.springbootstarter.core.messages.MessageSourceService;
import me.julb.springbootstarter.core.rendering.ContentRenderService;

import reactor.core.publisher.Mono;

/**
 * The RSS feed service.
 * <br>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class RSSFeedServiceImpl implements RSSFeedService {

    /**
     * The config source service.
     */
    @Autowired
    private ContextConfigSourceService configSourceService;

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
     * The incident repository.
     */
    @Autowired
    private IncidentRepository incidentRepository;

    /**
     * The incident history repository.
     */
    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    /**
     * The planned maintenance repository.
     */
    @Autowired
    private PlannedMaintenanceRepository plannedMaintenanceRepository;

    /**
     * The planned maintenance history repository.
     */
    @Autowired
    private PlannedMaintenanceHistoryRepository plannedMaintenanceHistoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RSSFeedDTO> buildIncidentsFeed() {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            CustomLocaleContext localeContext = ctx.get(ContextConstants.LOCALE);
            Locale locale = localeContext.getLocale();

            // Add items.
            String dateTimeThreshold = DateUtility.dateTimeMinus(Integers.THIRTY, ChronoUnit.DAYS);

            // Get incidents not DRAFT created in the last 30 days.
            return incidentRepository.findByTmAndLastUpdatedAtGreaterThanEqualAndStatusNotInOrderByLastUpdatedAtDesc(tm, dateTimeThreshold, Arrays.asList(IncidentStatus.DRAFT))
                .flatMap(incidentEntity -> {
                    return incidentHistoryRepository.findByTmAndIncidentIdAndStatusNotInOrderByCreatedAtDesc(tm, incidentEntity.getId(), Arrays.asList(IncidentStatus.DRAFT))
                        .reduceWith(() -> {
                            // Get title
                            String localizedTitleContent = contentRenderService.renderToText(tm, localeContext, incidentEntity.getLocalizedTitle());

                            // Get severity
                            String localizedSeverity = messageSourceService.getMessage(tm, "platform-health.rss.feed.incidents.incident.severity." + incidentEntity.getSeverity(), new Object[] {}, locale);

                            // Get message.
                            String localizedMessageContent = contentRenderService.renderToHtml(tm, localeContext, incidentEntity.getLocalizedMessage());

                            RSSFeedItemDTO item = new RSSFeedItemDTO();
                            item.setId(incidentEntity.getId());
                            item.setTitle(messageSourceService.getMessage(tm, "platform-health.rss.feed.incidents.incident.title", new Object[] {localizedTitleContent, localizedSeverity}, locale));
                            item.setLink(configSourceService.getProperty("platform-health.rss.feed.incidents.incident.link", new Object[] {incidentEntity.getId()}));
                            item.getAuthor().setMail(incidentEntity.getUser().getMail());
                            item.getAuthor().setDisplayName(incidentEntity.getUser().getDisplayName());
                            item.setPublishedDateTime(incidentEntity.getLastUpdatedAt());
                            item.getCategories().add(messageSourceService.getMessage(tm, "platform-health.rss.feed.incidents.incident.category", new Object[] {}, locale));
                            item.setHtmlDescription(localizedMessageContent);
                            return item;

                        },  (initial, incidentHistory) -> {
                            // Get message.
                            String localizedHistoryMessageContent = contentRenderService.renderToHtml(tm, localeContext, incidentHistory.getLocalizedMessage());

                            // Append 2 lines
                            StringBuilder sb = new StringBuilder();
                            sb.append(HTMLTags.NEW_LINE);
                            sb.append(HTMLTags.NEW_LINE);

                            // Append message
                            sb.append(HTMLTags.STRONG_LT);
                            sb.append(DateUtility.dateTimeToRfc1123(incidentHistory.getCreatedAt(), LocaleContextHolder.getTimeZone()));
                            sb.append(Strings.SPACE);
                            sb.append(Strings.DASH);
                            sb.append(Strings.SPACE);
                            sb.append(messageSourceService.getMessage(tm, "platform-health.rss.feed.incidents.incident.status." + incidentHistory.getStatus(), new Object[] {}, locale));
                            sb.append(Strings.SEMICOLON);
                            sb.append(HTMLTags.STRONG_RT);
                            sb.append(Strings.SPACE);
                            sb.append(localizedHistoryMessageContent);
                            initial.setHtmlDescription(initial.getHtmlDescription() + sb.toString());
                            return initial;
                        });
            }).reduceWith(() -> {
                // Build RSS feed.
                RSSFeedDTO rssFeed = new RSSFeedDTO();
                rssFeed.setTitle(messageSourceService.getMessage(tm, "platform-health.rss.feed.incidents.title", new Object[] {tm}, locale));
                rssFeed.setDescription(messageSourceService.getMessage(tm, "platform-health.rss.feed.incidents.description", new Object[] {}, locale));
                rssFeed.setLanguage(locale.toLanguageTag());
                rssFeed.setLink(configSourceService.getProperty("platform-health.rss.feed.incidents.link"));
                return rssFeed;
            },  (initial, itemToAdd) -> {
                initial.getItems().add(itemToAdd);
                return initial;
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<RSSFeedDTO> buildPlannedMaintenancesFeed() {
        return Mono.deferContextual(ctx -> {
            String tm = ctx.get(ContextConstants.TRADEMARK);
            CustomLocaleContext localeContext = ctx.get(ContextConstants.LOCALE);
            Locale locale = localeContext.getLocale();

            // Add items.
            String dateTimeThreshold = DateUtility.dateTimeMinus(Integers.THIRTY, ChronoUnit.DAYS);

            // Get planned maintenances not DRAFT created in the last 30 days.
            return plannedMaintenanceRepository.findByTmAndLastUpdatedAtGreaterThanEqualAndStatusNotInOrderByLastUpdatedAtDesc(tm, dateTimeThreshold, Arrays.asList(PlannedMaintenanceStatus.DRAFT))
                .flatMap(plannedMaintenance -> {
                    return plannedMaintenanceHistoryRepository.findByTmAndPlannedMaintenanceIdAndStatusNotInOrderByCreatedAtDesc(tm, plannedMaintenance.getId(), Arrays.asList(PlannedMaintenanceStatus.DRAFT))
                        .reduceWith(() -> {
                            // Get title
                            String localizedTitleContent = contentRenderService.renderToText(tm, localeContext, plannedMaintenance.getLocalizedTitle());

                            // Get complexity
                            String localizedComplexity = messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.planned-maintenance.complexity." + plannedMaintenance.getComplexity(), new Object[] {}, locale);

                            // Get message.
                            String localizedMessageContent = contentRenderService.renderToHtml(tm, localeContext, plannedMaintenance.getLocalizedMessage());

                            RSSFeedItemDTO item = new RSSFeedItemDTO();
                            item.setId(plannedMaintenance.getId());
                            item.setTitle(messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.planned-maintenance.title", new Object[] {localizedTitleContent, localizedComplexity}, locale));
                            item.setLink(configSourceService.getProperty("platform-health.rss.feed.planned-maintenances.planned-maintenance.link", new Object[] {plannedMaintenance.getId()}));
                            item.getAuthor().setMail(plannedMaintenance.getUser().getMail());
                            item.getAuthor().setDisplayName(plannedMaintenance.getUser().getDisplayName());
                            item.setPublishedDateTime(plannedMaintenance.getLastUpdatedAt());
                            item.getCategories().add(messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.planned-maintenance.category", new Object[] {}, locale));
                            
                            StringBuilder sb = new StringBuilder();
                            sb.append(HTMLTags.STRONG_LT);
                            sb.append(
                                messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.planned-maintenance.slot", new Object[] {DateUtility.dateTimeToRfc1123(plannedMaintenance.getSlotDateTime().getFrom(), LocaleContextHolder.getTimeZone()),
                                    DateUtility.dateTimeToRfc1123(plannedMaintenance.getSlotDateTime().getTo(), LocaleContextHolder.getTimeZone())}, locale));
                            sb.append(HTMLTags.STRONG_RT);
                            sb.append(HTMLTags.NEW_LINE);
                            sb.append(HTMLTags.NEW_LINE);
                            sb.append(localizedMessageContent);
                            item.setHtmlDescription(sb.toString());

                            return item;
                        }, (initial, plannedMaintenanceHistory) -> {
                            // Get message.
                            String localizedHistoryMessageContent = contentRenderService.renderToHtml(tm, localeContext, plannedMaintenanceHistory.getLocalizedMessage());

                            // Append 2 lines
                            StringBuilder sb = new StringBuilder();
                            sb.append(HTMLTags.NEW_LINE);
                            sb.append(HTMLTags.NEW_LINE);

                            // Append message
                            sb.append(HTMLTags.STRONG_LT);
                            sb.append(DateUtility.dateTimeToRfc1123(plannedMaintenanceHistory.getCreatedAt(), LocaleContextHolder.getTimeZone()));
                            sb.append(Strings.SPACE);
                            sb.append(Strings.DASH);
                            sb.append(Strings.SPACE);
                            sb.append(messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.planned-maintenance.status." + plannedMaintenanceHistory.getStatus(), new Object[] {}, locale));
                            sb.append(Strings.SEMICOLON);
                            sb.append(HTMLTags.STRONG_RT);
                            sb.append(Strings.SPACE);
                            sb.append(localizedHistoryMessageContent);
                            initial.setHtmlDescription(initial.getHtmlDescription() + sb.toString());
                            return initial;
                        });
            }).reduceWith(() -> {
                // Build RSS feed.
                RSSFeedDTO rssFeed = new RSSFeedDTO();
                rssFeed.setTitle(messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.title", new Object[] {tm}, locale));
                rssFeed.setDescription(messageSourceService.getMessage(tm, "platform-health.rss.feed.planned-maintenances.description", new Object[] {}, locale));
                rssFeed.setLanguage(locale.toLanguageTag());
                rssFeed.setLink(configSourceService.getProperty("platform-health.rss.feed.planned-maintenances.link"));
                return rssFeed;
            },  (initial, itemToAdd) -> {
                initial.getItems().add(itemToAdd);
                return initial;
            });
        });
    }
}
